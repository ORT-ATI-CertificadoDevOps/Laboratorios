# IaC Security con Checkov

Infrastructure as Code introduce una nueva categoría de vulnerabilidades: **misconfigurations**. Un bucket S3 público accidentalmente, un security group con `0.0.0.0/0` en todos los puertos, una instancia EC2 sin cifrado en el disco, un rol IAM con permisos `*`. Estos errores son fáciles de cometer y difíciles de detectar en una revisión manual.

**Checkov** escanea código Terraform (y otros formatos: CloudFormation, Kubernetes YAML, Dockerfile) buscando estas misconfigurations antes de que se apliquen. Corre en el pipeline sobre cada PR que modifique infraestructura.

## 5.1 Instalar Checkov

```bash
pip install checkov
```

**Verificar:**
```bash
checkov --version
```

## 5.2 Escanear código Terraform

```bash
# Escanear el directorio actual
checkov -d .

# Escanear un archivo específico
checkov -f main.tf

# Solo mostrar checks fallidos
checkov -d . --compact

# Output en JSON
checkov -d . -o json
```

**Ejemplo de output:**

```
Check: CKV_AWS_20: "Ensure the S3 bucket has access control list (ACL) is private"
	FAILED for resource: aws_s3_bucket.mi_bucket
	File: /main.tf:5-12
	Guide: https://docs.bridgecrew.io/docs/s3_1-acl-prohibited

		5 | resource "aws_s3_bucket" "mi_bucket" {
		6 |   bucket = "mi-bucket-ejemplo"
		7 |   acl    = "public-read"    ← problema detectado
		8 | }

Passed checks: 12, Failed checks: 3, Skipped checks: 0
```

## 5.3 Misconfigurations comunes en Terraform/AWS

Checkov verifica cientos de reglas. Las más frecuentes:

| Check | Problema | Recurso AWS |
|-------|----------|-------------|
| CKV_AWS_20 | Bucket S3 público | `aws_s3_bucket` |
| CKV_AWS_23 | Security group con `0.0.0.0/0` en todos los puertos | `aws_security_group` |
| CKV_AWS_8 | Instancia EC2 sin cifrado EBS | `aws_instance` |
| CKV_AWS_33 | KMS sin rotation habilitada | `aws_kms_key` |
| CKV_AWS_41 | Credenciales hardcodeadas en provider | `provider "aws"` |
| CKV_AWS_58 | RDS sin cifrado en reposo | `aws_db_instance` |
| CKV_AWS_91 | ALB sin logging activado | `aws_lb` |

## 5.4 Corregir misconfigurations

**Ejemplo: bucket S3 con acceso público**

```hcl
# ❌ Misconfiguration
resource "aws_s3_bucket" "mi_bucket" {
  bucket = "mi-bucket-ejemplo"
  acl    = "public-read"
}

# ✅ Corregido: bloquear acceso público explícitamente
resource "aws_s3_bucket" "mi_bucket" {
  bucket = "mi-bucket-ejemplo"
}

resource "aws_s3_bucket_public_access_block" "mi_bucket" {
  bucket = aws_s3_bucket.mi_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
```

**Ejemplo: security group restrictivo**

```hcl
# ❌ Misconfiguration: abre todos los puertos al mundo
resource "aws_security_group" "app" {
  ingress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ✅ Corregido: solo el puerto necesario, solo desde el ALB
resource "aws_security_group" "app" {
  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }
}
```

## 5.5 Integrar Checkov en GitHub Actions

```yaml
jobs:
  iac-security:
    name: IaC Security (Checkov)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Run Checkov
        uses: bridgecrewio/checkov-action@master
        with:
          directory: .
          framework: terraform
          soft_fail: false        # falla el job si hay checks fallidos
          output_format: cli
          download_external_modules: true
```

## 5.6 Suprimir falsos positivos

Cuando un check falla pero el contexto justifica la configuración (por ejemplo, un bucket S3 que debe ser público para alojar un sitio web estático), se puede suprimir con un comentario inline en el `.tf`:

```hcl
resource "aws_s3_bucket" "sitio_web" {
  bucket = "mi-sitio-web-publico"
  #checkov:skip=CKV_AWS_20:Este bucket es intencionalamente público — aloja el sitio web estático
  #checkov:skip=CKV2_AWS_6:Acceso público requerido para hosting web
}
```

El comentario `#checkov:skip=ID:RAZON` es obligatorio. Sin la razón documentada, el skip no expresa intención y se vuelve imposible de auditar.

## 5.7 Escanear el historial de cambios (solo archivos modificados)

En repos grandes con mucho código Terraform existente, puede ser preferible escanear solo los archivos modificados en el PR:

```yaml
- name: Get changed Terraform files
  id: changed
  uses: tj-actions/changed-files@v44
  with:
    files: '**/*.tf'

- name: Run Checkov on changed files
  if: steps.changed.outputs.any_changed == 'true'
  uses: bridgecrewio/checkov-action@master
  with:
    file: ${{ steps.changed.outputs.all_changed_files }}
    framework: terraform
```

## 5.8 tfsec como alternativa

[tfsec](https://github.com/aquasecurity/tfsec) es una alternativa a Checkov, también de Aqua Security (los creadores de Trivy). Es más rápido y tiene buena integración con GitHub:

```yaml
- name: Run tfsec
  uses: aquasecurity/tfsec-action@v1.0.0
  with:
    soft_fail: false
```

Checkov cubre más frameworks (no solo Terraform), tfsec tiene menor latencia. Usar Checkov si el repo tiene mezcla de Terraform + CloudFormation + K8s YAML.

## 5.9 Resumen

| Herramienta | Formato | Integración GitHub Actions |
|-------------|---------|---------------------------|
| Checkov | Terraform, CF, K8s, Docker | `bridgecrewio/checkov-action` |
| tfsec | Terraform | `aquasecurity/tfsec-action` |

Continuar con [06 - DAST con OWASP ZAP](../06-DAST/1-OWASP-ZAP.md)
