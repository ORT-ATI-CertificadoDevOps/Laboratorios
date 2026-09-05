# Ejercicio 02 - Argumentos, atributos y meta-argumentos
# No requiere credenciales de AWS: todo se crea en tu máquina.

terraform {
  required_providers {
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
  }
}

resource "random_pet" "nombre" {
  length = 2
}

resource "local_file" "saludo" {
  filename = "${path.module}/salida/saludo.txt"
  content  = "Hola desde ${random_pet.nombre.id}\n"
}

output "nombre_generado" {
  value = random_pet.nombre.id
}
