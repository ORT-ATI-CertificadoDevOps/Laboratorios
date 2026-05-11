# Terraform Block

> **Tiempo estimado:** 20 minutos

El bloque `terraform` es el punto de entrada de cualquier configuración: define la versión mínima del CLI, los providers requeridos y el backend donde se almacenará el state. Controlar las versiones aquí garantiza que el código funcione de forma reproducible en cualquier entorno, evitando que una actualización de Terraform o de un provider rompa configuraciones existentes.

### Puntos a tener en consideración
- `required_version` aplica al binario de Terraform CLI instalado localmente.
- `required_providers` controla la versión del plugin que se descarga al ejecutar `terraform init`.
- Usar el operador `~>` (pessimistic constraint) es la práctica recomendada para permitir actualizaciones menores pero no mayores.

---

## 01 - Introducción
- Entender sobre los Terraform Block y su importancia.
- Entender como manejar restricciones de versión para Terraform version y Provider version en Terraform Block.

## 02 - Entender sobre Terraform Settings Block
- Terraform Version requerida
- Provider Requirements
- Terraform backends
- Experimental Language Features
- Pasar Metadata to Providers
- Revisar el archivo [sample-terraform-settings.tf](https://raw.githubusercontent.com/ORT-ATI-CertificadoDevOps/Laboratorios/refs/heads/main/T05%20-%20Infrastructure%20as%20Code/Obligatorias/01-Terraform/02-TerraformFundamentalBlocks/02-01-Block/sample-terraform-settings.tf) para mayor entendimiento

## 03 - Crear un simple terraform block y jugar con el required_version
- `required_version` se centra en el Terraform CLI instalado en su equipo.
- Si la versión instalada en su escritorio no coincide con las restricciones especificadas en el Terraform block, se producirá un error.
- Cambie las versiones y ejecute `terraform init`, observe los comportamientos.

```
Play with Terraform Version
  required_version = "~> 1.9.0"   # exactamente 1.9.x
  required_version = "= 1.9.0"    # exactamente 1.9.0
  required_version = ">= 1.0"     # cualquier versión 1.x o superior
  required_version = "~> 1.0"     # recomendado: cualquier 1.x
  required_version = "= 2.0.0"    # fallará si tenés 1.x instalado
 

# Terraform Block
terraform {
  required_version = "~> 1.0"
}

# To view my Terraform CLI Version installed on my desktop
terraform version

# Initialize Terraform
terraform init
```
## 04 - Agregar Provider y jugar con el Provider Version 
- `required_providers` especifica todos los proveedores requeridos por el módulo actual, asignando cada nombre de proveedor local a una dirección origen y una restricción de versión. 
- Realizar el mismo ejercicio que el punto anterior.

```
Play with Provider Version
      version = "~> 5.0"             # recomendado: cualquier 5.x
      version = ">= 5.0.0, < 6.0.0"
      version = "= 5.0.0"            # versión exacta
      version = "~> 4.0"             # fallará si el provider actualiza a 5.x
```

```
# Terraform Init with upgrade option to change provider version
terraform init -upgrade
```


## 05 - Clean-Up

```
# Delete Terraform Folders & Files
rm -rf .terraform*
```

## Referencias
- [Terraform Version Constraints](https://www.terraform.io/docs/configuration/version-constraints.html)
- [Terraform Versions - Best Practices](https://www.terraform.io/docs/configuration/version-constraints.html#best-practices)

---

Continuar con [02-02 — Provider Block](../02-02-ProviderBlock/README.md)

