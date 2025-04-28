# Terraform Input Variables

## 00 - Introducción
- **v1:** Input Variables - Basics
- **v2:** Provide Input Variables when prompted during terraform plan or apply
- **v3:** Override default variable values using CLI argument `-var` 
- **v4:** Override default variable values using Environment Variables
- **v5:** Provide Input Variables using `terraform.tfvars` files
- **v6:** Provide Input Variables using `<any-name>.tfvars` file with CLI 
argument `-var-file`
- **v7:** Provide Input Variables using `auto.tfvars` files
- **v8-01:** Implement complex type constructors like `list` 
- **v8-02:** Implement complex type constructors like `maps`
- **v9:** Implement Custom Validation Rules in Variables
- **v10:** Protect Sensitive Input Variables
- **v11:** Understand about `File` function

## Prerequisitos
- Crear una EC2 Key pair con el nombre `terraform-key`
- En todos los templates listados V1 a V12, vamos a utilizar `key_name      = "terraform-key"`


## 01 - Input Variables Basics 
- **Directorio:** v1-Input-Variables-Basic
- Crear / revisar the terraform manifests
  - c1-versions.tf
  - c2-variables.tf
  - c3-security-groups.tf
  - c4-ec2-instance.tf
- Vamos a definir un archivo `c3-variables.tf` y definir las variables detalladas a continuación:
  - aws_region es una variable de tipo `string`
  - ec2_ami_id es una variable de tipo `string`
  - ec2_instance_count es una variable de tipo `number`
```t
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan

# Create Resources
terraform apply

# Access Application
http://<Public-IP-Address>

# Clean-Up
terraform destroy -auto-approve
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## 02 - Input Variables Asignar en Prompted
- **Directorio:** v2-Input-Variables-Assign-when-prompted
- Agregar una nueva variable en `variables.tf` y `ec2_instance_type` con ningun default value
- Como la variable no tiene ningún valor por defecto al momento de ejecutar `terraform plan` o `terraform apply` se v a alanzar el prompt para asignarle valores.

```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan
```

## 03 - Input Variables Override default value with cli argument `-var`
- **Directorio:** v3-Input-Variables-Override-default-with-cli
- Modificar las variables por defecto definidas en `variables.tf` pasando los nuevos vaores utilizando el flag `-var` utilizando la CLI.
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Option-1 (Always provide -var for both plan and apply)
# Review the terraform plan
terraform plan -var="ec2_instance_type=t3.large" -var="ec2_instance_count=1"

# Create Resources (optional)
terraform apply -var="ec2_instance_type=t3.large" -var="ec2_instance_count=1"

# Option-2 (Generate plan file with -var and use that with apply)
# Generate Terraform plan file
terraform plan -var="ec2_instance_type=t3.large" -var="ec2_instance_count=1" -out v3out.plan

# Create / Deploy Terraform Resources using Plan file
terraform apply v3out.plan 
```

## 04 - Input Variables Override with Environment Variables
- **Directorio:** v4-Input-Variables-Override-with-Environment-Variables
- Setear las variables de ambiente y ejecutar `terraform plan` y verificar si las variables se sobrescriben.
```
# Sample
export TF_VAR_variable_name=value

# SET Environment Variables
export TF_VAR_ec2_instance_count=1
export TF_VAR_ec2_instance_type=t3.large
echo $TF_VAR_ec2_instance_count, $TF_VAR_ec2_instance_type

# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan

# UNSET Environment Variables after demo
unset TF_VAR_ec2_instance_count
unset TF_VAR_ec2_instance_type
echo $TF_VAR_ec2_instance_count, $TF_VAR_ec2_instance_type
```

## 05 - Assign Input Variables from terraform.tfvars
- **Directorio:** v5-Input-Variables-Assign-with-terraform-tfvars
- Crear archivo llamada `terraform.tfvars` y definir variables
- En el archivo creado `terraform.tfvars`, Terraform va a cargar de manera automatica las variables presentes en este archivo sobreescribiendo los variables `default` existentes en `variables.tf`
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan

# Create Resources
terraform apply

# Access Application
http://<Elastic-IP-Address>
```

## 06 - Assign Input Variables with -var-file argument
- **Directorio:** v6-Input-Variables-Assign-with-tfvars-var-file
- Si queremos utilizar nombres diferentes para los archivos  `.tfvars`, es necesario explicitarlo cuando se pasa el argumento  `-var-file` durante `terraform plan or apply` 
- Vamos a utilizar los siguientes archivos para este ejercicio:
  - **c2-variables.tf:** Variable aws_region se tomara del valor por defecto.
  - **terraform.tfvars:** Varible ec2_instance_count sera tomada desde este archivo.
  - **web.tfvars:** Variable ec2_instance_type sera tomada desde este archivo.
  - **app.tfvars:** Variable ec2_instance_type sera tomada desde este archivo.
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan -var-file="web.tfvars"
terraform plan -var-file="app.tfvars"
```

## 07 - Auto load input variables with .auto.tfvars files
- **Direcotiro:** v7-Input-Variables-Assign-with-auto-tfvars
- Crear archivo con la extensión `.auto.tfvars`. 
- Con esta extensión, sin importar como se llame el archivo, las variables que se encuentran en el mismo van a ser cargadas durante `terraform plan o apply`
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 
```
## 08 - Implement complex type cosntructors like `list` and `maps`
- **Directorio:** v8-Input-Variables-Lists-Maps
- [Type Constraints](https://www.terraform.io/docs/language/expressions/types.html)
### 08-01 - Implement Vairable Type as List
- **list (or tuple):** una secuencia de valores, como ["us-west-1a", "us-west-1c"]. Los elementos de una lista o tupla se identifican mediante números enteros consecutivos que comienzan con cero.
- Implementar la función Lista para variables `ec2_instance_type`
```
# Implement List Function in variables.tf
variable "ec2_instance_type" {
  description = "EC2 Instance Type"
  type = list(string)
  default = ["t3.micro", "t3.small", "t3.medium"]
}

# Reference Values from List in ec2-instance.tf
instance_type = var.ec2_instance_type[0] --> t3.micro
instance_type = var.ec2_instance_type[1] --> t3.small
instance_type = var.ec2_instance_type[2] --> t3.medium

# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 
```

### 08-02 - Implement Vairable Type as Map
- **map (or object):** un grupo de valores identificados por etiquetas con nombre, como  {name = "Mabel", age = 52}.
- Implementar Map function para variables `ec2_instance_tags`
```
# Implement Map Function for tags
variable "ec2_instance_tags" {
  description = "EC2 Instance Tags"
  type = map(string)
  default = {
    "Name" = "ec2-web"
    "Tier" = "Web"
  }

# Reference Values from Map in ec2-instance.tf
tags = var.ec2_instance_tags  

# Implement Map Function for Instance Type
# Nota importante: comentar variable "ec2_instance_type" con lista
variable "ec2_instance_type_map" {
  description = "EC2 Instance Type using maps"
  type = map(string)
  default = {
    "small-apps" = "t3.micro"
    "medium-apps" = "t3.medium" 
    "big-apps" = "t3.large"
  }

# Reference Instance Type from Maps Variables
instance_type = var.ec2_instance_type_map["small-apps"]
instance_type = var.ec2_instance_type_map["medium-apps"]
instance_type = var.ec2_instance_type_map["big-apps"]

# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 
```



## 09 - Implement Custom Validation Rules in Variables
- **Directorio:** v9-Input-Variables-Validation-Rules
- Entender y implementar validaciones para las variables
- [Terraform Console](https://www.terraform.io/docs/cli/commands/console.html)
- El comando `terraform console` provee una consola interactiva para evaluar las expresiones.
- 
### 09-01 - Learn Terraform Length Function
- [Terraform Length Function](https://www.terraform.io/docs/language/functions/length.html)
```t
# Go to Terraform Console
terraform console

# Test length function
Template: length()
length("hi")
length("hello")
length(["a", "b", "c"]) # List
length({"key" = "value"}) # Map
length({"key1" = "value1", "key2" = "value2" }) #Map
```

### 09-02: Learn Terraform SubString Function
- [Terraform Sub String Function](https://www.terraform.io/docs/language/functions/substr.html)
```t
# Go to Terraform Console
terraform console

# Test substr function
Template: substr(string, offset, length)
substr("Lab", 1, 4)
substr("Lab", 0, 6)
substr("Lab", 0, 1)
substr("Lab", 0, 0)
substr("Lab", 0, 10)
```

### 09-03: Implement Validation Rule for ec2_ami_id variable
```
variable "ec2_ami_id" {
  description = "AMI ID"
  type = string  
  default = "ami-0be2609ba883822ec"
  validation {
    condition = length(var.ec2_ami_id) > 4 && substr(var.ec2_ami_id, 0, 4) == "ami-"
    error_message = "The ec2_ami_id value must be a valid AMI id, starting with \"ami-\"."
  }
}
```
- **Run Terraform commands**
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan
```

## 10 - Protect Sensitive Input Variables
- **Directorio:** v10-Sensitive-Input-Variables
- [AWS RDS DB Instance](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/db_instance)
- [Vault Provider](https://learn.hashicorp.com/tutorials/terraform/secrets-vault?in=terraform/secrets)
- Cuando utilice variables de entorno para establecer valores sensibles, tenga en cuenta que esos valores estarán en su entorno y en el historial de la línea de comandos. 
`Example: export TF_VAR_db_username=adminTF_VAR_db_password=adifferentpassword`
- Cuando usa variables sensibles en su configuración de Terraform, puede usarlas como lo haría con cualquier otra variable
- Terraform "redactará" estos valores en los archivos de registro y de salida del comando, y generará un error cuando detecte que se expondrán de otras formas.
- **Nota importante-1:** Nunca guarde archivos `secrets.tfvars` en repositorios git.
- **Nota importante-2:** El archivo de estado de Terraform contiene valores para estas variables sensibles  `terraform.tfstate`. Debe mantener seguro su archivo estatal para evitar exponer estos datos.
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan -var-file="secrets.tfvars"

# Create Resources
terraform apply -var-file="secrets.tfvars"

# Verify Terraform State files
grep password terraform.tfstate
grep username terraform.tfstate 

# Destroy Resources
terraform destroy var-file="secrets.tfvars"

# Clean-Up
rm -rf .terraform*
rm -rf terraform.tfstate*
```

### Variable Definition Precedence
- [Terraform Variable Definition Precedence](https://www.terraform.io/docs/language/values/variables.html#variable-definition-precedence)


## 11 - Entendiendo sobre `File` function
- **Directorio:** v11-File-Function
- Entender sobre [Terraform File Function](https://www.terraform.io/docs/language/functions/file.html)

```t
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 

# Create Resources
terraform apply 

# Access Application
http://<Public-IP>

# Destroy Resources
terraform destroy -auto-approve
```


## Referencias
- [Terraform Input Variables](https://www.terraform.io/docs/language/values/variables.html)
- [Resource: AWS EC2 Instance](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance)
- [Resource: AWS Security Group](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group)
- [Sensitive Variables - Additional Reference](https://learn.hashicorp.com/tutorials/terraform/sensitive-variables)



