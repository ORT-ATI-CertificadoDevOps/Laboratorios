# Multiple Provider Configurations

> **Tiempo estimado:** 20 minutos

En algunos escenarios es necesario desplegar recursos en múltiples regiones AWS dentro de la misma configuración de Terraform — por ejemplo, una VPC en `us-east-1` y otra en `us-west-1` para alta disponibilidad geográfica. Terraform lo resuelve permitiendo múltiples instancias del mismo provider usando el meta-argumento `alias`.

### Puntos a tener en consideración
- Siempre debe existir un provider "por defecto" (sin `alias`).
- Los recursos que no especifican `provider` usan el provider por defecto automáticamente.
- El alias se referencia como `<nombre_provider>.<alias>` en el bloque del recurso.

---

## 01 - Introducción
- Entender y implementar configuración de multiples providers.

## 02 - ¿Cómo definir multiples configuraciones del mismo provider?  
- Entender sobre el provider por defecto.
- Entender y definir multiples prodivers del mismo tipo de provider.

```hcl
# Provider-1 for us-east-1 (Default Provider)
provider "aws" {
  region = "us-east-1"
  profile = "default"
}

# Provider-2 for us-west-1
provider "aws" {
  region = "us-west-1"
  profile = "default"
  alias = "aws-west-1"
}
```

## 03 - ¿Cómo referencias un provider que no sea el por defecto en un recurso?

```hcl
# Resource Block to Create VPC in us-west-1
resource "aws_vpc" "vpc-us-west-1" {
  cidr_block = "10.2.0.0/16"
  #<PROVIDER NAME>.<ALIAS>
  provider = aws.aws-west-1
  tags = {
    "Name" = "vpc-us-west-1"
  }
}
```

## 04 - Ejecutar los siguientes comandos

```
# Initialize Terraform
terraform init

# Validate Terraform Configuration Files
terraform validate

# Generate Terraform Plan
terraform plan

# Create Resources
terraform apply -auto-approve

# Verify the same
1. Verify the VPC created in us-east-1
2. Verify the VPC created in us-west-2
```

## 05 - Clean-Up 

```
# Destroy Terraform Resources
terraform destroy -auto-approve

# Delete Terraform Files
rm -rf .terraform*
rm -rf terraform.tfstate*
```



## Referencias
- [Provider Meta Argument](https://www.terraform.io/docs/configuration/meta-arguments/resource-provider.html)

---

Continuar con [02-04 — Providers Dependency Lock File](../02-04-ProvidersDependencyLockFile/README.md)