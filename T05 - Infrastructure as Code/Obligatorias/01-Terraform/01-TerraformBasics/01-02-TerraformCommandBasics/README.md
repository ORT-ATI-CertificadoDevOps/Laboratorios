# Terraform Command Basics

## 01 - Introducción
- Entendiendo los comandos básicos de Terraform
  - terraform init
  - terraform validate
  - terraform plan
  - terraform apply
  - terraform destroy      

## 02 - Revisar terraform manifest para EC2 Instance
- **Pre-condición-1:** Verificar que se tiene **default-vpc** en la región del manifest.
- **Pre-condición-2:** Verificar que el AMI que se encuentra en el manifest existe en la región de manifest, en caso contrario, actualizar por un AMI existente en la región
- **Pre-condición-3:** Verificar las AWS Credentials en **$HOME/.aws/credentials**

```
# Terraform Settings Block
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      #version = "~> 5.0" # Opcional, pero recomendado en producción
    }
  }
}

# Provider Block
provider "aws" {
  profile = "default" # AWS Credentials Profile configurado localmente en $HOME/.aws/credentials
  region  = "us-east-1"
}

# Resource Block
resource "aws_instance" "ec2demo" {
  ami           = "ami-0953476d60561c955" # Amazon Linux en us-east-1, actualizar según la región
  instance_type = "t2.micro"
}
```

## 03 - Terraform Core Commands

```
# Initialize Terraform
terraform init

# Terraform Validate
terraform validate

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
```

## 04 - Verificar la instancia EC2 Instance en AWS Management Console
- Ir a AWS Management Console -> Services -> EC2
- Verificar que la instancia EC2 fue creada.



## 05 - Destruir la Infraestructura

```
# Destroy EC2 Instance
terraform destroy

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## 06 - Conclusión
- Resumen de lo que aprendimos durante esta lección.
- Aprendimos sobre los comandos más importantes de Terraform:
  - terraform init
  - terraform validate
  - terraform plan
  - terraform apply
  - terraform destroy