# Ejercicio 01 - Este archivo tiene 6 errores. Encontralos y corregilos
# usando únicamente los mensajes de `terraform fmt` y `terraform validate`.

terraform {
  required_providers {
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

resource "random_pet" "servidor" {
  length    = 2
  separator = "-"
  prefix    = 'web'
}

resource "random_integer" {
  min = 8000
  max = 8999
}

resource "random_string" "1sufijo" {
  length 8
  special = false
}

resource "random_password" "db" {
  lenght  = 16
  special = true

  lifecycle = {
    create_before_destroy = true
  }
}
