# Terraform Resource Meta-Argument count

## 01 - Introducción
- Entender Resource Meta-Argument `count`.
- Además implementar count y count index de manera practica.

## 02 - Crear 5 instancias EC2 usando Terraform
- Por lo general, para generar 1 instancia de EC2 en Terraform tenemos que tener definido un Resource.
- Por lo cual, 5 instancias de EC2 en Terraform tenemos que tener definidos 5 Resources.
- Con `Meta-Argument count` la creación de 5 instancias EC2 se vuelve de manera muy facil.
- Veamos de que manera: 
```t
# Create EC2 Instance
resource "aws_instance" "web" {
  ami = "ami-047a51fa27710816e" # Amazon Linux
  instance_type = "t2.micro"
  count = 5
  tags = {
    "Name" = "web"
  }
}
```
- **Ejecutar Terraform Commands**
```t
# Initialize Terraform
terraform init

# Terraform Validate
terraform validate

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
```
- Verificar instancia EC2 y su nombre


## 03 - Entendiendo sobre count index
- Si visualizarmos, todas nuestras instancias EC2 tienen el mismo nombre `web`
- Vamos a cambiarles el nombre usando count index a `web-0, web-1, web-2, web-3, web-4`
```t
# Create EC2 Instance
resource "aws_instance" "web" {
  ami = "ami-047a51fa27710816e" # Amazon Linux
  instance_type = "t2.micro"
  count = 5
  tags = {
    #"Name" = "web"
    "Name" = "web-${count.index}"
  }
}
```
- **Ejecutar Terraform Commands**
```t
# Terraform Validate
terraform validate

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
```
- Verificar instancias EC2


## 04 - Destroy Terraform Resources
```
# Destroy Terraform Resources
terraform destroy

# Remove Terraform Files
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## Referencias
- [Resources: Count Meta-Argument](https://www.terraform.io/docs/language/meta-arguments/count.html)