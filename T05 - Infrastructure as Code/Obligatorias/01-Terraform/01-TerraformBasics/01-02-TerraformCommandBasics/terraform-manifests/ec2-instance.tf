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
