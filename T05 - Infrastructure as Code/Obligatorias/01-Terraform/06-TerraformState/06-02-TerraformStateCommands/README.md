# Terraform State Commands

## 01  Introducción
- Terraform Commands
  - terraform show
  - terraform refresh
  - terraform plan
  - terraform state
  - terraform force-unlock   
  - terraform taint
  - terraform untaint
  - terraform apply target command  

## Prerequisitos: c1-versions.tf
- Actualizar el backend block con tu nombre de bucket s3, key y region
- Tambien actualizar el nombre de tu dynamodb table
```t
# Terraform Block
terraform {
  required_version = "~> 0.14" # which means any version equal & above 0.14 like 0.15, 0.16 etc and < 1.xx
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
  # Adding Backend as S3 for Remote State Storage
  backend "s3" {
    bucket = "terraform-DevOpsLab"
    key    = "statecommands/terraform.tfstate"
    region = "us-east-1" 

    # Enable during Step-09     
    # For State Locking
    dynamodb_table = "terraform-dev-state-table"    
    
  }
}
```  

## 02 - Terraform Show Command para revisar Terraform plan files
- El comando `terraform show` se utiliza para proporcionar una salida legible por humanos desde un archivo de estado o plan.
- Esto se puede utilizar para inspeccionar un plan para asegurarse de que se esperan las operaciones planificadas, o para inspeccionar el state actual se encuentra
- Los archivos de salida del plan de Terraform son archivos binarios. Podemos leerlos usando el comando `terraform show`
```t
# Initialize Terraform
terraform init

# Create Plan 
terraform plan -out=v1plan.out

# Read the plan 
terraform show v1plan.out

# Read the plan in json format
terraform show -json v1plan.out
```

## 03 - Terraform Show command para leer State files
- Por defecto, en el directorio de trabajo si tenemos el archivo `terraform.tfstate`, cuando proporcionemos el comando` terraform show`, leerá el archivo de estado automáticamente y mostrará la salida.
```t
# Terraform Show
terraform show
Observación: Deberia de mostrarse "No State" porque todavia no se han generado recursos sobre el directorio de trabajo

# Create Resources
terraform apply -auto-approve

# Terraform Show 
terraform show
Observación: Se debería de desplegar el archivo state 
```

## 04 -  Entender terraform refresh in detail
- Esta comando viene de **Terraform Inspecting State**
- Entender `terraform refresh` despeja muchas dudas en nuestra mente y el archivo state de terraform y la función del state
- El comando de `terraform refresh` se usa para reconciliar el state que conoce Terraform (a través de su archivo de estado) con la infraestructura del mundo real.
- Esto se puede usar para detectar cualquier desviación del último state conocido y para actualizar el archivo de state.
- Esto no modifica la infraestructura, pero modifica el archivo de state. Si se cambia el state, esto puede hacer que ocurran cambios durante el próximo plan o aplicar.
  
- **terraform refresh:** 
Actualiza el archivo de local state con los recursos reales en la nube
- **Desired State:** Local Terraform Manifest (todos los archivos *.tf)
- **Current State:** Recursos presentes en nuestra nube
- **Orden de ejecución:** refresh, plan, make a decision, apply
- ¿Por qué? entender en detalle este orden de ejecución

### 04-01 - Agregar una nueva tag a EC2 Instance usando AWS Management Console
```t
"demotag" = "refreshtest"
```

### 04-02 - Ejecutar terraform plan  
- Se debe de observar que no existen cambios en el local state porque se realiza la comparación en memoria.
- Verificar en el S3 bucket, no deberian de existir cambios
```t
# Execute Terraform plan
terraform plan 
```
### 04-03 - Ejecutar terraform refresh 
- Deberías de observar que el terraform state se actualiza con la nueva etiqueta agregada 
```
# Execute terraform refresh
terraform refresh

# Review terraform state file
1) terraform show
2) Verificar en S3 bucket, una nueva version de terraform.tfsate se tiene que haber generado

```
### 04-04 - Por qué se tuvo que ejecutar en este orden (refresh, plan, make a decision, apply) ?
- Se han producido cambios en su infraestructura de forma manual y no a través de terraform.
- Ahora debe tomar una decisión si desea esos cambios o no..
- **Opción-1:** Si no desea esos cambios, proceda con terraform apply, de modo que se eliminarán los cambios manuales que hemos realizado en nuestra instancia EC2 en la nube.
- **Opción-2:** 
Si desea esos cambios, consulte el archivo `terraform.tfstate` sobre los cambios e agregados en sus manifiestos terraform (ejemplo: c4-ec2-instance.tf) y continuar con el flujo (refresh, plan, make a decision, apply)

### 04-05 - Seleccione la Opción-2, por o cual voy a acutalizar las tags en c4-ec2-instance.tf
- Update in c4-ec2-instance.tf
```t
  tags = {
    "Name" = "amz-linux-vm"
    "demotag" = "refreshtest"
  }
```
### 04-06 - Ejecutar los comandos para dejar nuestro cambio manual en los archivos terraform manifests y tfstate
```t
# Terraform Refresh
terraform refresh

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve
```


## 05 - Terraform State Command
### 05-01 - Terraform State List and Show commands
- Estos dos comandos vienen de **Terraform Inspecting State**
- **terraform state list:**  Este comando se usa para listar recursos dentro de un estado de Terraform.
- **terraform  state show:** Este comando se usa para mostrar los atributos de un solo recurso en el estado de Terraform.
```t
# List Resources from Terraform State
terraform state list

# Show the attributes of a single resource from Terraform State
terraform  state show data.aws_ami.amzlinux
terraform  state show aws_instance.my-ec2-vm
```
### 05-02 - Terraform State mv command
- Este comando viene de **Terraform Moving Resources**
- Este comando moverá un elemento que coincida con la dirección dada a la dirección de destino
- Este comando también puede moverse a una dirección de destino en un archivo de estado completamente diferente
- Es un comando muy peligroso
- Es recomendable usarlo cuando se tiene conocimientos avanzados sobre la herramienta
- Los resultados serán impredecibles si el concepto no es claro sobre los archivos de estado de Terraform, principalmente el estado deseado y el estado actual. 
- Pruebe esto en entornos de producción, solo cuando todo haya funcionado bien en entornos inferiores (UAT, QA, DEV).
```t
# Terraform List Resources
terraform state list

# Terraform State Move Resources to different name
terraform state mv -dry-run aws_instance.my-ec2-vm aws_instance.my-ec2-vm-new 
terraform state mv aws_instance.my-ec2-vm aws_instance.my-ec2-vm-new 
ls -lrta

Observación: 
1) Debería crear un archivo de respaldo de terraform.tfstate como algo como esto "terraform.tfstate.1611929587.backup"
2) Se renombró el nombre de "my-ec2-vm" a "my-ec2-vm-new". 
3) Ejecute terraform plan y observe lo que sucede en la siguiente ejecución de terraform plan y aplique
-----------------------------
# MAL USO 
-----------------------------
# Es una mala practica de hacer terraform plan y apply luego de hacer terraform state mv
# Es necesario hacer una actualización del recurso en los terraform manifest para que matche con el mismo nombre

# Terraform Plan
terraform plan
Observación: Se debe de visualizar "Plan: 1 to add, 0 to change, 1 to destroy."
1 to add: New EC2 Instance will be added
1 to destroy: Old EC2 instance will be destroyed

NO HAGA TERRAFORM APPLY porque muestra hacer cambios. No se modificó nada mas que el nombre local del archivo state de un recurso. Idealmente, nada en el estado actual (el entorno real de la nube no debería cambiar debido a esto)
-----------------------------

-----------------------------
# BUEN USO
-----------------------------
Actualizar "c3-ec2-instance.tf"
Antes: resource "aws_instance" "my-ec2-vm" {
Después: resource "aws_instance" "my-ec2-vm-new" {  

Actualizar todas las referencias de este recurso en los terraform manifests
Actualizar c5-outpus.tf
Antes: value = aws_instance.my-ec2-vm.public_ip
Después: value = aws_instance.my-ec2-vm-new.public_ip

Antes: value = aws_instance.my-ec2-vm.public_dns
Después: value = aws_instance.my-ec2-vm-new.public_dns

Ahora ejecutar terraform and y visualizar como NO hay cambios de infraesructura

# Terraform Plan
terraform plan
Observación: 
1) Mensaje-1: No changes. Infrastructure is up-to-date
2) Message-2: This means that Terraform did not detect any differences between your
configuration and real physical resources that exist. As a result, no
actions need to be performed.
 
```
### 05-03 - Terraform State rm command
- Este comando viene de **Terraform Moving Resources**
- El comando `terraform state rm` es usado para remover los states
- Este comando puede eliminar recursos individuales, instancias únicas de un recurso, módulos completos y más.
```t
# Terraform List Resources
terraform state list

# Remove Resources from Terraform State
terraform state rm -dry-run aws_instance.my-ec2-vm-new
terraform state rm aws_instance.my-ec2-vm-new
Observation: 
1) En primer lugar, realiza una copia de seguridad del archivo de estado actual antes de eliminarlo (ejemplo: terraform.tfstate.1611930284.backup)
2) Lo elimina del archivo terraform.tfstate

# Terraform Plan
terraform plan
Observación: Le dirá que el recurso NO está en el archivo de state, pero el mismo está presente en sus manifiestos terraform (03-ec2-instace.tf - DESIRED STATE). ¿Quieres recrearlo?
Esto volverá a crear una nueva instancia EC2 excluyendo una creada anteriormente y en ejecución.

Tomar una decisión
-------------
Opción 1: desea que este recurso se ejecute en la nube, pero terraform no debe administrarlo. Luego elimine sus referencias en los terraform manifiests (DESIRED STATE). De modo que el que se ejecuta en la nube de AWS (infra actual) esta instancia será independiente de terraform.
Opción 2: desea que se cree un nuevo recurso sin eliminar otro (recurso administrado no terraform ahora en actual state). Ejecute terraform plan y aplique ASÍ NECESITAMOS TOMAR DECISIONES SOBRE CUAL SERÍA NUESTRO RESULTADO DE ELIMINAR UN RECURSO DEL ESTADO.

LA RAZÓN PRINCIPAL para este comando es que el recurso respectivo debe eliminarse como terraform administrado.

# Run Terraform Plan
terraform plan

# Run Terraform Apply
terraform apply 
```

# 05-04 - Terraform State replace-provider command
- Este comando viene de **Terraform Moving Resources**
- [Terraform State Replace Provider](https://www.terraform.io/docs/cli/commands/state/replace-provider.html)


### 05-05 - Terraform State pull / push command
- Este comando viene de **Terraform Disaster Recovery Concept**
- **terraform state pull:** 
  - El comando `terraform state pull`  es usado para descargar de manera manual y realizar output de un remote state.
  - Este comando funciona también con states locales.
  - Este comando descarga el state desde tu ubicación y genere un output en formato raw hacia stdout.
- **terraform state push:** El comando `terraform state push` es usado para subir manualmente un local state para una ubicación remota. 

```t
# Other State Commands (Pull / Push)
terraform state pull
terraform state push
```

## 06 - Terraform force-unlock command
- Este comando viene de **Terraform Disaster Recovery Concept**
- Manualmente desbloquea el state para la configuración definida.
- Esto no modificara la infraestructura.
- Este comando remueve el bloqueo de un state para la configuración actual
- El comportamiento del bloqueo es dependiente del backend (aws s3 con dynamodb para bloqueo, etc)
- **Nota importante:** El state local no puede ser desbloqueado por otro proceso.
```t
# Manually Unlock the State
terraform force-unlock LOCK_ID
```

## Step-07: Terraform taint & untaint commands
- Este comando viene de **Terraform Forcing Re-creation of Resources**
- Cuando se modifica una declaración de recurso, Terraform generalmente intenta actualizar el recurso existente en su lugar (aunque algunos cambios pueden requerir destrucción y recreación, generalmente debido a limitaciones de API).
- **Ejemplo:** Es posible que una máquina virtual que se configura con cloud-init al inicio ya no satisfaga sus necesidades si cambia la configuración de cloud-init.
- **terraform taint:** El comando `terraform taint` marca manualmente un recurso administrado por Terraform como contaminado/defectuoso/innecesario, lo que obliga a ser destruido y recreado en la siguiente aplicación.
- **terraform untaint:** 
  - El comando terraform untaint desmarca manualmente un recurso administrado por Terraform como contaminado/defectuoso/innecesario, restaurándolo como la instancia principal en el state.
  - Esto revierte una alteración de terraformación manual o el resultado de que los aprovisionadores fallan en un recurso.
  - Este comando no modificará la infraestructura, pero modificará el archivo de state para desmarcar un recurso como contaminado/defectuoso/innecesario.
```t
# List Resources from state
terraform state list

# Taint a Resource
terraform taint <RESOURCE_NAME_IN_TERRAFORM_LOCALLY>
terraform taint aws_instance.my-ec2-vm-new

# Terraform Plan
terraform plan
Observation: 
Message: "-/+ destroy and then create replacement"

# Untaint a Resource
terraform untaint <RESOURCE_NAME_IN_TERRAFORM_LOCALLY>
terraform untaint aws_instance.my-ec2-vm-new

# Terraform Plan
terraform plan
Observation: 
Message: "No changes. Infrastructure is up-to-date."
```


## 08 - Terraform Resource Targeting - Plan, Apply (-target) Option
- El flg `-target` se utilizara para que Terraform utilice un X archivo para realizar la acción. 
- [Terraform Resource Targeting](https://www.terraform.io/docs/cli/commands/plan.html#resource-targeting)
- Esta capacidad de orientación se proporciona para circunstancias excepcionales, como recuperarse de errores o trabajar con las limitaciones de Terraform.
- No se recomienda usar `-target` para operaciones de rutina, ya que esto puede llevar a una desviación de configuración no detectada y confusión sobre cómo el verdadero estado de los recursos se relaciona con la configuración.
- En lugar de utilizar `-target` como un medio para operar en porciones aisladas de configuraciones muy grandes, prefiera dividir las configuraciones grandes en varias configuraciones más pequeñas que puedan aplicarse de forma independiente.
```t
# Lets make two changes
Cambio-1: Agregar un new tag en c4-ec2-instance.tf
    "target" = "Target-Test-1"
Cambio-2: Agregar una nueva regla inboud en "vpc-web" security group para 8080
  ingress {
    description = "Allow Port 8080"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
Cambio-3: Agregar nuevo recurso EC2
# New VM
resource "aws_instance" "my-demo-vm" {
  ami           = data.aws_ami.amzlinux.id 
  instance_type = var.instance_type
  tags = {
    "Name" = "demo-vm1"
  }
}

# List Resources from state
terraform state list

# Terraform plan
terraform plan -target=aws_instance.my-ec2-vm-new
Observación:
0) Message: "Plan: 0 to add, 2 to change, 0 to destroy."
1) Está actualizando Cambio-1 porque estamos apuntando a ese recurso "aws_instance.my-ec2-vm-new"
2) Está actualizando cambio-2 "vpc-web" porque es un recurso dependiente para "aws_instance.my-ec2-vm-new"
3) No toca el nuevo recurso que estamos creando ahora. Estará en configuración terraform pero no se aprovisionará cuando estemos usando -target

# Terraform Apply
terraform apply -target=aws_instance.my-ec2-vm-new

```

## 09 - Terraform Destroy & Clean-Up
```t
# Destory Resources
terraform destroy -auto-approve

# Clean-Up Files
rm -rf .terraform*
rm -rf v1plan.out
```

Referencias
- [Terraform State Command](https://www.terraform.io/docs/cli/commands/state/index.html)
- [Terraform Inspect State](https://www.terraform.io/docs/cli/state/inspect.html)
- [Terraform Moving Resources](https://www.terraform.io/docs/cli/state/move.html)
- [Terraform Disaster Recovery](https://www.terraform.io/docs/cli/state/recover.html)
- [Terraform Taint](https://www.terraform.io/docs/cli/state/taint.html)
- [Terraform State](https://www.terraform.io/docs/language/state/index.html)
- [Manipulating Terraform State](https://www.terraform.io/docs/cli/state/index.html)
- [Additional Reference](https://www.hashicorp.com/blog/detecting-and-managing-drift-with-terraform)
