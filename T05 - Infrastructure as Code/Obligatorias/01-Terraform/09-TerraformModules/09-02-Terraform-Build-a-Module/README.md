# Build a Terraform Module

## 01: Introducción
- Build a Terraform Module
    - Crear a Terraform module
    - Usar local Terraform modules en la configuración
    - Configurar modules con variables
    - Usar module outputs
    - Vamos a crear un local re-usable module para el caso de uso
- **Caso de uso: Hosting a static website with AWS S3 buckets**
1. Creaer un S3 Bucket
2. Crear una Public Read policy para el bucket
3. Con 1 y 2 vamos a crear un re-usable module en Terraform
- **¿Cómo vamos a realizar esto?**
- Vamos a realizar esto en tres secciones
- **Sección-1 - Full Manual:** Crear una página para contenido estatico en S3 utilizando la AWS console
- **Sección-2 - Terraform Resources:** Automatizar la Sección-1 utilizando Terraform resources
- **Sección-3 - Terraform Modules:** Cerear un re-usable module para hostear una página de contenido estatico referenciando los archivos utilizados en la Sección-2

## 02 - Hosting a Static Website con AWS S3 usando AWS Management Console
- **Directorio:** v1-create-static-website-on-s3-using-aws-mgmt-console
- Vamos a hostear una página con contenido estatico en S3 utilizando la AWS console
### 02-01: Crear AWS S3 Bucket
- Ir a AWS Services -> S3 -> Create Bucket 
- **Bucket Name:** mybucket-xxxx (Nota: el nombre de bucket tiene que ser UNICO sobre todo AWS)
- **Region:** US.East (N.Virginia)
- Dejar el resto pore defecto
- Click on **Create Bucket**

### 02-02: Habilitar Static website hosting
- Ir a AWS Services -> S3 -> Buckets -> mybucket-xxxx -> Properties Tab -> At the end
- Edit to enable **Static website hosting**
- **Static website hosting:** enable
- **Index document:** index.html
- Click on **Save Changes**

### 02-03 - Sacar Block public access (bucket settings)
- Ir a AWS Services -> S3 -> Buckets -> mybucket-xxxx -> Permissions Tab 
- Editar **Block public access (bucket settings)** 
- Uncheck **Block all public access**
- Click on **Save Changes**
- Proveer texto `confirm` y Click on **Confirm**

### 02-04 - Agregar Bucket policy para public read por bucket owners
- Actualizar el nombre de tu bucket en la policy de a continuación
- **Archivo:** v1-create-static-website-on-s3-using-aws-mgmt-console/policy-public-read-access-for-website.json
```json
{
  "Version": "2012-10-17",
  "Statement": [
      {
          "Sid": "PublicReadGetObject",
          "Effect": "Allow",
          "Principal": "*",
          "Action": [
              "s3:GetObject"
          ],
          "Resource": [
              "arn:aws:s3:::mybucket-xxxx/*"
          ]
      }
  ]
}
```
- Ir a AWS Services -> S3 -> Buckets -> mybucket-xxxx -> Permissions Tab 
- Editar -> **Bucket policy** -> Copiar y pegar la policy anterior con el nombre del bucket alterado
- Click on **Save Changes**

### 02-05 - Upload index.html
- **Archivo:** v1-create-static-website-on-s3-using-aws-mgmt-console/index.html
- Ir a AWS Services -> S3 -> Buckets -> mybucket-xxxx -> Objects Tab 
- Upload **index.html**

### 02-06 - Acceder Static Website utilizando S3 Website Endpoint
- Acceder el nuevo archivo subido index.html al S3 bucket utilizando un browser
```
# Endpoint Format
http://example-bucket.s3-website.Region.amazonaws.com/

# Replace Values (Bucket Name, Region)
http://mybucket-xxxx.s3-website.us-east-1.amazonaws.com/
```

### 02-07 - Conclusión
- Tuvimos que realizar varios pasos manuales para poder hostear una página con contenido estatico en AWS.
- Ahora, vamos a automatizar todos estos pasos anteriores utilizando Terraform.

## 03 - Crear Terraform Configuration para Hostear un Static Website en AWS S3
- **Directorio:** v2-host-static-website-on-s3-using-terraform-manifests
- Vamos a hostear una página estatic en S3 utilizando Terraform
- 
### 03-01: Crear Terraform Configuration Files step by step
1. versions.tf
2. main.tf
3. variables.tf
4. outputs.tf
5. terraform.tfvars

### 03-02 - Ejecutar Terraform Commands & verificar en el bucket
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
1. Bucket tiene static website hosting habilitado
2. Bucket tiene public read access habilitado using policy
3. Bucket tiene "Block all public access" unchecked
```

### 03-03 - Upload index.html and test
```
# Endpoint Format
http://example-bucket.s3-website.Region.amazonaws.com/

# Replace Values (Bucket Name, Region)
http://mybucket-1046.s3-website.us-east-1.amazonaws.com/
```
### 03-04 - Destroy and Clean-Up
```t
# Terraform Destroy
terraform destroy -auto-approve

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```


### 03-05 - Conclusión
- Utilizando la configución de Terraform pasamos a tener hosteado nuestro contenido estatico en AWS S3 en segundos.
- Ahora convertiremos estos archivos TErraform en un modulo re-usable


## 04 - Build a Terraform Module para Host un Static Website en AWS S3
- **Directorio:** v3-build-a-module-to-host-static-website-on-aws-s3
- Vamos a construir un modulo de Terraform para hostear una página con contenido estatico en AWS S3.

### 04-01 - Crear Module Folder Structure
- Vamos a crerar el directorio `modules` y adentro un modulo (carpeta) llamado `aws-s3-static-website-bucket`
- Vamos a copiar los archivos requeridos de secciones anteriores para esta carpeta.
- Terraform Working Directory: v3-build-a-module-to-host-static-website-on-aws-s3
    - modules
        - aws-s3-static-website-bucket
            - main.tf
            - variables.tf
            - outputs.tf
            - README.md
            - LICENSE
- Dentro de `modules/aws-s3-static-website-bucket`, copiar los archivos listados a continuación desde la carpeta `v2-host-static-website-on-s3-using-terraform-manifests`
    - main.tf
    - variables.tf
    - outputs.tf


### 04-02 - Call Module from Terraform Work Directory (Root Module)
- Crear los archivos de configuración Terraform en el Root Module llamando nuestro nuevo modulo
- c1-versions.tf
- c2-variables.tf
- c3-s3bucket.tf
- c4-outputs.tf
```t
module "website_s3_bucket" {
  source = "./modules/aws-s3-static-website-bucket"
  bucket_name = var.my_s3_bucket
  tags = var.my_s3_tags
}
```
### 04-03 - Ejecutar Terraform Commands
```
# Terraform Initialize
terraform init
Observación: 
1. Verificar ".terraform", deberias de encontrar directorios "modules" en adición a directorios "providers"
2. Verificar dentro ".terraform/modules"

# Terraform Validate
terraform validate

# Terraform Format
terraform fmt

# Terraform Plan
terraform plan

# Terraform Apply
terraform apply -auto-approve

# Verify 
1. Bucket tiene static website hosting habilitado
2. Bucket tiene public read access habilitado using policy
3. Bucket tiene "Block all public access" habilitado
```

### 04-04 - Upload index.html and test
```
# Endpoint Format
http://example-bucket.s3-website.Region.amazonaws.com/

# Replace Values (Bucket Name, Region)
http://mybucket-xxxx.s3-website.us-east-1.amazonaws.com/
```

### 04-05 - Destroy and Clean-Up
```t
# Terraform Destroy
terraform destroy -auto-approve

# Delete Terraform files 
rm -rf .terraform*
rm -rf terraform.tfstate*
```

### 04-06 - Entender terraform get command

- Hemos utilizado `terraform init` para descargar proveedores desde el terraform registry y al mismo tiempo para descargar los `módulos` presentes en la carpeta de módulos locales en el directorio de trabajo de terraform.
- Suponiendo que ya nos hemos inicializado usando `terraform init` y luego hemos creado las configuraciones del `módulo`, podemos `terraform get` para descargar lo mismo.
- Siempre que agregue un nuevo módulo a una configuración, Terraform debe instalar el módulo antes de que pueda usarse.
- Los comandos `terraform get` y` terraform init` instalarán y actualizarán los módulos.
- El comando `terraform init` también inicializará backends e instalará complementos.
```
# Delete modules in .terraform folder
ls -lrt .terraform/modules
rm -rf .terraform/modules
ls -lrt .terraform/modules

# Terraform Get
terraform get
ls -lrt .terraform/modules
```
### 04-07 - Mayor diferencia entre Local and Remote Module
- Al instalar un módulo remoto, Terraform lo descargará en el directorio .terraform en el directorio raíz de su configuración.
- Al instalar un módulo local, Terraform se referirá directamente al directorio de origen.
- Debido a esto, Terraform notará automáticamente los cambios en los módulos locales sin tener que volver a ejecutar terraform init o terraform get.

## 05 - Conclusión
- Creamos un Terraform module
- Usamos local Terraform modules en nuestra configuración
- Configuramos modules con variables
- Usamos module outputs



















