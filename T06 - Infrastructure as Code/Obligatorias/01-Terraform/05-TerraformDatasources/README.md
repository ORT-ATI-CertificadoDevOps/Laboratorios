# Terraform Datasources

## 01 - Introducción
- Entender sobre Datasources en Terraform
- Impelementar un pequeños ejemplo que utilice Datasources
- Obtener la última imagen Amazon Linux 2 AMI ID utilizando Datasources y referenciar el valor al momento de crear la instancia EC2 `ami = data.aws_ami.amzlinux.id`

## 02 - Crear datasource to fetch latest AMI ID
- Crear or revisar manifest `c6-ami-datasource.tf`
- Ir a AWS Mgmt Console -> Services -> EC2 -> Images -> AMI 
- Buscar por "Public Images" -> Provide AMI ID -> se puede obtener:
  - AMI Name format
  - Owner Name
  - Visibility
  - Platform
  - Root Device Type
  - and many more info here. 
- De acuerdo a esto, utilizar esta información para nuestros filtros en DataSource

## 03 - Referenciar datasource in ec2-instance.tf
```
  ami           = data.aws_ami.amzlinux.id 
```

## 04 - Test using Terraform commands
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 

# Create Resources (Optional)
terraform apply -auto-approve

# Access Application
http://<Public-DNS>

# Destroy Resources
terraform destroy -auto-approve
```


## Referencias
- [AWS EC2 AMI Datasource](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/ami)
