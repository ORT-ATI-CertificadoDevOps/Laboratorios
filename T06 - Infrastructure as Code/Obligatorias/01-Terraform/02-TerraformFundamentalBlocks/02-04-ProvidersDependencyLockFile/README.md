# Providers - Dependency Lock File

## 01 - Introducción
- Entender la importancia de Dependency Lock File que aparece a partir de la versión de Terraforn 0.14.

## 02 - Revisar los Terraforn Manifests
- Ver que la información em los archivos de la carpeta terraform-manifesta y validar en que difieren de los ejercicios anteriores. 

- c1-versions.tf
- c2-s3bucket.tf
- .terraform.lock.hcl


## 03 - Inicializar y aplicar la configuración 
```t
# Initialize Terraform
terraform init

# Validate Terraform Configuration files
terraform validate

# Execute Terraform Plan
terraform plan

# Create Resources using Terraform Apply
terraform apply
```
- Verificar **.terraform.lock.hcl**
  - Ver el contenido del archivo
  - Comparar `.terraform.lock.hcl-ORIGINAL` & `.terraform.lock.hcl`
  - Hacer respaldo de `.terraform.lock.hcl` como `.terraform.lock.hcl-FIRST-INIT` 
```
# Backup
cp .terraform.lock.hcl .terraform.lock.hcl-FIRST-INIT
```

## 04 - Hacer version upgrade del AWS provider 
- Para AWS Provider, con la version `version = ">= 2.0.0"`, va a ser actualizado a la última versióni `3.x.x` con el comando `terraform init -upgrade` 
```t
# Upgrade Provider Version
terraform init -upgrade
```
- Revisar **.terraform.lock.hcl**
  - Discutir sobre las versiones de AWS.
  - Comparar `.terraform.lock.hcl-FIRST-INIT` & `.terraform.lock.hcl`

## 05 - Aplicar la configuración de Terraform con el último AWS Provider 
- Debería de fallar el despliegue por los cambios realizados sobre la versión de AWS.
```
# Terraform Apply
terraform apply
```

## 06 - Comentar la región en el recurso que fallo y volver a probar
- Cuando actualizamos a una versión mayor de provider, se pueden romper algunas funcionalidades.
- Por eso con `.terraform.lock.hcl`, podemos evitar este tipo de problemas.
```
# Comment Region Attribute
# Resource Block: Create AWS S3 Bucket
resource "aws_s3_bucket" "sample" {
  bucket = random_pet.petname.id
  acl    = "public-read"

  #region = "us-west-2"
}

# Terraform Apply
terraform apply
```

## 07 - Clean-Up
```
# Destroy Resources
terraform destroy

# Delete Terraform Files
rm -rf .terraform    # We are not removing files named ".terraform.lock.hcl, .terraform.lock.hcl-ORIGINAL" which are needed for this demo for you.
rm -rf terraform.tfstate*
```

## Referencias
- [Random Pet Provider](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/pet)
- [Dependency Lock File](https://www.terraform.io/docs/configuration/dependency-lock.html)
- [Terraform New Features in v0.14](https://learn.hashicorp.com/tutorials/terraform/provider-versioning?in=terraform/0-14)
- [AWS S3 Bucket Region - Read Only in AWS Provider V3.x](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/guides/version-3-upgrade#region-attribute-is-now-read-only)