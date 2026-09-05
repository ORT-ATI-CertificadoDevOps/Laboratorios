# Demo — Meta-argumentos + GitHub Actions

Tres demos progresivas que cubren `depends_on`, `count`, `for_each` y `lifecycle` integrados con un workflow de GitHub Actions para automatizar el ciclo plan → apply.

> **Prerequisito común:** credenciales AWS configuradas como secrets en el repositorio (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_DEFAULT_REGION`).

---

## Demo 1 — Básico: S3 con `count` y `lifecycle`

**Objetivo:** crear múltiples buckets S3 en un solo bloque, proteger el de producción contra borrado accidental, y disparar el flujo desde GitHub Actions.

### Código Terraform

**`main.tf`**
```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

# count: crea 3 buckets (dev-0, dev-1, dev-2)
resource "aws_s3_bucket" "logs" {
  count  = 3
  bucket = "demo-logs-bucket-${count.index}-${random_id.suffix.hex}"

  tags = {
    Name  = "demo-logs-${count.index}"
    Index = count.index
  }

  lifecycle {
    # count.index == 2 representa "prod" en este ejemplo
    prevent_destroy = false
  }
}

# Bucket de producción separado con prevent_destroy
resource "aws_s3_bucket" "prod" {
  bucket = "demo-prod-bucket-${random_id.suffix.hex}"

  tags = {
    Name        = "demo-prod"
    Environment = "prod"
  }

  lifecycle {
    prevent_destroy = true
  }
}

resource "random_id" "suffix" {
  byte_length = 4
}
```

**`outputs.tf`**
```hcl
output "log_bucket_names" {
  value = [for b in aws_s3_bucket.logs : b.bucket]
}

output "prod_bucket_name" {
  value = aws_s3_bucket.prod.bucket
}
```

### Workflow GitHub Actions

**`.github/workflows/terraform.yml`**
```yaml
name: Terraform

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

permissions:
  contents: read
  pull-requests: write

jobs:
  terraform:
    name: Plan / Apply
    runs-on: ubuntu-latest

    env:
      AWS_ACCESS_KEY_ID:     ${{ secrets.AWS_ACCESS_KEY_ID }}
      AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
      AWS_DEFAULT_REGION:    ${{ secrets.AWS_DEFAULT_REGION }}

    steps:
      - uses: actions/checkout@v4

      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: "1.9.0"

      - name: Init
        run: terraform init

      - name: Validate
        run: terraform validate

      - name: Plan
        id: plan
        run: terraform plan -no-color -out=tfplan

      - name: Comment plan en PR
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v7
        with:
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: '```\n${{ steps.plan.outputs.stdout }}\n```'
            })

      - name: Apply
        if: github.ref == 'refs/heads/main' && github.event_name == 'push'
        run: terraform apply -auto-approve tfplan
```

### Puntos a observar
- `count = 3` genera `aws_s3_bucket.logs[0]`, `[1]`, `[2]` — tres recursos independientes en el state.
- `prevent_destroy = true` en el bucket prod bloquea `terraform destroy`. Para probarlo: intentar `terraform destroy` y ver el error.
- GitHub Actions corre `plan` en cada PR y solo hace `apply` cuando se mergea a `main`.

---

## Demo 2 — Intermedio: Security Groups por entorno con `for_each` y `depends_on`

**Objetivo:** crear security groups diferenciados por entorno usando `for_each`, levantar una instancia EC2 y asignarle una Elastic IP que depende explícitamente del Internet Gateway.

### Código Terraform

**`main.tf`**
```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

# VPC y red base
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = { Name = "demo-vpc" }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  tags = { Name = "demo-subnet-public" }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "demo-igw" }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# for_each: un Security Group por entorno con reglas distintas
resource "aws_security_group" "env" {
  for_each = {
    dev  = { port = 8080, cidr = "0.0.0.0/0" }
    qa   = { port = 8081, cidr = "10.0.0.0/8" }
    prod = { port = 443,  cidr = "0.0.0.0/0" }
  }

  name        = "sg-${each.key}"
  description = "Security group para ${each.key}"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = each.value.port
    to_port     = each.value.port
    protocol    = "tcp"
    cidr_blocks = [each.value.cidr]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Environment = each.key }
}

# EC2 usando el SG de dev
resource "aws_instance" "web" {
  ami                    = "ami-0c02fb55956c7d316" # Amazon Linux 2023 us-east-1
  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.env["dev"].id]

  tags = {
    Name = "demo-web"
  }

  lifecycle {
    ignore_changes = [tags]  # ignora cambios manuales de tags desde la consola
  }
}

# depends_on: la EIP solo se asocia cuando el IGW está activo
resource "aws_eip" "web" {
  instance = aws_instance.web.id
  domain   = "vpc"

  depends_on = [aws_internet_gateway.igw]

  tags = { Name = "demo-eip" }
}
```

**`outputs.tf`**
```hcl
output "security_groups" {
  value = { for k, sg in aws_security_group.env : k => sg.id }
}

output "instance_public_ip" {
  value = aws_eip.web.public_ip
}
```

### Puntos a observar
- `for_each` con map: cada SG se identifica en el state como `aws_security_group.env["dev"]`, `["qa"]`, `["prod"]`. Agregar o quitar un entorno no afecta a los demás.
- `depends_on = [aws_internet_gateway.igw]` en la EIP: sin este, Terraform podría intentar asociarla antes de que el IGW esté listo (la dependencia no es detectable por referencia implícita).
- `ignore_changes = [tags]`: cambiar tags desde la consola no genera drift en el próximo `terraform plan`.

---

## Demo 3 — Avanzado: Infraestructura multi-entorno con todos los meta-argumentos

**Objetivo:** desplegar un conjunto de instancias EC2 por entorno (`for_each`), con réplicas dentro de cada entorno (`count`), dependencias de red explícitas (`depends_on`), comportamiento de ciclo de vida seguro (`lifecycle`) y el workflow completo de GitOps con aprobación manual antes del apply en producción.

### Estructura de archivos

```
.
├── .github/
│   └── workflows/
│       └── terraform.yml
├── main.tf
├── variables.tf
├── outputs.tf
└── terraform.tfvars
```

### Código Terraform

**`variables.tf`**
```hcl
variable "environments" {
  description = "Configuración por entorno"
  type = map(object({
    instance_count = number
    instance_type  = string
    protect        = bool
  }))
  default = {
    dev = {
      instance_count = 1
      instance_type  = "t2.micro"
      protect        = false
    }
    staging = {
      instance_count = 2
      instance_type  = "t2.micro"
      protect        = false
    }
    prod = {
      instance_count = 3
      instance_type  = "t3.small"
      protect        = true
    }
  }
}
```

**`main.tf`**
```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "mi-terraform-state-bucket"
    key            = "demo/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
  }
}

provider "aws" {
  region = "us-east-1"
}

# -------------------------------------------------------------------
# Red compartida
# -------------------------------------------------------------------
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags       = { Name = "demo-multi-env-vpc" }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = "demo-igw" }
}

# for_each: una subnet pública por entorno con CIDR distinto
resource "aws_subnet" "public" {
  for_each = {
    dev     = "10.0.1.0/24"
    staging = "10.0.2.0/24"
    prod    = "10.0.3.0/24"
  }

  vpc_id                  = aws_vpc.main.id
  cidr_block              = each.value
  map_public_ip_on_launch = true

  tags = {
    Name        = "subnet-${each.key}"
    Environment = each.key
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = { Name = "demo-rt-public" }
}

resource "aws_route_table_association" "public" {
  for_each = aws_subnet.public

  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}

# for_each: un Security Group por entorno
resource "aws_security_group" "env" {
  for_each = var.environments

  name        = "sg-${each.key}"
  description = "SG para ${each.key}"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Environment = each.key }

  lifecycle {
    create_before_destroy = true  # evita downtime al actualizar reglas
  }
}

# -------------------------------------------------------------------
# Instancias EC2 por entorno
# Combinación for_each (entorno) + count (réplicas)
# Terraform no soporta for_each + count en el mismo bloque,
# por eso usamos for_each con range() para simular count.
# -------------------------------------------------------------------
locals {
  instances = merge([
    for env, cfg in var.environments : {
      for i in range(cfg.instance_count) :
      "${env}-${i}" => {
        env           = env
        index         = i
        instance_type = cfg.instance_type
        protect       = cfg.protect
        subnet_id     = aws_subnet.public[env].id
        sg_id         = aws_security_group.env[env].id
      }
    }
  ]...)
}

resource "aws_instance" "app" {
  for_each = local.instances

  ami                    = "ami-0c02fb55956c7d316"
  instance_type          = each.value.instance_type
  subnet_id              = each.value.subnet_id
  vpc_security_group_ids = [each.value.sg_id]

  tags = {
    Name        = "app-${each.key}"
    Environment = each.value.env
    Replica     = each.value.index
  }

  lifecycle {
    create_before_destroy = true

    ignore_changes = [
      ami,  # permite actualizar la AMI de forma controlada, no automática
      tags,
    ]
  }
}

# -------------------------------------------------------------------
# Elastic IPs para instancias de prod únicamente
# depends_on: necesita el IGW activo antes de asociarse
# -------------------------------------------------------------------
resource "aws_eip" "prod" {
  for_each = {
    for k, v in local.instances : k => v
    if v.env == "prod"
  }

  instance = aws_instance.app[each.key].id
  domain   = "vpc"

  depends_on = [aws_internet_gateway.igw]

  tags = {
    Name        = "eip-${each.key}"
    Environment = "prod"
  }
}

# -------------------------------------------------------------------
# S3 bucket de logs por entorno con protect en prod
# -------------------------------------------------------------------
resource "aws_s3_bucket" "logs" {
  for_each = var.environments

  bucket = "demo-logs-${each.key}-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name        = "logs-${each.key}"
    Environment = each.key
  }

  lifecycle {
    prevent_destroy = each.value.protect  # true solo en prod
  }
}

data "aws_caller_identity" "current" {}
```

**`outputs.tf`**
```hcl
output "instance_ids" {
  description = "ID de cada instancia por entorno-réplica"
  value       = { for k, v in aws_instance.app : k => v.id }
}

output "prod_eips" {
  description = "IPs elásticas de prod"
  value       = { for k, v in aws_eip.prod : k => v.public_ip }
}

output "subnets" {
  value = { for k, v in aws_subnet.public : k => v.cidr_block }
}
```

### Workflow GitHub Actions con aprobación manual

**`.github/workflows/terraform.yml`**
```yaml
name: Terraform GitOps

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

permissions:
  contents: read
  pull-requests: write

env:
  AWS_ACCESS_KEY_ID:     ${{ secrets.AWS_ACCESS_KEY_ID }}
  AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
  AWS_DEFAULT_REGION:    us-east-1
  TF_VAR_FILE:           terraform.tfvars

jobs:
  # ----------------------------------------------------------------
  # Job 1: plan — corre en PR y en push a main
  # ----------------------------------------------------------------
  plan:
    name: Terraform Plan
    runs-on: ubuntu-latest

    outputs:
      plan_exitcode: ${{ steps.plan.outputs.exitcode }}

    steps:
      - uses: actions/checkout@v4

      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: "1.9.0"

      - name: Init
        run: terraform init

      - name: Validate
        run: terraform validate

      - name: Plan
        id: plan
        run: |
          terraform plan \
            -var-file="${{ env.TF_VAR_FILE }}" \
            -detailed-exitcode \
            -no-color \
            -out=tfplan 2>&1 | tee plan_output.txt
          echo "exitcode=${PIPESTATUS[0]}" >> $GITHUB_OUTPUT

      - name: Subir plan como artefacto
        uses: actions/upload-artifact@v4
        with:
          name: tfplan
          path: tfplan

      - name: Comentar plan en PR
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v7
        with:
          script: |
            const fs = require('fs');
            const plan = fs.readFileSync('plan_output.txt', 'utf8');
            const body = `## Terraform Plan\n\`\`\`\n${plan.slice(0, 65000)}\n\`\`\``;
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body
            });

  # ----------------------------------------------------------------
  # Job 2: apply — solo en push a main, requiere aprobación manual
  # (configurar Environment "production" con reviewers en GitHub)
  # ----------------------------------------------------------------
  apply:
    name: Terraform Apply
    runs-on: ubuntu-latest
    needs: plan
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    environment: production  # <-- requiere aprobación manual en GitHub

    steps:
      - uses: actions/checkout@v4

      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: "1.9.0"

      - name: Init
        run: terraform init

      - name: Descargar plan
        uses: actions/download-artifact@v4
        with:
          name: tfplan

      - name: Apply
        run: terraform apply -auto-approve tfplan
```

### Puntos a observar

**`for_each` + `count` combinados:**
- Terraform no permite usar `for_each` y `count` en el mismo bloque resource. El workaround estándar es generar un mapa via `locals` usando `merge` + `range()` y aplicar `for_each` sobre ese mapa.
- Resultado en el state: `aws_instance.app["dev-0"]`, `aws_instance.app["staging-0"]`, `aws_instance.app["staging-1"]`, `aws_instance.app["prod-0"]`, etc. Cada instancia es direccionable por nombre, no por índice numérico.

**`depends_on` en la EIP:**
- La EIP de prod referencia `aws_instance.app[each.key].id` (dependencia implícita), pero la asociación con la red pública solo funciona si el IGW existe. Terraform no puede inferir esa segunda dependencia, por eso `depends_on = [aws_internet_gateway.igw]` es necesario.

**`lifecycle` en tres recursos distintos:**
- `aws_security_group.env`: `create_before_destroy = true` — si se cambian reglas, se crea el nuevo SG y luego se elimina el viejo. Evita que las instancias queden sin SG durante el cambio.
- `aws_instance.app`: `ignore_changes = [ami, tags]` — permite gestionar actualizaciones de AMI de forma controlada (no en cada apply) y tolera cambios manuales de tags desde la consola.
- `aws_s3_bucket.logs`: `prevent_destroy = each.value.protect` — expresión dinámica: solo el bucket de prod tiene la protección activa.

**GitHub Actions GitOps:**
- `plan` corre en todo PR — el equipo ve el impacto antes de mergear.
- `apply` corre solo en push a `main` y está bloqueado por un Environment de GitHub que requiere aprobación de un reviewer designado.
- El plan se pasa como artefacto entre jobs: el apply ejecuta exactamente lo que fue planificado y aprobado, sin re-planificar.

### Comandos para validar localmente antes de hacer push

```bash
# Init con backend local para pruebas
terraform init -backend=false

# Ver qué instancias se crearían
terraform plan -var-file=terraform.tfvars

# Ver el mapa de instancias generado por locals
terraform console
> local.instances

# Destruir solo dev (sin tocar staging/prod)
terraform destroy -target='aws_instance.app["dev-0"]'
```

---

## Resumen de conceptos cubiertos

| Meta-argumento | Demo 1 | Demo 2 | Demo 3 |
|---|---|---|---|
| `count` | ✅ buckets S3 | — | ✅ vía `range()` en locals |
| `for_each` | — | ✅ SGs por entorno | ✅ subnets, SGs, instancias, buckets |
| `depends_on` | — | ✅ EIP → IGW | ✅ EIP → IGW en prod |
| `lifecycle.prevent_destroy` | ✅ bucket prod | — | ✅ bucket prod dinámico |
| `lifecycle.create_before_destroy` | — | — | ✅ SGs |
| `lifecycle.ignore_changes` | — | ✅ tags EC2 | ✅ ami + tags EC2 |
| GitHub Actions plan | ✅ básico | — | ✅ con comentario en PR |
| GitHub Actions apply | ✅ auto | — | ✅ con aprobación manual |
