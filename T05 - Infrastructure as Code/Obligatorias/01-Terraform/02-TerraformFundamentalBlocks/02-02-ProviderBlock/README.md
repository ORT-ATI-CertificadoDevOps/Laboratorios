# Terraform Provider Block

> **Tiempo estimado:** 25 minutos

El Provider Block es el puente entre Terraform y la API del proveedor de infraestructura (AWS, Azure, GCP, etc.). Sin él, Terraform no sabe cómo autenticarse ni contra qué región trabajar. Los providers son plugins descargados del Terraform Registry durante el `terraform init`, y cada uno expone recursos y data sources específicos de esa plataforma.

### Puntos a tener en consideración
- La autenticación con Static Credentials (hardcoded en el `.tf`) **nunca** debe usarse en producción ni subirse a Git.
- El método recomendado es el archivo `~/.aws/credentials` generado por `aws configure`.
- Si no se especifica `profile`, Terraform usa el perfil `default` automáticamente.

---

## 01 - Introducción
- Qué son los Terraform Providers?
- Qué hacen los Providers?
- En donde estan alojados los Providers (Terraform Registry)?
- Para qué se usan los Providers?


## 02 - Provider Requirements
- Definir los providers en Terraform Block
- Entender que significa cada uno de los siguientes terminos:
`required_providers` in terraform block
  - local names
  - source
  - version

```
# Terraform Block
terraform {
  required_version = "~> 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}
```


## 03 - Provider Block  
- Crear un Provider Block for AWS

```
# Provider Block
provider "aws" {
  region = "us-east-1"
  profile = "default"  # defining it is optional for default profile
}
```

- Verificar los tipos de [Authentication Types](https://registry.terraform.io/providers/hashicorp/aws/latest/docs#authentication) 
- Static Credentials - NO RECOMENDADA
- Environment variables
- Credanciales compartidas/configuration file (Vamos a utilizar este último)
  - Verificar en `cat $HOME/.aws/credentials`
  - Si no lo encuentran, usar `aws configure` para configurar las credenciales de aws.

```
# Initialize Terraform
terraform init

# Validate Terraform Configuration files
terraform validate

# Execute Terraform Plan
terraform plan
```  

## 04 - Agregar un Resource Block para crear una AWS VPC
- [AWS VPC Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc)

```
resource "aws_vpc" "myvpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    "Name" = "myvpc"
  }
}
```

## 05 - Ejecutar los comandos de Terraform para crear una AWS VPC

```
# Initialize Terraform
terraform init

# Validate Terraform Configuration files
terraform validate

# Execute Terraform Plan
terraform plan

# Create Resources using Terraform Apply
terraform apply -auto-approve
```  

## 06 - Clean-Up 

```
# Destroy Terraform Resources
terraform destroy -auto-approve

# Delete Terraform Files
rm -rf .terraform*
rm -rf terraform.tfstate*
```


## Referencias
- [Terraform Providers](https://www.terraform.io/docs/configuration/providers.html)
- [AWS Provider Documentation](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [AWS VPC](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc)

---

Continuar con [02-03 — Multiple Provider Configurations](../02-03-MultipleProviderConfigurations/README.md)