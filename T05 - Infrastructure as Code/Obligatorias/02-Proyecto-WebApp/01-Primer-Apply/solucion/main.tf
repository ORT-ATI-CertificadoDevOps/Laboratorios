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
  # Amazon Linux 2023 en us-east-1. Los AMI ID cambian: reemplazalo por el
  # que devuelve el comando de la sección 02 del README.
  ami                    = "ami-0953476d60561c955"
  instance_type          = "t3.micro"
  vpc_security_group_ids = [aws_security_group.web.id]

  # Si cambia el user_data, la instancia se recrea (ver sección 07)
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
