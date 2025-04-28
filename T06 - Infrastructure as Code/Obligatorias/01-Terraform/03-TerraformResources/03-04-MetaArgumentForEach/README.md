# Terraform Resource Meta-Argument for_each

## 01 - Introducción
- Entender sobre Meta-Argument `for_each`
- Implementar `for_each` con **Maps**
- Implementar `for_each` con **Set of Strings**

## 02 - Implementar for_each with Maps
- **Directorio:** v1-for_each-maps
- **Implementación:** Crear 4 S3 buckets usando for_each maps 
- **c2-s3bucket.tf**
```t
# Create S3 Bucket per environment with for_each and maps
resource "aws_s3_bucket" "mys3bucket" {

  for_each = {
    dev   = "my-dapp-bucket"
    qa    = "my-qapp-bucket"
    stag  = "my-sapp-bucket"    
    prod  = "my-papp-bucket"        
  }  

  bucket = "${each.key}-${each.value}"
  acl    = "private"

  tags = {
    eachvalue   = each.value
    Environment = each.key
    bucketname  = "${each.key}-${each.value}"
  }
}
```

## 03 - Ejecutar Terraform Commands
```t
# Switch to Working Directory
cd v1-for_each-maps

# Initialize Terraform
terraform init

# Validate Terraform Configuration Files
terraform validate

# Format Terraform Configuration Files
terraform fmt

# Generate Terraform Plan
terraform plan
Observación: 
1) 4 buckets que se crearan seran mostrados en el plan
2) Revisar Resource Names ResourceType.ResourceLocalName[each.key]
2) Revisar bucket name (each.key+each.value)
3) Revisar bucket tags

# Create Resources
terraform apply
Observación: 
1) 4 S3 buckets se deberían de crear
2) Revisar bucket names y tags en AWS Management console

# Destroy Resources
terraform destroy

# Clean-Up 
rm -rf .terraform*
rm -rf terraform.tfstate*
```


## 04 - Implementar for_each with toset "Strings"
- **Directorio:** v2-for_each-toset
- **Implementación:** Cerar 4 IAM Users usando for_each toset strings 
- **c2-iamuser.tf**
```t
# Create 4 IAM Users
resource "aws_iam_user" "myuser" {
  for_each = toset( ["Jack", "James", "Madhu", "Dave"] )
  name     = each.key
}
```

## 05 - Ejecutar Terraform Commands
```t
# Switch to Working Directory
cd v2-for_each-toset

# Initialize Terraform
terraform init

# Validate Terraform Configuration Files
terraform validate

# Format Terraform Configuration Files
terraform fmt

# Generate Terraform Plan
terraform plan
Observación: 
1) 4 IAM users van a ser creados con el plan
2) Revisar Resource Names ResourceType.ResourceLocalName[each.key]
2) Revisar IAM User name (each.key)

# Create Resources
terraform apply
Observation: 
1) 4 IAM users se deberían de crear
2) Revisar IAM users en AWS Management console

# Destroy Resources
terraform destroy

# Clean-Up 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## Referencias
- [Resource Meta-Argument: for_each](https://www.terraform.io/docs/language/meta-arguments/for_each.html)
- [Resource: AWS S3 Bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket)
- [Resource: AWS IAM User](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user)