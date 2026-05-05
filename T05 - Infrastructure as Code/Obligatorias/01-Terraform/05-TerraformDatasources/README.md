# Terraform Datasources

> **Tiempo estimado:** 20 minutos

Los Data Sources permiten leer información de recursos existentes en AWS (u otro proveedor) sin gestionarlos con Terraform. Son la forma de consultar el AMI más reciente de Amazon Linux, la VPC default, subnets existentes o cualquier recurso creado fuera de la configuración actual. En lugar de hardcodear IDs que cambian con el tiempo, los datasources los obtienen dinámicamente en cada ejecución.

### Puntos a tener en consideración
- Los datasources son de **solo lectura** — no crean ni modifican recursos.
- Se referencian como `data.<tipo>.<nombre>.<atributo>`.
- Amazon Linux 2 llegó a su fin de vida en junio de 2025. Usar Amazon Linux 2023 (AL2023) en adelante.

---

## 01 - Introducción
- Entender sobre Datasources en Terraform
- Implementar un pequeño ejemplo que utilice Datasources
- Obtener el último AMI ID de **Amazon Linux 2023** dinámicamente usando Datasources, y referenciar el valor al crear la instancia EC2: `ami = data.aws_ami.amzlinux.id`

> **Nota:** Este lab usa Amazon Linux 2023 (AL2023). Amazon Linux 2 llegó a su fin de vida en junio de 2025 y ya no recibe actualizaciones de seguridad.

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

---

Continuar con [06-01 — Remote State Storage & Locking](../06-TerraformState/06-01-TerraformRemoteStateStorageandLocking/README.md)
