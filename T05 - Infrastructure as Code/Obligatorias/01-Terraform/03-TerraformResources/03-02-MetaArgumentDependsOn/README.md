# Terraform Resource Meta-Argument depends_on

## 01 - Introduction
- Crear 9 aws resources paso por paso
- Crear Terraform Block
- Crear Provider Block
- Crear 9 Resource Blocks
  - Crear VPC
  - Crear Subnet
  - Crear Internet Gateway
  - Crear Route Table
  - Crear Route en Route Table para acceso a internet
  - Asociar Route Table en Subnet
  - Crear Security Group en la VPC con los puertos 80, 22 como entrantes open
  - Crear instancia EC2 en la nueva vpc, nueva subnet creada anteriormente con static key pair, asociar Security group creado anteriormente
  - Crear Elastic IP Address y asociarla a la instancoa EC2
  - Usar `depends_on` Resource Meta-Argument cuando creamos Elastic IP  

## 02: Pre-requisito - Crear a EC2 Key Pair
- Crear EC2 Key pair `terraform-key` y descargar archivo pem y dejarlo listo con permisos para realizar conexión SSH
>Nota: Mirar en el siguiente link como generar las claves [EC2 Key pairs](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#having-ec2-create-your-key-pair)

## 03 - c1-versions.tf - Crear Terraform & Provider Blocks 
- Crear Terraform Block
- Crear Provider Block
```
# Terraform Block
terraform {
  required_version = "~> 0.14.6"
  required_providers {
    aws = { 
      source = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
}

# Provider Block
provider "aws" {
  region = "us-east-1"
  profile = "default"
}
```
## 04 - c2-vpc.tf - Crear VPC Resources 
### 04-01: Crear VPC usando AWS Management Console
- Crear una VPC de forma manual desde la consola y entender todos los recursos que se van a crear. Eliminar la VPC creada ya que empezaremos a escribir el template de creación para un VPC con Terraform
### 04-02: Crear VPC usando Terraform
- Crear VPC Resources listed below  
  - Crear VPC
  - Crear Subnet
  - Crear Internet Gateway
  - Crear Route Table
  - Crear Route en Route Table para acceso a internet
  - Asociar Route Table con la Subnet
  - Crear Security Group en la VPC con puertos 80, 22 como entrantes open
```
# Resource Block
# Resource-1: Crear VPC
resource "aws_vpc" "vpc-dev" {
  cidr_block = "10.0.0.0/16"

  tags = {
    "name" = "vpc-dev"
  }
}

# Resource-2: Crear Subnets
resource "aws_subnet" "vpc-dev-public-subnet-1" {
  vpc_id = aws_vpc.vpc-dev.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "us-east-1a"
  map_public_ip_on_launch = true
}


# Resource-3: Crear Internet Gateway
resource "aws_internet_gateway" "vpc-dev-igw" {
  vpc_id = aws_vpc.vpc-dev.id
}

# Resource-4: Crear Route Table
resource "aws_route_table" "vpc-dev-public-route-table" {
  vpc_id = aws_vpc.vpc-dev.id
}

# Resource-5: Crear Route en Route Table para acceso a internet
resource "aws_route" "vpc-dev-public-route" {
  route_table_id = aws_route_table.vpc-dev-public-route-table.id 
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.vpc-dev-igw.id 
}


# Resource-6: Asociar Route Table con la Subnet
resource "aws_route_table_association" "vpc-dev-public-route-table-associate" {
  route_table_id = aws_route_table.vpc-dev-public-route-table.id 
  subnet_id = aws_subnet.vpc-dev-public-subnet-1.id
}

# Resource-7: Crear Security Group
resource "aws_security_group" "dev-vpc-sg" {
  name = "dev-vpc-default-sg"
  vpc_id = aws_vpc.vpc-dev.id
  description = "Dev VPC Default Security Group"

  ingress {
    description = "Allow Port 22"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow Port 80"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all ip and ports outboun"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

}
```

## 05 - c3-ec2-instance.tf - Crear instancia EC2 Resource
- Review `apache-install.sh`
```sh
#! /bin/bash
sudo yum update -y
sudo yum install -y httpd
sudo service httpd start  
sudo systemctl enable httpd
echo "<h1>Welcome to Practico Terraform de Federico ! AWS Infra created using Terraform in us-east-1 Region</h1>" > /var/www/html/index.html
```
- Crear EC2 Instance Resource
```
# Resource-8: Crear instancia EC2
resource "aws_instance" "my-ec2-vm" {
  ami = "ami-0be2609ba883822ec" # Amazon Linux
  instance_type = "t2.micro"
  subnet_id = aws_subnet.vpc-dev-public-subnet-1.id
  key_name = "terraform-key"
	#user_data = file("apache-install.sh")
  user_data = <<-EOF
    #!/bin/bash
    sudo yum update -y
    sudo yum install httpd -y
    sudo systemctl enable httpd
    sudo systemctl start httpd
    echo "<h1>Welcome to Practico Terraform de Federico ! AWS Infra created using Terraform in us-east-1 Region</h1>" > /var/www/html/index.html
    EOF  
  vpc_security_group_ids = [ aws_security_group.dev-vpc-sg.id ]
}
```

## 06 - c4-elastic-ip.tf - Crear Elastic IP Resource
- Crear Elastic IP Resource
- Agregar un Resource Meta-Argument `depends_on` para asugurarnos de que el el resource Elastic IP solamente se cree cuanto el AWS Internet Gateway esta presente o fue creado
```
# Resource-9: Crear Elastic IP
resource "aws_eip" "my-eip" {
  instance = aws_instance.my-ec2-vm.id
  vpc = true
  depends_on = [ aws_internet_gateway.vpc-dev-igw ]
}
```

## 07 - Ejecutar Terraform commands para Crear los Resources usando Terraform
```
# Initialize Terraform
terraform init

# Terraform Validate
terraform validate

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
```

## 08 - Verificar los Resources
- Verificar VPC
- Verificar EC2 Instance
- Verificar Elastic IP
- Revisar archivo `terraform.tfstate`
- Acceder al Apache Webserver Static usando la Elastic IP
```
# Access Application
http://<AWS-ELASTIC-IP>
```

## 09 - Destroy Terraform Resources
```
# Destroy Terraform Resources
terraform destroy

# Remove Terraform Files
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## Referencias 
- [Elastic IP creation depends on VPC Internet Gateway](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/eip)
- [Resource: aws_vpc](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc)
- [Resource: aws_subnet](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/subnet)
- [Resource: aws_internet_gateway](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/internet_gateway)
- [Resource: aws_route_table](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route_table)
- [Resource: aws_route](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route)
- [Resource: aws_route_table_association](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route_table_association)
- [Resource: aws_security_group](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/security_group)
- [Resource: aws_instance](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance)
- [Resource: aws_eip](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/eip)