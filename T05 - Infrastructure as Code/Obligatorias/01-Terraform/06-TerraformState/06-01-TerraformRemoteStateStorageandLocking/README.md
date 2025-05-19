# Terraform Remote State Storage & Locking

## 01 - Introducción
- Entender sobre Terraform Backends
- Entender sobre Remote State Storage y sus ventajas
- Entender sobre State Locking y sus ventajas
- El state se encuentra almacenado por defecto en una archivo local llamado "terraform.tfstate", pero se puede almacenar remotamente, lo cual es mejor para trabajar en equipo
- Crear un AWS S3 bucket para almacenar el archivo `terraform.tfstate` y habilitar las configuraciones de backend en el block de Terraform
- Crear una DynamoDB table para implementar State Locking (lo cual habilitaremos desde el Terraform)

## 02 - Create S3 Bucket
- Ir a Services -> S3 -> Create Bucket
- **Bucket name:** terraform-DevOpsLab
- **Region:** US-East (N.Virginia)
- **Bucket settings for Block Public Access:** dejar por defecto
- **Bucket Versioning:** Enable
- Todo lo demas dejar **default**
- Click en **Create Bucket**
- **Create Folder**
  - **Folder Name:** dev
  - Click on **Create Folder**


## 03 - Terraform Backend Configuration
- **Directorio:** terraform-manifests
- [Terraform Backend as S3](https://www.terraform.io/docs/language/settings/backends/s3.html)
- Agregar el siguiente codigo de Terraform Backend Block en el las `Terrafrom Settings` sobre el archivo `main.tf`
```
# Terraform Backend Block
  backend "s3" {
    bucket = "terraform-DevOpsLab"
    key    = "dev/terraform.tfstate"
    region = "us-east-1"    
  }
```

## 04 - Test with Remote State Storage Backend
```t
# Initialize Terraform
terraform init

Observación: 
Successfully configured the backend "s3"! Terraform will automatically
use this backend unless the backend configuration changes.

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev/terraform.tfstate

# Validate Terraform configuration files
terraform validate

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev/terraform.tfstate

# Format Terraform configuration files
terraform fmt

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev/terraform.tfstate

# Review the terraform plan
terraform plan 

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev/terraform.tfstate

# Create Resources 
terraform apply -auto-approve

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev/terraform.tfstate
Observación: En este punto deberiamos de poder ver los archivos terraform.tstate en nuestro s3 bucket

# Access Application
http://<Public-DNS>
```

## 05 - Bucket Versioning - Test
- Verificar en archivo `c2-variables.tf` 
```t
variable "instance_type" {
  description = "EC2 Instance Type - Instance Sizing"
  type = string
  #default = "t2.micro"
  default = "t2.small"
}
```
- Execute Terraform Commands
```t
# Review the terraform plan
terraform plan 

# Create Resources 
terraform apply -auto-approve

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev/terraform.tfstate
Observación: Una nueva version de terraform.tfstate va a ser creada
```


## 06 - Destruir Resources
- Destruir los recursos y verificar el versionado del s3 bucket
```t
# Destroy Resources
terraform destroy -auto-approve
```
## 07 - Terraform State Locking Introduction
- Entender sobre ventajas de Terraform State Locking

## 08 - Agregar State Locking Feature using DynamoDB Table
- Crear Dynamo DB Table
  - **Table Name:** terraform-dev-state-table
  - **Partition key (Primary Key):** LockID (Poner como String)
  - **Table settings:** Use default settings (checked)
  - Click en **Create**

## 09 - Actualizar DynamoDB Table entry in backend in c1-versions.tf
- Habilitar State Locking in `c1-versions.tf`
```t
  # Adding Backend as S3 for Remote State Storage with State Locking
  backend "s3" {
    bucket = "terraform-DevOpsLab"
    key    = "dev2/terraform.tfstate"
    region = "us-east-1"  

    # For State Locking
    dynamodb_table = "terraform-dev-state-table"
  }
```

## 10 - Ejecutar Terraform Commands
```t
# Initialize Terraform 
terraform init

# Review the terraform plan
terraform plan 
Observación: 
1) Mensajes desplegados al inicio del comando:
Acquiring state lock. This may take a few moments...
Releasing state lock. This may take a few moments...
2) Verificar en DynamoDB Table -> Items tab

# Create Resources 
terraform apply -auto-approve

# Verify S3 Bucket for terraform.tfstate file
bucket-name/dev2/terraform.tfstate
Observación: Una nueva version de terraform.tfstate va a ser creada en la carpeta dev2
```

## 11 - Destruir Resources
- Destruir Resources and verificar Bucket Versioning
```t
# Destroy Resources
terraform destroy -auto-approve

# Clean-Up Files
rm -rf .terraform*
rm -rf terraform.tfstate*  # This step not needed as e are using remote state storage here
```

## Referencias 
- [AWS S3 Backend](https://www.terraform.io/docs/language/settings/backends/s3.html)
- [Terraform Backends](https://www.terraform.io/docs/language/settings/backends/index.html)
- [Terraform State Storage](https://www.terraform.io/docs/language/state/backends.html)
- [Terraform State Locking](https://www.terraform.io/docs/language/state/locking.html)
- [Remote Backends - Enhanced](https://www.terraform.io/docs/language/settings/backends/remote.html)