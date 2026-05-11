# Terraform Module Basics

> **Tiempo estimado:** 30 minutos

Los módulos son el mecanismo de reutilización de Terraform: agrupan recursos relacionados en una unidad cohesiva con su propia interfaz (variables de entrada y outputs). En lugar de copiar bloques de recursos entre proyectos, se encapsulan en un módulo que puede instanciarse con distintos parámetros. El Terraform Registry ofrece módulos oficiales y de la comunidad para los casos de uso más comunes.

### Puntos a tener en consideración
- **Root Module:** el directorio de trabajo desde donde se ejecuta Terraform.
- **Child Module:** cualquier módulo referenciado desde el Root Module via bloque `module {}`.
- Los módulos del Registry se versionan — siempre especificar `version` para garantizar reproducibilidad.
- Crear el par de claves `terraform-key` en AWS y actualizar el `vpc_security_group_ids` y `subnet_id` con valores reales de la cuenta.

---

## 01: Introducción
1. Introducción - Module Basics  
  - Root Module
  - Child Module
  - Published Modules (Terraform Registry)

2. Module Basics 
  - Defining a Child Module
    - Source (Mandatory)
    - Version
    - Meta-arguments (count, for_each, providers, depends_on, )
    - Accessing Module Output Values
    - Tainting resources within a module

## 02 - Definir un Child Module
- Tenemos que entender sobre lo siguiente:
  - Module Source (mandatory): Para empezar vamos a utilizar Terraform Registry
  - Module Version (optional): Recomendado usar module version
- Cerar un modulo de instancia EC2 Instance
  - c1-versions.tf: standard
  - c2-variables.tf: standard
  - c3-ami-datasource.tf: standard
  - c4-ec2instance-module.tf: Haremos foco en este template
```t
# AWS EC2 Instance Module

module "ec2_cluster" {
  source                 = "terraform-aws-modules/ec2-instance/aws"
  version                = "~> 2.0"

  name                   = "my-modules-demo"
  instance_count         = 2

  ami                    = data.aws_ami.amzlinux.id
  instance_type          = "t2.micro"
  key_name               = "terraform-key"
  monitoring             = true
  vpc_security_group_ids = ["sg-08b25c5a5bf489ffa"]  # Get Default VPC Security Group ID and replace
  subnet_id              = "subnet-4ee95470" # Get one public subnet id from default vpc and replace
  user_data               = file("apache-install.sh")

  tags = {
    Name        = "Modules-Demo"
    Terraform   = "true"
    Environment = "dev"
  }
}
```

## 03 - Definir Outputs para modulo de instancia EC2
- c5-outputs.tf: Mandara el output de los atributos de la instancia EC2 (Public DNS and Public IP)
```t
# Output variable definitions

output "ec2_instance_public_ip" {
  description = "Public IP addresses of EC2 instances"
  value       = module.ec2_cluster.*.public_ip
}

output "ec2_instance_public_dns" {
  description = "Public IP addresses of EC2 instances"
  value       = module.ec2_cluster.*.public_dns
}
```

## 04 - Ejecutar Terraform Commands
```t
# Terraform Init
terraform init

# Terraform Validate
terraform validate

# Terraform Format
terraform fmt

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve

# Verify 
1) Verificar en la AWS management console si las instancias EC2 fueron creadas
2) Actualizar default security group de la VPN para que permita trafico entrante desde internet al puerto 80
3) Acceder Apache Webserver
http://<Public-IP-VM1>
http://<Public-IP-VM2>
```

## 05 - Tainting Resources en un Module
- El **taint command** se puede utilizar para contaminar recursos específicos dentro de un módulo.
- **Nota importante:** No es posible contaminar un módulo completo. En cambio, cada recurso dentro del módulo debe estar contaminado por separado.
```t
# List Resources from State
terraform state list

# Taint a Resource
terraform taint <RESOURCE-NAME>
terraform taint module.ec2_cluster.aws_instance.this[0]

# Terraform Plan
terraform plan
Observation: First VM will be destroyed and re-created

# Terraform Apply
terraform apply -auto-approve
```

## 06 - Clean-Up Resources & local working directory
```t
# Terraform Destroy
terraform destroy -auto-approve

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## 07 - Meta-Arguments para Modules
- Los meta-arguments funcionan igual que en recursos:
  - count
  - for_each
  - providers
  - depends_on
- [Meta-Arguments for Modules](https://www.terraform.io/docs/language/modules/syntax.html#meta-arguments)


## Referencias
- [Terraform EC2 Instance Module](https://registry.terraform.io/modules/terraform-aws-modules/ec2-instance/aws/latest)

---

Continuar con [09-02 — Build a Terraform Module](../09-02-Terraform-Build-a-Module/README.md)