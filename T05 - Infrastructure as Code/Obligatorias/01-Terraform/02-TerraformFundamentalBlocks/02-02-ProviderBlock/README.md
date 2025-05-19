# Terraform Provider Block

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
```t
# Terraform Block
terraform {
  required_version = "~> 0.14.4"
  required_providers {
    aws = { 
      source = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
}
```


## 03 - Provider Block  
- Crear un Provider Block for AWS
```t
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

```t
# Initialize Terraform
terraform init

# Validate Terraform Configuration files
terraform validate

# Execute Terraform Plan
terraform plan
```  

## 04 - Agrear un Reosource Block para crear una AWS VPC
- [AWS VPC Resource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc)
```t
resource "aws_vpc" "myvpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    "Name" = "myvpc"
  }
}
```

## 05 - Ejecutar los comandos de Terraform para crear una AWS VPC
```t
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
```t
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