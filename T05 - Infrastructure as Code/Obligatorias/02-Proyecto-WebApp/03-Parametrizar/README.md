# 03 — Parametrizar

> **Tiempo estimado:** 45 minutos

El código del laboratorio 01 funciona, pero sirve para **un solo** servidor: el nombre, la región, el tipo de instancia y la AMI están fijos. En este laboratorio lo vas a convertir en una configuración que, sin tocar el código, levanta `dev` o `prod` según el archivo de valores que le pases.

No vas a escribir infraestructura nueva. Vas a **refactorizar** la que ya tenés, resolviendo uno por uno los problemas que anotaste al final del lab 01.

### Puntos a tener en consideración
- Punto de partida: tu carpeta `~/tf-webapp`. Si no la tenés, copiá [`../01-Primer-Apply/solucion/`](../01-Primer-Apply/solucion/).
- Después de cada paso corré `terraform validate`. Recién al final hace falta `apply`.
- En [`solucion/`](solucion/) está el resultado final, para comparar.

---

## 01 - Outputs: dejar de buscar en el state

Creá `outputs.tf`:

```hcl
output "url" {
  description = "URL de la aplicación"
  value       = "http://${aws_instance.web.public_ip}"
}
```

Agregá vos un segundo output, `instance_id`. Después:

```bash
terraform apply
terraform output
terraform output -raw url
curl $(terraform output -raw url)
```

¿Para qué sirve `-raw`? Probá el `curl` sin esa opción.

## 02 - Variables: sacar los valores fijos

Creá `variables.tf`:

```hcl
variable "aws_region" {
  description = "Región de AWS donde se crean los recursos"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Nombre del ambiente (dev, test, prod)"
  type        = string
}
```

1. Agregá dos variables más: `instance_type` (default `t3.micro`) y `mensaje` (default `Hola desde Terraform`).
2. Reemplazá en `main.tf` cada valor fijo por su variable (`var.aws_region`, `var.instance_type`). El mensaje va dentro del `user_data`:
   ```hcl
   echo "<h1>${var.mensaje}</h1><p>Ambiente: ${var.environment}</p>" > /var/www/html/index.html
   ```
3. Mové los bloques `terraform` y `provider` a un archivo `versions.tf`.

Corré `terraform plan`. ¿Qué te pregunta y por qué solo eso? Cancelá con `Ctrl+C`.

## 03 - Cuatro formas de asignar un valor

Probá cada una con `terraform plan` y mirá qué valor de `environment` aparece en el plan:

```bash
# 1. Por línea de comandos
terraform plan -var environment=dev

# 2. Por variable de entorno
export TF_VAR_environment=test
terraform plan
```

Creá `terraform.tfvars` con `environment = "prod"` y, **sin** hacer `unset` de la variable de entorno:

```bash
# 3. terraform.tfvars se carga solo
terraform plan

# 4. Todo junto
terraform plan -var environment=dev
```

Completá la tabla con lo que observaste:

| Están definidos | Gana |
|-----------------|------|
| `TF_VAR_environment` y `terraform.tfvars` | |
| `terraform.tfvars` y `-var` | |

Al terminar: `unset TF_VAR_environment` y borrá `terraform.tfvars`.

## 04 - Validación: fallar antes de crear

```bash
terraform plan -var environment=produccion
```

Terraform lo acepta: cualquier string es válido. Agregá dentro de la variable `environment`:

```hcl
  validation {
    condition     = contains(["dev", "test", "prod"], var.environment)
    error_message = "El ambiente debe ser dev, test o prod."
  }
```

Repetí el comando. ¿En qué momento falla: antes o después de consultar a AWS?

## 05 - Locals: no repetirse

El nombre `webapp-web` aparece tres veces y los tags se copian en cada recurso. Creá `locals.tf`:

```hcl
locals {
  name_prefix = "webapp-${var.environment}"

  common_tags = {
    Project     = "webapp"
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}
```

En `main.tf`, usalos en los dos recursos:

```hcl
  name = "${local.name_prefix}-web"     # solo en el security group

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-web"
  })
```

Probá las expresiones sin aplicar nada:

```bash
terraform console -var environment=dev
> local.name_prefix
> merge(local.common_tags, { Name = "prueba" })
> exit
```

¿Cuál es la diferencia entre una variable y un local? ¿Quién puede cambiar el valor de cada uno?

## 06 - Data source: la AMI que no envejece

Creá `data.tf`:

```hcl
data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}
```

Reemplazá el AMI ID fijo por `data.aws_ami.al2023.id` y agregá un output `ami_id` para ver cuál resolvió.

¿En qué se diferencia un bloque `data` de un bloque `resource`? Fijate cómo aparece cada uno en el plan.

## 07 - Un código, dos ambientes

Creá `dev.tfvars`:

```hcl
environment   = "dev"
instance_type = "t3.micro"
mensaje       = "Hola desde dev"
```

Y `prod.tfvars` con `environment = "prod"`, `instance_type = "t3.small"` y otro mensaje.

```bash
terraform fmt
terraform apply -var-file=dev.tfvars
curl $(terraform output -raw url)
```

Sin destruir nada, corré:

```bash
terraform plan -var-file=prod.tfvars
```

**No lo apliques.** Leé el plan con atención:

1. ¿Terraform va a crear un segundo servidor para `prod`, o va a hacer otra cosa?
2. ¿Por qué? Pista: ¿cuántos archivos de state tenés?

Este es el problema que resuelven los laboratorios de state y de módulos.

### 🔎 Investigá
- Agregá una variable `extra_tags` de tipo `map(string)` con default `{}` y sumala a `common_tags` con `merge`. Definila solo en `prod.tfvars`. → [Type Constraints](https://developer.hashicorp.com/terraform/language/expressions/type-constraints)
- Si una variable guarda una contraseña, ¿qué cambia al marcarla `sensitive = true`? ¿Deja de estar en el state? → [Sensitive variables](https://developer.hashicorp.com/terraform/language/values/variables#suppressing-values-in-cli-output)
- ¿Qué archivos de este directorio van a Git y cuáles no? ¿Subirías un `.tfvars`?
- `most_recent = true` hace que la AMI cambie sola cuando Amazon publica una nueva. ¿Qué le pasa a tu instancia en el próximo `apply`? ¿Lo querés en producción?

## 08 - Limpieza

```bash
terraform destroy -var-file=dev.tfvars
```

¿Por qué `destroy` también necesita el archivo de variables?

Conservá la carpeta `tf-webapp` para el próximo laboratorio.

---

## Referencias
- [Input Variables](https://developer.hashicorp.com/terraform/language/values/variables)
- [Variable Definition Precedence](https://developer.hashicorp.com/terraform/language/values/variables#variable-definition-precedence)
- [Output Values](https://developer.hashicorp.com/terraform/language/values/outputs)
- [Local Values](https://developer.hashicorp.com/terraform/language/values/locals)
- [Data Sources](https://developer.hashicorp.com/terraform/language/data-sources)

---

Continuar con [04 — Escalar](../04-Escalar/README.md)
