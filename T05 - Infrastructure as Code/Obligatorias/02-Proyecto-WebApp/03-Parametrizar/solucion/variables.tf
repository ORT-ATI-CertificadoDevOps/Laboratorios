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

variable "mensaje" {
  description = "Texto que muestra la página web"
  type        = string
  default     = "Hola desde Terraform"
}

variable "extra_tags" {
  description = "Tags adicionales para todos los recursos"
  type        = map(string)
  default     = {}
}
