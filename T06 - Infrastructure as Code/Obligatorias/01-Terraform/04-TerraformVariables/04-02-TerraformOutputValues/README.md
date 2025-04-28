# Terraform Output Values

## 01 - Introducción
- Entender sobre Output Value y como se implementam
- Query outputs usando `terraform output`
- Entender como utilizar secure attributes en output values
- Generar machin-readable output

## 02 - Basics of Output Values
- **Directorio:** terraform-manifests
- Entender sobre Output Value
- Se pueden exportar los Argument & Attribute References

```t
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 

# Create Resources
terraform apply -auto-approve

# Access Application
http://<Public-IP>
http://<Public-DNS>
```

## 03 - Query Terraform Outputs
- Terraform cargará el estado del proyecto en el archivo de estado, de modo que usando el comando `terraform output` para que podamos consultar el estado del archivo.
```t
# Terraform Output Commands
terraform output
terraform output ec2_publicdns
```


## 04 - Output Values - Suppressing Sensitive Values in Output
- Podemos escribir para suprimir el output de datos sensibles utilizando `sensitve = true` en el output block
- Esto solo redactará la salida cli para terraform plan y se aplicará
- Cuando consultas usando `terraform output`
podrá obtener valores exactos del archivo `terraform.tfstate` 
- Agregar `sensitve = true` para output de `ec2_publicdns`
```t
# Attribute Reference - Create Public DNS URL with http:// appended
output "ec2_publicdns" {
  description = "Public DNS URL of an EC2 Instance"
  value = "http://${aws_instance.my-ec2-vm.public_dns}"
  sensitive = true
}
```
- Validar el flujo
```t
# Terraform Apply
terraform apply -auto-approve
Observación: Se debería ver el dato como sensible

# Query using terraform output
terraform output ec2_publicdns
Observación: Deberías de ver un non-redacted original value desde el archivo terraform.tfstate
```

## 05 - Generar machine-readable output
```t
# Generate machine-readable output
terraform output -json
```

## 06 - Destroy Resources
```t
# Destroy Resources
terraform destroy -auto-approve

# Clean-Up
rm -rf .terraform*
rm -rf terraform.tfstate*
```


## Referencias
- [Terraform Output Values](https://www.terraform.io/docs/language/values/outputs.html)