# Terraform Provisioners

## 00 - Conceptos sobre Provisioners
- Generic Provisioners
  - file
  - local-exec
  - remote-exec
- Provisioner Timings
  - Creation-Time Provisioners (by default)
  - Destroy-Time Provisioners  
- Provisioner Failure Behavior
  - continue
  - fail
- Provisioner Connections
- Provisioner Without a Resource  (Null Resource)

## Prerequisitos
- Crear una EC2 Key pain con el nombre `terraform-key` y copiar el archivo `terraform-key.pem` el la subcarpeta `private-key` en el directorio `terraform-manifest` 
- El bloque de conexión para provisioners usa esto para conectarse a la instancia EC2 recién creada para copiar archivos usando `file provisioner`, execute scripts using `remote-exec provisioner`

## 01 - Introducción
- Entender sobre File Provisioners
- Crear Provisioner Connections block utilizados por los File Provisioners
- Ademas, discutiremos sobre **Creation-Time Provisioners (by default)**
- Discutir sobre Provisioner Failure Behavior
  - continue
  - fail
- Discutir sobre Destroy-Time Provisioners  


## 02 - File Provisioner & Connection Block
- **Directorio:** terraform-manifests
- Entender sobre file provisioner & Connection Block
- **Connection Block**
  - Podemos tener un block de conexión dentro del block de recursos para todos los provisioners o podemos tener un block de conexión dentro de un block de aprovisionamiento para ese aprovisionador respectivo.
- **Self Object**
  - **Nota técnica importante:** Las referencias a recursos están restringidas aquí porque las referencias crean dependencias. Hacer referencia a un recurso por su nombre dentro de su propio block crearía un ciclo de dependencia
  - Las expresiones en los blocks de provisioner no pueden hacer referencia a su recurso principal por su nombre. En su lugar, se puede usar **self object.**
  - El **self object** representa el recurso principal del provisioner y tiene todos los atributos de ese recurso.
```t
  # Connection Block for Provisioners to connect to EC2 Instance
  connection {
    type = "ssh"
    host = self.public_ip # Understand what is "self"
    user = "ec2-user"
    password = ""
    private_key = file("private-key/terraform-key.pem")
  }  
```

## 03 - Crear multiples provisioners de varios tipos
- **Creation-Time Provisioners:** 
- Por defecto, los provisioners se ejecutan cuando se crea el recurso en el que están definidos.
- Los provisioners en tiempo de creación solo se ejecutan durante la creación, no durante la actualización o cualquier otro ciclo de vida.
- Están pensados ​​como un medio para realizar el arranque de un sistema.
- Si falla un provisioners en tiempo de creación, el recurso se marca como contaminado.
- Se planificará un recurso contaminado para su destrucción y recreación cuando se aplique la siguiente Terraform.
- Terraform hace esto porque un provisioners fallido puede dejar un recurso en un estado semi-configurado.
- Dado que Terraform no puede razonar sobre lo que hace el provisioners, la única forma de garantizar la creación adecuada de un recurso es recrearlo. Esto está contaminando.
- Puede cambiar este comportamiento configurando el atributo on_failure, que se describe en detalle a continuación.

```t

 # Copies the file-copy.html file to /tmp/file-copy.html
  provisioner "file" {
    source      = "apps/file-copy.html"
    destination = "/tmp/file-copy.html"
  }

  # Copies the string in content into /tmp/file.log
  provisioner "file" {
    content     = "ami used: ${self.ami}" # Understand what is "self"
    destination = "/tmp/file.log"
  }

  # Copies the app1 folder to /tmp - FOLDER COPY
  provisioner "file" {
    source      = "apps/app1"
    destination = "/tmp"
  }

  # Copies all files and folders in apps/app2 to /tmp - CONTENTS of FOLDER WILL BE COPIED
  provisioner "file" {
    source      = "apps/app2/" # when "/" at the end is added - CONTENTS of FOLDER WILL BE COPIED
    destination = "/tmp"
  }
 # Copies the file-copy.html file to /tmp/file-copy.html
  provisioner "file" {
    source      = "apps/file-copy.html"
    destination = "/tmp/file-copy.html"
  }

  # Copies the string in content into /tmp/file.log
  provisioner "file" {
    content     = "ami used: ${self.ami}" # Understand what is "self"
    destination = "/tmp/file.log"
  }

  # Copies the app1 folder to /tmp - FOLDER COPY
  provisioner "file" {
    source      = "apps/app1"
    destination = "/tmp"
  }

  # Copies all files and folders in apps/app2 to /tmp - CONTENTS of FOLDER WILL BE COPIED
  provisioner "file" {
    source      = "apps/app2/" # when "/" at the end is added - CONTENTS of FOLDER WILL BE COPIED
    destination = "/tmp"
  }
```

## 04 - Crear Resources usando Terraform commands

```t
# Terraform Initialize
terraform init

# Terraform Validate
terraform validate

# Terraform Format
terraform fmt

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve

# Verify - Login to EC2 Instance
chmod 400 private-key/terraform-key.pem 
ssh -i private-key/terraform-key.pem ec2-user@IP_ADDRESSS_OF_YOUR_VM
ssh -i private-key/terraform-key.pem ec2-user@54.197.54.126 (IP EJEMPLO)
Verify /tmp for all files copied
cd /tmp
ls -lrta /tmp

# Clean-up
terraform destroy -auto-approve
rm -rf terraform.tfsate*
```

## 05 - Failure Behavior: Entender Decision making cuando provisioner falla (continue / fail)
- Por defecto, los provisioners que fallan también harán que Terraform se aplique a sí mismo para fallar. La configuración on_failure se puede utilizar para cambiar esto. Los valores permitidos son:
  - **continue:** Ignora el error y continúa con la creación o destrucción.
  - **fail:** (comportamiento por defecto) Genera un error y deja de aplicar (el comportamiento por defecto). Si se trata de un provisioner de creación, contamina el recurso.
- Intente copiar un archivo a la carpeta de contenido estático de Apache "/var/www/html" usando el aprovisionador de archivos
- Esto fallará porque el usuario que está utilizando para copiar estos archivos es "ec2-user" para amazon linux vm. Este usuario no tiene acceso a la carpeta "/var/www/html/" archivos de copia superior.
- Necesitamos usar sudo para hacer eso.
- Todo lo que sabemos es que no podemos copiarlo directamente, pero sabemos que ya hemos copiado este archivo en "/tmp" usando el provisioner de archivos.
- **Dos posibles escenarios**
  - No atributo `on_failure` (mismo que `on_failure = fail`) - por defecto lo que sucede es que se generará un error y dejará de aplicarse. Si se trata de un provisioner de creación, contaminará el recurso.
  - Cuanto el atributo `on_failure = continue`, continuara creando el recurso
  - **Verificar:**  `terraform.tfstate` como  `"status": "tainted"`
```t
# Test-1: Without on_failure attribute which will fail terraform apply
 # Copies the file-copy.html file to /var/www/html/file-copy.html
  provisioner "file" {
    source      = "apps/file-copy.html"
    destination = "/var/www/html/file-copy.html"
   }
###### Verify:  Verify terraform.tfstate for  "status": "tainted"

# Test-2: With on_failure = continue
 # Copies the file-copy.html file to /var/www/html/file-copy.html
  provisioner "file" {
    source      = "apps/file-copy.html"
    destination = "/var/www/html/file-copy.html"
    on_failure  = continue 
   }
###### Verify:  Verify terraform.tfstate for  "status": "tainted"  
```
```t

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve

# Verify
Iniciar sesión en la instancia EC2
Verificar /tmp, /var/www/html y ver los archivos copiados
```

## 06 - Clean-Up Resources & local working directory
```t
# Terraform Destroy
terraform destroy -auto-approve

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

## 07 - Destroy Time Provisioners
- Discutir sobre el concepto
- [Destroy Time Provisioners](https://www.terraform.io/docs/language/resources/provisioners/syntax.html#destroy-time-provisioners)
- Dentro de un provisioner cuando agrega esta declaración `when    = destroy` proporcionará esto durante el tiempo de destrucción de recursos
```t
resource "aws_instance" "web" {
  # ...

  provisioner "local-exec" {
    when    = destroy 
    command = "echo 'Destroy-time provisioner'"
  }
}
```