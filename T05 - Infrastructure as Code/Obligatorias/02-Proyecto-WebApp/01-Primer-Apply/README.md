# 01 — Primer apply

> **Tiempo estimado:** 35 minutos

En T04 levantaste un servidor web **a mano**: consola, clicks, security group, user data. En este laboratorio vas a crear lo mismo **en código**, y vas a usar ese código para entender cómo piensa Terraform: qué compara, qué decide cambiar y dónde guarda lo que sabe.

Este es el primer paso de un proyecto que crece durante todo el módulo. La carpeta que creás hoy (`tf-webapp`) es la que vas a seguir usando en los laboratorios siguientes.

### Puntos a tener en consideración
- Necesitás credenciales vigentes del **Learner Lab** (`aws sts get-caller-identity` tiene que responder).
- Todo se crea en `us-east-1`, en la VPC por defecto.
- Leé **siempre** el plan antes de escribir `yes`. Ese hábito es el objetivo real del lab.
- Si te trabás, en [`solucion/`](solucion/) está el código terminado.

---

## 01 - Preparar el directorio

```bash
terraform version              # 1.10 o superior
aws sts get-caller-identity    # credenciales del Learner Lab vigentes

mkdir ~/tf-webapp && cd ~/tf-webapp
```

## 02 - Escribir la configuración

Buscá el ID de la AMI actual de Amazon Linux 2023:

```bash
aws ssm get-parameter --region us-east-1 \
  --name /aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64 \
  --query Parameter.Value --output text
```

Creá `main.tf` con este contenido, reemplazando `ami-REEMPLAZAR` por el ID que obtuviste:

```hcl
terraform {
  required_version = ">= 1.10"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_security_group" "web" {
  name        = "webapp-web"
  description = "Permite HTTP desde internet"

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Salida a internet"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "webapp-web"
  }
}

resource "aws_instance" "web" {
  ami                    = "ami-REEMPLAZAR"
  instance_type          = "t3.micro"
  vpc_security_group_ids = [aws_security_group.web.id]

  user_data_replace_on_change = true
  user_data                   = <<-EOT
    #!/bin/bash
    dnf install -y httpd
    echo "<h1>Hola desde Terraform</h1>" > /var/www/html/index.html
    systemctl enable --now httpd
  EOT

  tags = {
    Name = "webapp-web"
  }
}
```

Antes de seguir, identificá en el archivo: el bloque de settings, el provider y los dos recursos. ¿En qué línea un recurso **usa** al otro?

## 03 - init

```bash
terraform init
ls -la
```

1. ¿Qué carpeta y qué archivo nuevos aparecieron? ¿Qué hay adentro de cada uno?
2. ¿Cuál de los dos se sube a Git y cuál no?

## 04 - plan

```bash
terraform fmt
terraform validate
terraform plan
```

Todavía no se creó nada. Leé el plan y respondé:

1. ¿Cuántos recursos se van a crear? ¿Qué símbolo los marca?
2. ¿Qué atributos dicen `(known after apply)`? ¿Por qué Terraform no puede saberlos todavía?
3. Escribiste primero el security group y después la instancia. Invertí el orden en el archivo y volvé a correr `plan`. ¿Cambió algo?

## 05 - apply

```bash
terraform apply
```

Revisá el plan que muestra, escribí `yes` y esperá. No definimos ninguna salida, así que hay que buscar la IP en lo que Terraform guardó:

```bash
terraform state list
terraform state show aws_instance.web | grep public_ip
```

Esperá un minuto a que termine el user data y probá:

```bash
curl http://<public_ip>
```

Verificá también en la consola de EC2 que la instancia y el security group existen.

## 06 - El state

```bash
ls -la
terraform show
```

Abrí `terraform.tfstate` con un editor (**sin modificarlo**) y buscá el ID de la instancia.

Ahora corré `terraform plan` otra vez. El resultado tiene que ser `No changes`.

1. ¿Contra qué dos cosas compara Terraform tu código para llegar a esa conclusión?
2. ¿Qué pasaría si borraras `terraform.tfstate` y corrieras `terraform apply`?

## 07 - Tres tipos de cambio

**Cambio en el lugar.** Agregá un tag a la instancia y corré `terraform plan`:

```hcl
  tags = {
    Name  = "webapp-web"
    Owner = "tu-nombre"
  }
```

¿Qué símbolo aparece? Aplicá el cambio y verificá que el ID de la instancia **no cambió**.

**Reemplazo.** Cambiá el texto del `<h1>` en el `user_data` y corré `terraform plan`.

¿Qué símbolo aparece ahora? ¿Qué línea del plan explica por qué? Aplicá, y verificá que el ID y la IP **son otros** y que `curl` devuelve el texto nuevo.

**Cambio por fuera (drift).** Desde la consola de EC2, agregale a la instancia un tag `Manual = si`. Sin tocar el código, corré:

```bash
terraform plan
```

¿Qué propone Terraform? ¿Quién "gana": el código o la consola?

### 🔎 Investigá
- ¿Qué hace exactamente `user_data_replace_on_change`? ¿Qué pasa con un cambio de `user_data` si lo ponés en `false`? → [documentación de `aws_instance`](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance)
- En la documentación de un recurso, ¿cómo sabés si cambiar un argumento actualiza en el lugar o fuerza un reemplazo?
- `terraform plan -out=tfplan` seguido de `terraform apply tfplan`: ¿qué problema resuelve frente a correr `apply` directo? Pensalo para un pipeline.

## 08 - Limpieza

```bash
terraform destroy
```

Leé el plan de destrucción antes de confirmar. ¿En qué orden destruye los recursos y por qué?

**No borres la carpeta `tf-webapp`**: el código sigue en el próximo laboratorio. Lo único que se destruye es la infraestructura.

---

## Qué quedó incómodo

Anotá estos problemas, porque los próximos laboratorios existen para resolverlos:

- El AMI ID está escrito a mano y va a quedar viejo.
- Para ver la IP hubo que buscar dentro del state.
- El nombre `webapp-web`, la región y el tipo de instancia están repetidos y fijos: no se puede levantar un segundo ambiente con este mismo código.

## Referencias
- [Core Terraform Workflow](https://developer.hashicorp.com/terraform/intro/core-workflow)
- [Resource Behavior](https://developer.hashicorp.com/terraform/language/resources/behavior)
- [State](https://developer.hashicorp.com/terraform/language/state)

---

Continuar con [03 — Parametrizar](../03-Parametrizar/README.md)
