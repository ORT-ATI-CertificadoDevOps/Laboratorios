# Terraform remote-exec Provisioner

> **Tiempo estimado:** 25 minutos

El `remote-exec` provisioner ejecuta comandos directamente en el recurso recién creado via SSH. Permite automatizar la configuración del servidor (instalar software, copiar archivos, iniciar servicios) como parte del ciclo de vida del recurso. En este laboratorio copiamos un archivo HTML con el `file provisioner` y luego lo movemos al directorio de Apache usando `remote-exec`.

### Puntos a tener en consideración
- Crear el par de claves EC2 `terraform-key` y copiar `terraform-key.pem` a la subcarpeta `private-key/` antes de comenzar.
- El provisioner requiere conectividad SSH — la instancia necesita tener el puerto 22 abierto en su Security Group.
- Si el servidor tarda en iniciar, agregar un `sleep` al comienzo del bloque `inline` para esperar que esté listo.

---

## 00 - Prerequisitos
- Crear el par de claves EC2 con el nombre `terraform-key` y copiar el archivo `terraform-key.pem` en la subcarpeta `private-key` del directorio `terraform-manifests`.
- El bloque de conexión para provisioners usa estas credenciales para conectarse a la instancia EC2 recién creada.

## 01 - Introducción
- Entender sobre **remote-exec** Provisioner
- El `remote-exec` provisioner invoca comandos en un recurso remoto después de que el recurso fue creado.
- Puede usarse para correr herramientas de configuration management o ejecutar scripts de bootstrap.

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

---

Continuar con [08-03 — local-exec Provisioner](../08-03-local-exec-provisioner/README.md)
