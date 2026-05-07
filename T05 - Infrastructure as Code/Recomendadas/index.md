# T05 IaC — Recomendadas

## Terragrunt

Terragrunt es un wrapper sobre Terraform que resuelve dos problemas recurrentes en proyectos grandes: el DRY del backend (no repetir el bloque `backend "s3"` en cada módulo) y la orquestación de dependencias entre módulos.

**Para explorar:**
- Instalar Terragrunt y entender `terragrunt.hcl`
- Configurar un backend remoto común para múltiples módulos
- Usar `dependency` blocks para encadenar outputs entre módulos
- Ejecutar `terragrunt run-all apply` sobre un directorio multi-módulo

## OpenTofu

OpenTofu es el fork open-source de Terraform mantenido por la Linux Foundation, surgido como respuesta al cambio de licencia de HashiCorp (BSL). Es drop-in compatible con Terraform 1.5 y agrega funcionalidades como state encryption nativa.

**Para explorar:**
- Instalar OpenTofu (`brew install opentofu`)
- Migrar una configuración existente de Terraform a OpenTofu (`tofu init`)
- Comparar la paridad de comandos con Terraform

## Pulumi

Pulumi es una alternativa a Terraform donde la infraestructura se define en lenguajes de programación reales (TypeScript, Python, Go, C#) en lugar de HCL. Es especialmente útil cuando la lógica de la infraestructura es compleja y requiere abstracciones que HCL no soporta bien.

**Para explorar:**
- Crear una cuenta en pulumi.com
- Definir una instancia EC2 en TypeScript con Pulumi
- Comparar el modelo de estado de Pulumi con el de Terraform

## Ansible

Ansible complementa a Terraform: Terraform provisiona la infraestructura (qué recursos existen), Ansible la configura (qué software corre en esos recursos). En pipelines reales es común usar ambos en conjunto.

**Para explorar:**
- Instalar Ansible (`brew install ansible`)
- Crear un inventario estático con una IP de EC2
- Escribir un playbook que instale nginx y copie una página HTML
- Usar el plugin de inventario dinámico de AWS para reemplazar el inventario estático

## Checkov — IaC Security

Checkov escanea archivos Terraform (y otros formatos de IaC) en busca de malas configuraciones de seguridad: buckets S3 públicos, security groups abiertos, encryption deshabilitada, etc. Está cubierto en el módulo T06-DevSecOps, pero es especialmente relevante practicarlo aquí sobre el código que generaron en estos labs.

**Para explorar:**
- `pip install checkov`
- `checkov -d .` sobre el directorio de cualquier lab de este módulo
- Revisar los findings y corregir al menos uno
