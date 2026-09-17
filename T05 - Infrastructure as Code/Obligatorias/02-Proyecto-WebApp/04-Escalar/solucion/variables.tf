variable "aws_region" {
  description = "Región de AWS donde se crean los recursos"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Nombre del ambiente (dev, test, prod)"
  type        = string

  validation {
    condition     = contains(["dev", "test", "prod"], var.environment)
    error_message = "El ambiente debe ser dev, test o prod."
  }
}

variable "instance_type" {
  description = "Tipo de instancia EC2"
  type        = string
  default     = "t3.micro"
}

variable "instance_count" {
  description = "Cantidad de servidores web detrás del balanceador"
  type        = number
  default     = 2

  validation {
    condition     = var.instance_count >= 1 && var.instance_count <= 4
    error_message = "La cantidad de instancias debe estar entre 1 y 4."
  }
}

variable "mensaje" {
  description = "Texto que muestra la página web"
  type        = string
  default     = "Hola desde Terraform"
}

variable "vpc_cidr" {
  description = "Rango de direcciones de la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnets" {
  description = "Subnets públicas: zona de disponibilidad => CIDR"
  type        = map(string)
  default = {
    "us-east-1a" = "10.0.1.0/24"
    "us-east-1b" = "10.0.2.0/24"
  }
}

variable "extra_tags" {
  description = "Tags adicionales para todos los recursos"
  type        = map(string)
  default     = {}
}
