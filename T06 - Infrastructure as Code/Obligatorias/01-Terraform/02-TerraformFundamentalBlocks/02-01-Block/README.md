# Terraform Block 

## 01 - Introducción
- Entender sobre los Terraform Block y su importancia.
- Entender como manejar restricciones de versión para Terraform version y Provider version en Terraform Block.

## 02 - Entender sobre Terraform Settings Block
- Terraform Version requerida
- Provider Requirements
- Terraform backends
- Experimental Language Features
- Pasar Metadata to Providers
- Revisar el archivo **sample-terraform-settings.tf** para mayor entendimiento

## 03 - Crear un simple terraform block y jugar con el required_version
- `required_version` se centra en el Terraform CLI instalado en su equipo.
- Si la versión instalada en su escritorio no coincide con las restricciones especificadas en el Terraform block, se producirá un error.
- Cambie las versiones y ejecute `terraform init`, observe los comportamientos.
```
Play with Terraform Version
  required_version = "~> 0.14.3" 
  required_version = "= 0.14.3"    
  required_version = "= 0.14.4"  
  required_version = ">= 0.13"   
  required_version = "= 0.13"    
  required_version = "~> 0.13"   
 

# Terraform Block
terraform {
  required_version = "~> 0.14"
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
      version = "~> 3.0"            
      version = ">= 3.0.0, < 3.1.0"
      version = ">= 3.0.0, <= 3.1.0"
      version = "~> 2.0"
      version = "~> 3.0"   
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

