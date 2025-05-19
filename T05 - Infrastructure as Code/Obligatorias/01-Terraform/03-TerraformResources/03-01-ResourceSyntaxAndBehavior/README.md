# Terraform Resource Syntax & Behavior

## 01 - Introducción
- Entender la sintaxis de los Resource.
- Entender el comportamiento de los Resource.
- Entender el Terraform State File.
  - terraform.tfstate
- Entender Desired y Current States (solamente a alto nivel)

## 02 - Entendiendo Resource Syntax
- Vamos a entender sobre los siguientes conceptos desde la perspectiva de Resource Syntax
  - Resource Block
  - Resource Type
  - Resource Local Name
  - Resource Arguments
  - Resource Meta-Arguments

## 03 - Entendiendo Resource Behavior
- Vamos a entender los resource behavior en combinación con Terraform State
  - Create Resource
  - Update in-place Resources
  - Destroy and re-crear Resources
  - Destroy Resource  


## 04 - Resource: Create Resource: Crear EC2 Instance
```
# Initialize Terraform
terraform init

Obvervación: 
1) Descarga exitosa del provider en carpeta .terraform

2) Creado lock file llamado ".terraform.lock.hcl"

# Validate Terraform configuration files
terraform validate
Observación: No archivos cambios / agregados en el directorio.

# Format Terraform configuration files
terraform fmt
Observación: *.tf files van a ser formateadas en caso de existir cambios de formatos a aplicar.

# Review the terraform plan
terraform plan 

Observación-1: No ocurre nada desde la perspectiva del Terraform state

Observación-2: En la perspectiva del Resource Behavior se puede obvervar "+ create" en donde se estan creando

# Create Resources 
terraform apply -auto-approve

Observación: 
1) Se crea archivo terraform.tfstate en el directorio local de trabajo

2) Se crea el recurso definido en AWS Cloud

```

- **Important Note:** Aquí vimos un ejemplo de **Create Resource**


## 05 - Entendiendo Terraform State File
- Qué es Terraform State ? 
  - Es el core principa para que terraform funcione.
  - De forma breve explicado, es la base de datos que contiene la información de los recursos que van a ser aprovisionados usando terraform.
  - **Principal objetivo:** Para almacenar las relaciones entre los objetos en un sistema remoto y las instancias de recursos declaradas en la configuración
  - **Primary Purpose:** To store bindings between objects in a remote system and resource instances declared in your configuration. 
  - Cuando Terraform crea un objeto remoto en respuesta a un cambio de configuración, registrará la identidad de ese objeto remoto en una instancia de recurso en particular, y luego potencialmente actualizará o eliminará ese objeto en respuesta a futuros cambios de configuración.

- Archivo Terraform state es creado cuando se ejecuta por primera vez `terraform apply`
- Archivo Terraform state es creado local en nuestro directorio de trabajo.
- Si requerimos, podemos configurar el `backend block` en `terraform block` que nos permite alojar los states files de manera remota. Es recomendando esta opción, la cual veremos en próximas secciones del laboratorio.
- 
## 06 - Revisar terraform.tfstate file
- Terraform State es un archivo de formato JSON.
- Editar de manera manual esta archivo no es lo más recomendado de hacer.
- Revisar el archivo `terraform.tfstate` para ver los pasos que realiza.


## 07 - Resource: Update In-Place: Realizar cambios agregando tag a EC2 Instance 
- Agregar un nuevo tag en `c2-ec2-instance.tf`
```
# Add this for EC2 Instance tags
    "tag1" = "Update-test-1"
```
- Revisar Terraform Plan
```
# Review the terraform plan
terraform plan 
Observación: Deberías de ver "~ update in-place" 
"Plan: 0 to add, 1 to change, 0 to destroy."

# Create / Update Resources 
terraform apply -auto-approve
Observación: "Apply complete! Resources: 0 added, 1 changed, 0 destroyed."
```
- **Important Note:** Aquí vimos un ejemplo de **update in-place**


## 08 - Resource: Destroy and Re-create Resources: actualizar Availability Zone
- Esto va a destruir la instancia EC2 en 1 AZ y la volvera a recrear en otra AZ.
```
# Before
  availability_zone = "us-east-1a"

# After
  availability_zone = "us-east-1b"  
```
```
# Review the terraform plan
terraform plan 
Observación: 
1) -/+ destroy and then create replacement
2) # aws_instance.my-ec2-vm must be "replaced"
3) # aws_instance.my-ec2-vm must be "replaced" - This parameter forces replacement
4) "Plan: 1 to add, 0 to change, 1 to destroy."

# Create / Update Resources 
terraform apply -auto-approve
Observation: "Apply complete! Resources: 1 added, 0 changed, 1 destroyed."
```


## 09 - Resource: Destroy Resource
```
# Destroy Resource
terraform destroy 
Observación: 
1) - destroy
2) # aws_instance.my-ec2-vm will be destroyed
3) Plan: 0 to add, 0 to change, 1 to destroy
4) Destroy complete! Resources: 1 destroyed
```

## 10 - Entendiendo Desired and Current States (alto nivel)
- **Desired State:** Local Terraform Manifest (All *.tf files)
- **Current State:**  Real Resources presentes en nuestra cloud

## 11 - Clean-Up
```
# Destroy Resource
terraform destroy -auto-approve 

# Remove Terraform Files
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## Referencias
- [Terraform State](https://www.terraform.io/docs/language/state/index.html)
- [Manipulating Terraform State](https://www.terraform.io/docs/cli/state/index.html)