# Terraform local-exec Provisioner

> **Tiempo estimado:** 20 minutos

El `local-exec` provisioner ejecuta comandos en la máquina donde corre Terraform (no en el recurso remoto). Es útil para notificaciones, registrar información de los recursos creados en archivos locales, o invocar scripts de integración externos después del deploy. A diferencia de `remote-exec`, no requiere SSH ni conectividad con el recurso.

### Puntos a tener en consideración
- Los comandos se ejecutan con los permisos del usuario que corre Terraform.
- `working_dir` define el directorio base para los archivos de salida.
- El `destroy-time provisioner` se dispara al ejecutar `terraform destroy` — útil para cleanup o notificaciones de destrucción.

---

## 00 - Prerequisitos
- Crear el par de claves EC2 con el nombre `terraform-key` y copiar el archivo `terraform-key.pem` en la subcarpeta `private-key` del directorio `terraform-manifests`.

## 01 - Introducción
- Entender sobre **local-exec** Provisioner
- El `local-exec` provisioner invoca un ejecutable local luego de que el recurso fue creado.
- Ejecuta un proceso en la máquina que está corriendo Terraform, no en el recurso remoto.

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
Verificar el archivo en carpeta "local-exec-output-files/destroy-time.txt"

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

---

Continuar con [08-04 — Null Resource](../08-04-Null-Resource/README.md)

