# 04 — Escalar

> **Tiempo estimado:** 60 minutos

Hasta ahora el proyecto es un servidor suelto en la VPC por defecto. En este laboratorio lo vas a llevar a la arquitectura del integrador de T04: **VPC propia, subnets en dos zonas, varias instancias y un balanceador**.

Escribir dos subnets copiando y pegando el bloque funciona. Escribir diez, no. Vas a aprender las dos formas que tiene Terraform de repetir un recurso (`count` y `for_each`), y vas a ver con tus propios ojos por qué elegir mal entre ellas puede destruir infraestructura que no querías tocar.

### Puntos a tener en consideración
- Punto de partida: tu carpeta `~/tf-webapp` como quedó en el lab 03. Si no la tenés, copiá [`../03-Parametrizar/solucion/`](../03-Parametrizar/solucion/).
- Varios pasos piden correr `plan` y **no aplicar**. Respetalo: el plan es el experimento.
- El balanceador tarda 2 o 3 minutos en crearse y otros tantos en destruirse.
- En [`solucion/`](solucion/) está el resultado final.

---

## 01 - Red propia con `count`

Agregá a `variables.tf`:

```hcl
variable "vpc_cidr" {
  description = "Rango de direcciones de la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR de cada subnet pública"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}
```

Agregá a `data.tf`:

```hcl
data "aws_availability_zones" "available" {
  state = "available"
}
```

Creá `network.tf`:

```hcl
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-vpc"
  })
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-igw"
  })
}

resource "aws_subnet" "public" {
  count = length(var.public_subnet_cidrs)

  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-${count.index}"
  })
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-rt"
  })
}
```

Falta asociar cada subnet a la tabla de rutas. Escribí vos el recurso `aws_route_table_association.public` usando `count`. → [documentación](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route_table_association)

```bash
terraform apply -var-file=dev.tfvars
terraform state list
```

¿Cómo aparecen las subnets en el state? ¿Cómo se referencia la segunda?

## 02 - El problema de `count`

Supongamos que hay que eliminar la **primera** subnet. Cambiá el default de la variable:

```hcl
  default = ["10.0.2.0/24"]
```

```bash
terraform plan -var-file=dev.tfvars
```

**No apliques.** Leé el plan:

1. Querías eliminar una subnet. ¿Cuántas va a tocar Terraform, y qué le hace a cada una?
2. ¿Qué subnet termina destruida: la que querías u otra?
3. ¿Qué identifica a cada subnet para Terraform cuando usás `count`?

Si esas subnets tuvieran instancias adentro, las habrías perdido. Volvé el default a sus dos valores.

## 03 - `for_each`: identidad por clave

Reemplazá la variable `public_subnet_cidrs` por un mapa:

```hcl
variable "public_subnets" {
  description = "Subnets públicas: zona de disponibilidad => CIDR"
  type        = map(string)
  default = {
    "us-east-1a" = "10.0.1.0/24"
    "us-east-1b" = "10.0.2.0/24"
  }
}
```

Y en `network.tf`:

```hcl
resource "aws_subnet" "public" {
  for_each = var.public_subnets

  vpc_id                  = aws_vpc.main.id
  availability_zone       = each.key
  cidr_block              = each.value
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-${each.key}"
  })
}

resource "aws_route_table_association" "public" {
  for_each = aws_subnet.public

  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}
```

Borrá el data source `aws_availability_zones`, que ya no se usa. Aplicá y mirá el state:

```bash
terraform apply -var-file=dev.tfvars
terraform state list
```

Ahora repetí el experimento: comentá la línea de `us-east-1a` en el default y corré `terraform plan -var-file=dev.tfvars`. **No apliques.**

1. ¿Cuántas subnets toca esta vez?
2. `aws_route_table_association` usa `for_each = aws_subnet.public`. ¿Qué es `each.value` en ese caso?

Volvé a dejar las dos zonas.

## 04 - Varias instancias en la VPC nueva

Las instancias son intercambiables entre sí: acá `count` es la herramienta correcta. Agregá la variable:

```hcl
variable "instance_count" {
  description = "Cantidad de servidores web detrás del balanceador"
  type        = number
  default     = 2
}
```

Agregá a `locals.tf` una lista ordenada con los IDs de las subnets:

```hcl
  subnet_ids = [for az in sort(keys(var.public_subnets)) : aws_subnet.public[az].id]
```

En `main.tf`:

- Al security group agregale `vpc_id = aws_vpc.main.id`.
- En la instancia, agregá y modificá:

```hcl
  count = var.instance_count

  subnet_id = local.subnet_ids[count.index % length(local.subnet_ids)]

  # dentro del user_data:
  echo "<h1>${var.mensaje}</h1><p>Ambiente: ${var.environment} - Instancia: ${count.index}</p>" > /var/www/html/index.html

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-web-${count.index}"
  })
```

`terraform validate` va a fallar en `outputs.tf`. Corregí los outputs: `instance_ids` tiene que devolver **todos** los IDs y `url` desaparece por ahora (vuelve en el paso siguiente).

Antes de aplicar, respondé con `terraform console -var-file=dev.tfvars`:

```
> 0 % 2
> 1 % 2
> 2 % 2
```

Con 3 instancias y 2 subnets, ¿en qué subnet cae cada una?

## 05 - Balanceador

Creá `alb.tf` copiando el de [`solucion/alb.tf`](solucion/alb.tf) **sin** el último recurso (`aws_lb_target_group_attachment`). Leelo: tiene un security group, el balanceador, el target group y el listener, los mismos cuatro elementos que creaste a mano en T04.

1. Escribí vos `aws_lb_target_group_attachment.web`: tiene que registrar **cada** instancia en el target group. → [documentación](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group_attachment)
2. En el security group `web`, cambiá el ingreso para que solo acepte tráfico **desde el security group del balanceador**: reemplazá `cidr_blocks` por `security_groups = [aws_security_group.alb.id]`.
3. Agregá el output `url` usando `aws_lb.web.dns_name`.

```bash
terraform apply -var-file=dev.tfvars
```

Cuando termine, esperá un minuto a que las instancias pasen el health check y probá:

```bash
for i in 1 2 3 4 5 6; do curl -s $(terraform output -raw url); echo; done
```

Deberías ver respuestas de las dos instancias.

## 06 - `depends_on`: la dependencia que Terraform no ve

Terraform ordena la creación siguiendo las referencias entre recursos. La instancia referencia a la subnet y al security group, así que espera por ellos. Pero el `user_data` hace `dnf install`, y para eso necesita **salida a internet**, que depende de la tabla de rutas. Esa dependencia no está escrita en ningún argumento.

Mirá el grafo actual:

```bash
terraform graph -var-file=dev.tfvars | grep aws_instance
```

¿Aparece `aws_route_table_association` entre las dependencias de la instancia?

Agregá a la instancia:

```hcl
  depends_on = [aws_route_table_association.public]
```

Repetí el comando y compará. ¿Qué podría haber pasado sin esta línea si la instancia arrancaba antes que la ruta?

## 07 - `lifecycle`: en qué orden se reemplaza

Cambiá `mensaje` en `dev.tfvars` y corré `terraform plan -var-file=dev.tfvars`. **No apliques todavía.**

¿Qué símbolo tienen las instancias? ¿Qué pasa primero: se destruye la vieja o se crea la nueva? ¿Qué ve un usuario mientras tanto?

Agregá a la instancia:

```hcl
  lifecycle {
    create_before_destroy = true
  }
```

Volvé a correr `plan`. ¿Cómo cambió el símbolo? Ahora sí, en otra terminal dejá corriendo:

```bash
while true; do curl -s -m 2 $(terraform output -raw url); echo; sleep 1; done
```

y aplicá. Observá en qué momento aparece el mensaje nuevo y si hay errores en el medio.

### 🔎 Investigá
- En el paso 03, pasar de `count` a `for_each` destruyó y recreó las subnets. ¿Cómo se evita con un bloque `moved`? → [Refactoring](https://developer.hashicorp.com/terraform/language/modules/develop/refactoring)
- ¿Qué hace `prevent_destroy = true`? ¿En qué recurso de un ambiente productivo lo pondrías? ¿Qué pasa con `terraform destroy`?
- En el lab 01 agregaste un tag desde la consola y Terraform quiso borrarlo. ¿Cómo le decís que ignore los tags que agrega otro sistema? → [`ignore_changes`](https://developer.hashicorp.com/terraform/language/meta-arguments/lifecycle#ignore_changes)
- Con `create_before_destroy`, ¿hubo errores durante el reemplazo? ¿Qué le falta a esta arquitectura para un despliegue sin corte? Pista: ¿qué recurso usaste en T04 en lugar de instancias sueltas?
- Regla práctica: ¿cuándo `count` y cuándo `for_each`? Escribila en una oración con tus palabras.

## 08 - Limpieza

```bash
terraform destroy -var-file=dev.tfvars
```

Conservá la carpeta `tf-webapp`.

---

## Referencias
- [The `count` Meta-Argument](https://developer.hashicorp.com/terraform/language/meta-arguments/count)
- [The `for_each` Meta-Argument](https://developer.hashicorp.com/terraform/language/meta-arguments/for_each)
- [The `depends_on` Meta-Argument](https://developer.hashicorp.com/terraform/language/meta-arguments/depends_on)
- [The `lifecycle` Meta-Argument](https://developer.hashicorp.com/terraform/language/meta-arguments/lifecycle)
- [`for` Expressions](https://developer.hashicorp.com/terraform/language/expressions/for)
