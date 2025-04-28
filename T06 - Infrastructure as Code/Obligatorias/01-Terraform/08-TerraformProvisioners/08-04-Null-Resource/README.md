# Terraform Null Resource

## 01 - Introducción
- Entender sobre [Null Provider](https://registry.terraform.io/providers/hashicorp/null/latest/docs)
- Entender sobre  [Null Resource](https://www.terraform.io/docs/language/resources/provisioners/null_resource.html)
- Entender sobre  [Time Provider](https://registry.terraform.io/providers/hashicorp/time/latest/docs)
- **Caso de uso:** Forzar un recurso a que se actualize basado en un null_resource
- Crear `time_sleep` resource para esperar 90 segundos luego que la instancia EC2 fue creada.
- Crear null resource con los proovisioners requeridos
  - Provisioner: copiar apps/app1 a /tmp
  - Remote Exec Provisioner: Copiar app1 desde /tmp a /var/www/html
- Durante todo el proceso vamos a aprender
  - null_resource
  - time_sleep resource
  - También aprenderemos a forzar un recurso para actualizarse en base a cambios en un null_resource usando `timestamp function` y `triggers` en `null_resource`


## 02 - Definir null provider en Terraform Settings Block
- Update null provider info listed below in **c1-versions.tf**
```t
    null = {
      source = "hashicorp/null"
      version = "~> 3.0.0"
    }
```

## 03 - Definir Time Provider en Terraform Settings Block
- Update time provider info listed below in **c1-versions.tf**
```t
    time = {
      source = "hashicorp/time"
      version = "~> 0.6.0"
    }  
```

## 04 - Crear / Revisar c4-ec2-instance.tf terraform configuration
### 04-01 - Crear Time Sleep Resource
- Este recurso esperara 90 segundos luego de que la instancia EC2 fue creada.
- Este tiempo de espera le dara tiempo a la instancia EC2 para provisionar el Apache Webserver y crear todos los directorios necesarios.
- Primeramente si queremos copiar contenido estatico, debemos de tener sobre nuestro servidor apache el directorio `/var/www/html`
```t
# Wait for 90 seconds after creating the above EC2 Instance 
resource "time_sleep" "wait_90_seconds" {
  depends_on = [aws_instance.my-ec2-vm]
  create_duration = "90s"
}
```
### 04-02 - Crear Null Resource
- Crear un Null resource con `triggers` y función `timestamp()` que servira para triggerear por cada `terraform apply`
- Este `Null resource` nos ayudara a sincronizar el contenido estatico desde nuestro directorio local hacia la instancia EC2 cuando sea requerido.
- Además los cambios solamente se aplicaran usando `null_resource` cuando se ejecute `terraform apply`. En otras palabras, cuando el contenido estatico cambie, como se se actualizara el contenido en nuestra instancia EC2 usando Terraform, a continuación una manera de poder realizarlo.
- El foco en esta parte es hacer enfasis sobre esto:
  - null_resource
  - null_resource trigger
  - How trigger works based on timestamp() function ?
  - Provisioners in Null Resource
```t
# Sync App1 Static Content to Webserver using Provisioners
resource "null_resource" "sync_app1_static" {
  depends_on = [ time_sleep.wait_90_seconds ]
  triggers = {
    always-update =  timestamp()
  }

  # Connection Block for Provisioners to connect to EC2 Instance
  connection {
    type = "ssh"
    host = aws_instance.my-ec2-vm.public_ip 
    user = "ec2-user"
    password = ""
    private_key = file("private-key/terraform-key.pem")
  }  

 # Copies the app1 folder to /tmp
  provisioner "file" {
    source      = "apps/app1"
    destination = "/tmp"
  }

# Copies the /tmp/app1 folder to Apache Webserver /var/www/html directory
  provisioner "remote-exec" {
    inline = [
      "sudo cp -r /tmp/app1 /var/www/html"
    ]
  }
}
```

## 05 - Ejecutar Terraform Commands
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

# Verify
ssh -i private-key/terraform-key.pem ec2-user@<PUBLIC-IP>
ls -lrt /tmp
ls -lrt /tmp/app1
ls -lrt /var/www/html
ls -lrt /var/www/html/app1
http://<public-ip>/app1/file1.html
http://<public-ip>/app1/file2.html
```

## 06 - Crear nuevo archivo local en app1
- Crear nuevo archivo llamado `file3.html`
- Ademas actualizar `file1.html` con alguna información adicional
- **file3.html**
```html
<h1>>App1 File3</h1
```
- **file1.html**
```html
<h1>>App1 File1 - Updated</h1
```

## 07 - Ejecutar Terraform plan y apply commands
```t
# Terraform Plan
terraform plan
Observación: Se deberian de observar cambios para  "null_resource.sync_app1_static" porque el trigger va a pasar a tener un nuevo timestamp cuando ejecutes el terraform plan

# Terraform Apply
terraform apply -auto-approve

# Verify
chmod 400 private-key/terraform-key.pem
ssh -i private-key/terraform-key.pem ec2-user@<PUBLIC-IP>
ls -lrt /tmp
ls -lrt /tmp/app1
ls -lrt /var/www/html
ls -lrt /var/www/html/app1
http://<public-ip>/app1/file1.html
http://<public-ip>/app1/file3.html
```

## 08 - Clean-Up Resources & local working directory
```t
# Terraform Destroy
terraform destroy -auto-approve

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```



## Referencias
- [Resource: time_sleep](https://registry.terraform.io/providers/hashicorp/time/latest/docs/resources/sleep)
- [Time Provider](https://registry.terraform.io/providers/hashicorp/time/latest/docs)
