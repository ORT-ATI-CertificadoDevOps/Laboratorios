# HCP Terraform & GitHub Integration

> **Nota:** Terraform Cloud fue renombrado a **HCP Terraform** en 2024. Las URLs y la interfaz son las mismas (`app.terraform.io`), solo cambió el nombre del producto.

> **Tiempo estimado:** 40 minutos

HCP Terraform ejecuta los planes en infraestructura remota de HashiCorp, con historial de runs, variables cifradas y state locking sin necesidad de configurar S3 + DynamoDB manualmente. La integración con GitHub convierte cada `git push` en un trigger de plan automático, implementando el flujo GitOps para infraestructura: el repositorio es la fuente de verdad y los cambios se aplican via pull request.

### Puntos a tener en consideración
- La cuenta gratuita de HCP Terraform incluye hasta 500 recursos gestionados — más que suficiente para laboratorios.
- Las variables de entorno (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`) se configuran como **sensitive** en HCP Terraform para que nunca aparezcan en los logs.
- Los planes se ejecutan en la nube de HashiCorp — las credenciales AWS configuradas deben tener permisos suficientes.

---

## 01 - Introducción
- Crear GitHub Repository en github.com
- Clonar GitHub Repository al ambiente local
- Copiar y hacer Check-In de las configuraciones Terraform en el repositorio
- Crear cuenta en HCP Terraform (ex Terraform Cloud)
- Crear Organization y Workspace con VCS workflow
- Aprender sobre Queue Plans, Runs, States, Variables y Settings

## 02 - Crear new github Repository
- **URL:** github.com
- Click on **Create a new repository**
- **Repository Name:** terraform-cloud-demo1
- **Description:** Terraform Cloud Demo 
- **Repo Type:** Public / Private
- **Initialize this repository with:**
- **CHECK** - Add a README file
- **CHECK** - Add .gitignore 
- **Select .gitignore Template:** Terraform
- **CHECK** - Choose a license
- **Select License:** Apache 2.0 License
- Click on **Create repository**

## 03 - Revisar .gitignore created for Terraform
- Revisar .gitignore created for Terraform projects

## 04 - Clone Github Repository to Local Desktop
```t
# Clone Github Repo
git clone https://github.com/<YOUR_GITHUB_ID>/<YOUR_REPO>.git
```

## 05 - Copiar archivos desde terraform-manifests a local repo & Check-In Code
- Lista de archivos a ser copiados
  - apache-install.sh
  - c1-versions.tf
  - c2-variables.tf
  - c3-security-groups.tf
  - c4-ec2-instance.tf
  - c5-outputs.tf
  - c6-ami-datasource.tf
- Source Location: terraform-manifest en 10-TerraformCloudandEnterpriseCapabilities
- Destination Location: Directorio clonado en nuevo repositorio `terraform-cloud-demo1`
- Verificar localmente antes de realizar el push al git
```t
# Terraform Init
terraform init

# Terraform Validate
terraform validate

# Terraform Plan
terraform plan
```
- Check-In code to Remote Repository
```t
# GIT Status
git status

# Git Local Commit
git add .
git commit -am "TF Files First Commit"

# Push to Remote Repository
git push

# Verify the same on Remote Repository
https://github.com/<YOUR_GITHUB_ID>/<YOUR_REPO>.git
```

## 06 - Sign-Up en HCP Terraform - Free Account & Login
- **SignUp URL:** https://app.terraform.io/signup/account
- **Username:**
- **Email:**
- **Password:** 
- **Login URL:** https://app.terraform.io

## 07 - Create Organization 
- **Organization Name:** xxxx-demo1
- **Email Address:** xxxx@xxxx.com
- Click on **Create Organization**

## 08 - Create New Workspace
- Get in to newly created Organization
- Click on **New Workspace**
- **Choose your workflow:** V
  - Version Control Workflow
- **Connect to VCS**
  - **Connect to a version control provider:** github.com
  - NEW WINDOW: **Authorize Terraform Cloud:** Click on **Authorize Terraform Cloud Button**
  - NEW WINDOW: **Install Terraform Cloud**
  - **Select radio button:** Only select repositories
  - **Selected 1 Repository:** <YOUR_GITHUB_ID>/<YOUR_REPO>
  - Click on **Install**
- **Choose a Repository**
  - <YOUR_GITHUB_ID>/<YOUR_REPO>
- **Configure Settings**
  - **Workspace Name:** terraform-cloud-demo1 (Whatever populated automically leave to defaults) 
  - **Advanced Settings:** leave to defaults 
- Click on **Create Workspace**  
- You should see this message `Configuration uploaded successfully`


## 09 - Configure Variables
- **Variable:** aws_region
  - key: aws_region
  - value: us-east-1
- **Variable:** instance_type
  - key: instance_type
  - value: t3.micro

## 10 - Configre Environment Variables
- [Setup AWS Access Keys for Terraform](https://registry.terraform.io/providers/hashicorp/aws/latest/docs#environment-variables)
- Configure AWS Access Key ID and Secret Access Key  
- **Environment Variable:** AWS_ACCESS_KEY_ID
  - Key: AWS_ACCESS_KEY_ID
  - Value: XXXXXXXXXXXXXXXXXXXXXX
- **Environment Variable:** AWS_SECRET_ACCESS_KEY
  - Key: AWS_SECRET_ACCESS_KEY
  - Value: YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY

## 11 - Click on Queue Plan
- Go to Workspace -> Runs -> Queue Plan
- Review the plan generated in **Full Screen**
- **Add Comment:** First Run
- Click on **Confirm & Apply**
- **Add Comment:** First Run Approved
- Click on **Confirm Plan**
- Review Apply log output in **Full Screen**
- **Add Comment:** Successfully Provisioned, Verified in AWS

## 12 - Revisar Terraform State
- Go to Workspace -> States
- Review the state file

## 13 - Change Number of Instance
- Go to Local Desktop -> Local Repo -> c4-ec2-instance.tf -> Change count from 1 to 2
```t
# Change c4-ec2-instance.tf
count = 2

# GIT Status
git status

# Git Local Commit
git add .
git commit -am "Changed EC2 Instances from 1 to 2"

# Push to Remote Repository
git push

# Verify Terraform Cloud
Go to Workspace -> Runs 
Observation: 
1) New plan should be queued ->  Click on Current Plan and review logs in Full Screen
2) Click on **Confirm and Apply**
3) Add Comment: Approved for new EC2 Instance and Click on **Confirm Plan**
4) Verify Apply Logs in Full Screen
5) Review the update state in  Workspace -> States
6) Verify if new EC2 Instance got created
```

## 14 - Review Workpace Settings
- Goto -> Workspace -> Settings
  - General Settings
  - Locking
  - Notifications
  - Run Triggers
  - SSH Key
  - Version Control

## 15 - Destruction and Deletion
- Goto -> Workspace -> Settings -> Destruction and Deletion
- click on **Queue Destroy Plan** to delete the resources on cloud 
- Goto -> Workspac -> Runs -> Click on **Confirm & Apply**
- **Add Comment:** Approved for Deletion

