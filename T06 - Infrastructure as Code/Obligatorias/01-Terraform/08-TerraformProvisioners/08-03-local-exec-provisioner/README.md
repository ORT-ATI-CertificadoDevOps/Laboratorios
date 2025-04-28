# Terraform local-exec Provisioner

## 00: Prerequisitos
- Crear una EC2 Key pain con el nombre `terraform-key` y copiar el archivo `terraform-key.pem` el la subcarpeta `private-key` en el directorio `terraform-manifest` 
- El bloque de conexión para provisioners usa esto para conectarse a la instancia EC2 recién creada para copiar archivos usando `file provisioner`, execute scripts using `remote-exec provisioner`

## 01 - Introducción
- Entender sobre **local-exec** Provisioner
- El `local-exec` provisioner invoca a un ejecutable local luego de que el recurso fue creado.
- Esto invoca a un proceso en la máquina que esta corriendo Terraform, no en le recurso.

## 02 - Review local-exec provisioner code
- Vamos a crear un provisioner durante el creation-time. El cual va a mandar como output la ip privada de la instancia a un archivo llamado `creation-time-private-ip.txt`
- Vamos a crear otro provisioner que se utilizara durante el detroy-time. El cual va a mandar como output el dia a un archivo llamado `destroy-time.txt`
- **C3-ec2-instance.tf**
```t
  # local-exec provisioner (Creation-Time Provisioner - Triggered during Create Resource)
  provisioner "local-exec" {
    command = "echo ${aws_instance.my-ec2-vm.private_ip} >> creation-time-private-ip.txt"
    working_dir = "local-exec-output-files/"
    #on_failure = continue
  }

  # local-exec provisioner - (Destroy-Time Provisioner - Triggered during Destroy Resource)
  provisioner "local-exec" {
    when    = destroy
    command = "echo Destroy-time provisioner Instanace Destroyed at `date` >> destroy-time.txt"
    working_dir = "local-exec-output-files/"
  }  
```


## 03: Revisar Terraform manifests & ejecutar Terraform Commands
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
Verificar el archivo en carpeta "local-exe-output-files/creation-time-private-ip.txt"

```
## 04 - Clean-Up Resources & local working directory
```t
# Terraform Destroy
terraform destroy -auto-approve

# Verify
Verificar el archivo en carpeta "local-exe-output-files/destroy-time.txt"

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

