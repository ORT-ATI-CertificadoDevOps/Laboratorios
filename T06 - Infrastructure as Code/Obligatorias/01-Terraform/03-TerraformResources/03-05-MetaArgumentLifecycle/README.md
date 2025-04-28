# Terraform Resource Meta-Argument lifecycle

## 01 - Introducción
- lifecyle Meta-Argument block contiene 3 argumentos
  - create_before_destroy
  - prevent_destroy
  - ignore_changes
- Entender y implementar cada uno de los anteriores de manera practica paso por paso.

## 02 - lifecyle - create_before_destroy
- Comportamiento por defecto de un Resource: destroy Resource & re-create Resource
- Con Lifecycle Block podemos cambiar esto usando `create_before_destroy=true`
  - Primero el recurso nuevo va a ser creado.
  - Segundo el recurso viejo va a ser eliminado.
  - 
- **Agregar Lifecycle Block en Resource Block para alterar el comportamiento**  
```t
  lifecycle {
    create_before_destroy = true
  }
```  
### 02-01 - Observar comportamiento sin Lifecycle Block
```t
# Switch to Working Directory
cd v1-create_before_destroy

# Initialize Terraform
terraform init

# Validate Terraform Configuration Files
terraform validate

# Format Terraform Configuration Files
terraform fmt

# Generate Terraform Plan
terraform plan

# Create Resources
terraform apply -auto-approve

# Modify Resource Configuration
Change Availability Zone de us-east-1a a us-east-1b

# Apply Changes
terraform apply -auto-approve
Observación:
1) Primero el recurso us-east-1a va a ser destruido.
2) Segundo el recursos us-east-1b se creara.
```
### 02-02 - Con Lifecycle Block
- Agregar Lifecycle block en resource (Descomentar lifecycle block)
```t
# Generate Terraform Plan
terraform plan

# Apply Changes
terraform apply -auto-approve

# Modify Resource Configuration
Cambiar Availability Zone de us-east-1b a us-east-1a

# Apply Changes
terraform apply -auto-approve
Observación: 
1) Primero el recurso us-east-1a se va a crear
2) Segundo el recurso us-east-1b se destruira
```
### 02-03 - Clean-Up Resources
```t
# Destroy Resources
terraform destroy -auto-approve

# Clean-Up 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## 03 - lifecycle - prevent_destroy
### 03-01 - Introducción
- Este meta-argument, cuando se establece en true, hará que Terraform rechace con un error cualquier plan que destruya el objeto de infraestructura asociado con el recurso, siempre que el argumento permanezca presente en la configuración.
- Esto se puede utilizar como una medida de seguridad contra el reemplazo accidental de objetos que pueden ser costosos de reproducir, como instancias de bases de datos.
- Sin embargo, hará que ciertos cambios de configuración sean imposibles de aplicar e impedirá el uso del `terraform destroy` comando una vez que se crean tales objetos, por lo que esta opción debe usarse `sparingly`.
- Dado que este argumento debe estar presente en la configuración para que se aplique la protección, tenga en cuenta que esta configuración no evita que el objeto remoto se destruya si el bloque de recursos se eliminó por completo de la configuración: en ese caso, el `prevent_destroy` se elimina junto con él, por lo que Terraform permitirá que la operación de destrucción tenga éxito.
```t
  lifecycle {
    prevent_destroy = true # Default is false
  }
```
### 03-02 - Ejecutar Terraform Commands
```t
# Switch to Working Directory
cd v2-prevent_destroy

# Initialize Terraform
terraform init

# Validate Terraform Configuration Files
terraform validate

# Format Terraform Configuration Files
terraform fmt

# Generate Terraform Plan
terraform plan

# Create Resources
terraform apply -auto-approve

# Destroy Resource
terraform destroy 
Observación: 
federico.barcelo@globant.com$ terraform destroy -auto-approve
Error: Instance cannot be destroyed
  on c2-ec2-instance.tf line 2:
   2: resource "aws_instance" "web" {
El recurso aws_instance.web tiene seteado lifecycle.prevent_destroy, pero el plan llama for para esto recurso para ser destruido. Para evitar este error y continue con el plan, deshabilite lifecycle.prevent_destroy o reduzca el alcance de la planificar utilizando el flag -target flag.


# Remove/Comment Lifecycle block
- Remove or Comment lifecycle block and clean-up

# Destroy Resource after removing lifecycle block
terraform destroy

# Clean-Up
rm -rf .terraform*
rm -rf terraform.tfstate*
```


## 04 - lifecyle - ignore_changes
### 04-01 - Create an EC2 Changes, make manual changes and understand the behavior
- Create EC2 Instance
```t
# Switch to Working Directory
cd v3-ignore_changes

# Initialize Terraform
terraform init

# Terraform Validate
terraform validate

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
```
### 04-02 - Actualizar el tag desde AWS management console
- Agreger una new tag manualmente a instancia EC2.
- Probar `terraform apply`
- Terraform encontrará la diferencia en la configuración en el lado remoto cuando se compara con el local e intenta eliminar el cambio manual cuando ejecutamos `terraform apply`
```t
# Add new tag manually
WebServer = Apache

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
Observación: 
1) Se va a remover las adiciones que fueron realizadas a mano desde la AWS Management console.
```

### 04-03 - Agregar  lifecyle - ignore_changes block
- Habilitar el block en `c2-ec2-instance.tf`

```t
   lifecycle {
    ignore_changes = [
      # Ignore changes to tags, e.g. because a management agent
      # updates these based on some ruleset managed elsewhere.
      tags,
    ]
  }
```
- Agregar nuevamente tags manualmente usando la AWS mgmt console para la instancia EC2.
```t
# Add new tag manually
WebServer = Apache2
ignorechanges = test1

# Terraform Plan to Verify what it is going to create / update / destroy
terraform plan

# Terraform Apply to Create EC2 Instance
terraform apply 
Observación: 
1) Lo cambios que se agregaron manuales no van a eliminarse, deberían de ser ignorados por Terraform.
2) Verificar que paso en AWS management console.

# Destroy Resource
terraform destroy -auto-approve

# Clean-Up
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## Referencias
- [Resource Meat-Argument: Lifecycle](https://www.terraform.io/docs/language/meta-arguments/lifecycle.html)