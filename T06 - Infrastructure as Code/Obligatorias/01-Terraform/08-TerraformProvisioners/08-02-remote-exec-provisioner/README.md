# Terraform remote-exec Provisioner

## 00: Prerequisitos
- Crear una EC2 Key pain con el nombre `terraform-key` y copiar el archivo `terraform-key.pem` el la subcarpeta `private-key` en el directorio `terraform-manifest` 
- El bloque de conexión para provisioners usa esto para conectarse a la instancia EC2 recién creada para copiar archivos usando `file provisioner`, execute scripts using `remote-exec provisioner`

## 01 - Introduction
- Entender sobre **remote-exec** Provisioner
- El `remote-exec` provisioner invoca un script en un remote resource despues que el recurso se creo.
- Esto puede usarse para correr un configuration management tool, 
- invokes a script on a remote resource after it is created. 
- This can be used to run a configuration management tool.

## 02 - Crear / Revisar Provisioner configuration
1. Copiar el archivo `file-copy.html` usando `File Provisioner` al directorio "/tmp"
2. Usando `remote-exec provisioner`, usando los comandos de Linux, a su vez copiaremos el archivo al directorio de contenido estático del servidor web Apache `/var/www/html` y acceda a él a través del navegador una vez que esté aprovisionado
```t
 # Copies the file-copy.html file to /tmp/file-copy.html
  provisioner "file" {
    source      = "apps/file-copy.html"
    destination = "/tmp/file-copy.html"
  }

# Copies the file to Apache Webserver /var/www/html directory
  provisioner "remote-exec" {
    inline = [
      "sleep 120",  # Will sleep for 120 seconds to ensure Apache webserver is provisioned using user_data
      "sudo cp /tmp/file-copy.html /var/www/html"
    ]
  }
```

## 03 - Revisar Terraform manifests & ejecutar Terraform Commands
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
1) Hacer log in en instancia EC2
chmod 400 private-key/terraform-key.pem 
ssh -i private-key/terraform-key.pem ec2-user@IP_ADDRESSS_OF_YOUR_VM
ssh -i private-key/terraform-key.pem ec2-user@54.197.54.126

2) Verificar /tmp por archivo file-copy.html (ls -lrt /tmp/file-copy.html)
3) Verificar /var/www/html por archivo file-copy.html (ls -lrt /var/www/html/file-copy.html)
4) Acceder via browser http://<Public-IP>/file-copy.html
```
## 04 - Clean-Up Resources & local working directory
```t
# Terraform Destroy
terraform destroy -auto-approve

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

