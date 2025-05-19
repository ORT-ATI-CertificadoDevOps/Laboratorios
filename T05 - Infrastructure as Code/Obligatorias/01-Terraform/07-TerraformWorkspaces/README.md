# Terraform Workspaces

## 01 - Introducción
- Vamos a crear 2 workspaces (dev,qa) ademas del workspace que viene por defecto
- Actualizar los terraform manifestst para que soporten `terraform workspace`
  - Para los security group, el nombre tiene que ser unico para cada workspace
  - De la misma manera para las intancias EC2 el nombre del tag
- Los siguientes opciones son posibles para el comando `terraform workspace`
  - terraform workspace show
  - terraform workspace list
  - terraform workspace new
  - terraform workspace select
  - terraform workspace delete


## 02 - Actualizar terraform manifests para soportar multiple workspaces
- **Directorio:** v1-local-backend
- Idealmente, AWS no permite crar security groups con el mismo nombre dos veces.
- Por esto, necesitamos cambiar el nombre de nuestro security group en `c2-security-groups.tf`
- También para mantener una nomenclatura clara deberíamos de cambiar nuesro `Name tag` para nuestras instancias EC2, para mentener una consistencia con los workspaces.
- ¿Qué es? **${terraform.workspace}**? - Va a tener el nombre del workspace en donde vamos a trabajar
- **Uso-1:** Usar el workspace como parte del manejo de nombres y tags 
- **Uso-2:** Referenciar el workspace es de utilidad para obtener ciertos comportamientos dependiendo el ambiente. 
```t
# Change-1: Security Group Names
  name        = "vpc-ssh-${terraform.workspace}"
  name        = "vpc-web-${terraform.workspace}"  

# Change-2: For non-default workspaces, it may be useful to spin up smaller cluster sizes.
  count = terraform.workspace == "default" ? 2 : 1  
This will create 2 instances if we are in default workspace and in any other workspaces it will create 1 instance

# Change-3: EC2 Instance Name tag
    "Name" = "vm-${terraform.workspace}-${count.index}"

# Change-4: Outputs
  value = aws_instance.my-ec2-vm.*.public_ip
  value = aws_instance.my-ec2-vm.*.public_dns 


Puede crear una lista de todos los valores de un atributo determinado para los elementos de la colección con *. Por ejemplo, aws_instance.my-ec2-vm. *. Id será una lista de todas las IP públicas de las instancias.   
```

## 03 - Create resources in default workspaces
- Default Workspace: Todo directorio inicializado tiene al menos generado 1 workspace
- Si no generaste ningun workspace, por defecto vas a tener el llamado como **default**
- Por directorio, solamente un workspace puede ser seleccionado al mismo tiempo.
- La gran mayoria de comandos (incluido comandos de provisioning y state) solamente interacturan con el workspace seleccionado
```t
# Terraform Init
terraform init 

# List Workspaces
terraform workspace list

# Output Current Workspace using show
terraform workspace show

# Terraform Plan
terraform plan
Observación: Esto debería de mostrarte dos instancias EC2 "count = terraform.workspace == "default" ? 2 : 1" porque estamos creando esto en el workspace default

# Terraform Apply
terraform apply -auto-approve

# Verify
Verificar en la consola de AWS
Observacion: 
1) Dos intancias fueron creadas con los nombres (vm-default-0, vm-default-1)
2) Security Groups se deberion de crear con los nombres (vpc-ssh-default, vpc-web-default)
3) Observar el output en el CLI, se deberia de observaqr la lista de las Public IP y Public DNS
```

## 04 - Crear New Workspace and provisionar Infra 
```t
# Create New Workspace
terraform workspace new dev

# Verify the folder
cd terraform.tfstate.d 
cd dev
ls
cd ../../

# Terraform Plan
terraform plan
Obsevación: Esto solamente debería de crear solamente 1 instancia "count = terraform.workspace == "default" ? 2 : 1" por estar usando un non-default workspace, llamado dev

# Terraform Apply
terraform apply -auto-approve

# Verify Dev Workspace statefile
cd terraform.tfstate.d/dev
ls
cd ../../
Observación: Deberías de encontrar "terraform.tfstate" en el directorio "current-working-directory/terraform.tfstate.d/dev"

# Verify EC2 Instance in AWS mgmt console
Observación:
1) El nombre debe de ser "vm-dev-0"
2) Security Group debe de llamarse "vpc-ssh-dev, vpc-web-dev"
```

## 05 - Cambiar workspace y destruir resources
- Cambiar de workspace dev a workspace default y destruir los recursos en el workspace default
```t
# Show current workspace
terraform workspace show

# List Worksapces
terraform workspace list

# Workspace select
terraform workspace select default

# Delete Resources from default workspace
terraform destroy 

# Verify
Verificar en la consola de AWS que las 2 instancias y los security group fueron eliminados
```

## 06 - Delete dev workspace
- NO se puede eliminar el workspace default
- SI se pueden eliminar otras workspaces que fueron generados (en este caso, dev)
```t
# Delete Dev Workspace
terraform workspace delete dev
Observación: Workspace "dev" is not empty.
Deleting "dev" can result in dangling resources: resources that
exist but are no longer manageable by Terraform. Please destroy
these resources first.  If you want to delete this workspace
anyway and risk dangling resources, use the '-force' flag.

# Switch to Dev Workspace
terraform workspace select dev

# Destroy Resources
terraform destroy -auto-approve

# Delete Dev Workspace
terraform workspace delete dev
Observación:
Workspace "dev" is your active workspace.
You cannot delete the currently active workspace. Please switch
to another workspace and try again.

# Switch Workspace to default
terraform workspace select default

# Delete Dev Workspace
terraform workspace delete dev
Observación: Successfully delete workspace dev

# Verify 
Verificar en la consola de AWS que todas las instancias EC2 fueron eliminadas.
```

## 07 - Clean-Up Local folder
```t
# Clean-Up local folder
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## 08 - Terraform Workspaces en combinación con Terraform Backend (Remote State Storage)
### 08-01 - Revisar terraform manifest (primarily c1-versions.tf)
- **Directorio:** v2-remote-backend
- Solamente cambiar en el template **c1-versions.tf**, vamos a tener el remote backend block que aprendimos durante este sección.**07-01-Terraform-Remote-State-Storage-and-Locking**
```t
  # Adding Backend as S3 for Remote State Storage
  backend "s3" {
    bucket = "terraform-DevOpsLab"
    key    = "workspaces/terraform.tfstate"
    region = "us-east-1"  
  # For State Locking
    dynamodb_table = "terraform-dev-state-table"           
  }
```
### 08-02 - Provisionar infra usando default workspace
```t
# Initialize Terraform
terraform init

# List Terraform Workspaces
terraform workspace list

# Show current Terraform workspace
terraform workspace show

# Terraform Validate
terraform validate

# Terraform Format
terraform fmt

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve

# Review State file in S3 Bucket for default workspace
Ir a AWS console -> Services -> S3 -> terraform-DevOpsLab -> workspaces -> terraform.tfstate

```
### 08-03 - Crear new workspace dev y provisionar infra using dicho workspace
```t
# List Terraform Workspaces
terraform workspace list

# Create New Terraform Workspace
terraform workspace new dev

# List Terraform Workspaces
terraform workspace list

# Show current Terraform workspace
terraform workspace show

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve

# Review State file in S3 Bucket for dev workspace
Ir a AWS Mgmt Console -> Services -> S3 -> terraform-DevOpsLab -> env:/ -> dev -> workspaces -> terraform.tfstate
```

### 08-04 - Destruir resources en ambos workspaces (default, dev)

```t
# Show current Terraform workspace
terraform workspace show

# Destroy Resources in Dev Workspace
terraform destroy -auto-approve

# Show current Terraform workspace
terraform workspace show

# Select other workspace
terraform workspace select default

# Show current Terraform workspace
terraform workspace show

# Destroy Resources in default Workspace
terraform destroy -auto-approve

# Delete Dev Workspace
terraform workspace delete dev
```
### 08-05 - Intentar eliminar default workspace and entender que pasa

```t
# Try deleting default workspace
terraform workspace delete default
Observación: 
1) Workspace "default" is your active workspace.
2) You cannot delete the currently active workspace. Please switch
to another workspace and try again.

# Create new workspace
terraform workspace new test1

# Show current Terraform workspace
terraform workspace show

# Try deleting default workspace now
terraform workspace delete default
Observación:
1) can't delete default state
```

### 08-06 - Clean-Up
```t
# Switch workspace to default
terraform workspace select default

# Delete test1 workspace
terraform workspace delete test1

# Clean-Up Terraform local folders
rm -rf .terraform*

# Clean-Up State files in S3 bucket 
Go to S3 Bucket and delete files
```


## Referencias
- [Terraform Workspaces](https://www.terraform.io/docs/language/state/workspaces.html)
- [Managing Workspaces](https://www.terraform.io/docs/cli/workspaces/index.html)