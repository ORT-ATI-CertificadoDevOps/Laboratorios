# T05 — Infrastructure as Code

Infrastructure as Code (IaC) es la práctica de gestionar y provisionar infraestructura mediante archivos de configuración en lugar de procesos manuales. Con IaC, el estado deseado de la infraestructura está versionado, revisable y reproducible — igual que el código de aplicación.

**Terraform** es la herramienta de IaC open-source más adoptada en la industria. Permite describir infraestructura de cualquier proveedor cloud (AWS, GCP, Azure) en un lenguaje declarativo (HCL) y gestionar su ciclo de vida completo: crear, actualizar y destruir recursos.

## Laboratorios

### 01 — Terraform Basics

| Lab | Contenido |
|-----|-----------|
| [01-01 Tools Setup](Obligatorias/01-Terraform/01-TerraformBasics/01-01-ToolsSetup/README.md) | Instalación de Terraform CLI, AWS CLI y VS Code |
| [01-02 Command Basics](Obligatorias/01-Terraform/01-TerraformBasics/01-02-TerraformCommandBasics/README.md) | Comandos esenciales: init, validate, plan, apply, destroy |
| [01-03 Language Syntax](Obligatorias/01-Terraform/01-TerraformBasics/01-03-TerraformLanguageSyntax/README.md) | Bloques, argumentos, meta-argumentos y comentarios |

### 02 — Fundamental Blocks

| Lab | Contenido |
|-----|-----------|
| [02-01 Terraform Block](Obligatorias/01-Terraform/02-TerraformFundamentalBlocks/02-01-Block/README.md) | `required_version`, restricciones de versión |
| [02-02 Provider Block](Obligatorias/01-Terraform/02-TerraformFundamentalBlocks/02-02-ProviderBlock/README.md) | Configuración del provider de AWS, autenticación |
| [02-03 Multiple Providers](Obligatorias/01-Terraform/02-TerraformFundamentalBlocks/02-03-MultipleProviderConfigurations/README.md) | Alias de providers, multi-región |
| [02-04 Lock File](Obligatorias/01-Terraform/02-TerraformFundamentalBlocks/02-04-ProvidersDependencyLockFile/README.md) | `.terraform.lock.hcl` y consistencia de versiones en equipo |

### 03 — Resources

| Lab | Contenido |
|-----|-----------|
| [03-01 Resource Syntax](Obligatorias/01-Terraform/03-TerraformResources/03-01-ResourceSyntaxAndBehavior/README.md) | Sintaxis, comportamiento y ciclo de vida de recursos |
| [03-02 depends_on](Obligatorias/01-Terraform/03-TerraformResources/03-02-MetaArgumentDependsOn/README.md) | Dependencias explícitas entre recursos |
| [03-03 count](Obligatorias/01-Terraform/03-TerraformResources/03-03-MetaArgumentCount/README.md) | Crear múltiples recursos con un solo bloque |
| [03-04 for_each](Obligatorias/01-Terraform/03-TerraformResources/03-04-MetaArgumentForEach/README.md) | Iteración sobre maps y sets |
| [03-05 lifecycle](Obligatorias/01-Terraform/03-TerraformResources/03-05-MetaArgumentLifecycle/README.md) | `create_before_destroy`, `prevent_destroy`, `ignore_changes` |

### 04 — Variables y Valores

| Lab | Contenido |
|-----|-----------|
| [04-01 Input Variables](Obligatorias/01-Terraform/04-TerraformVariables/04-01-TerraformInputVariables/README.md) | Tipos, defaults, `.tfvars`, validaciones, variables sensibles |
| [04-02 Output Values](Obligatorias/01-Terraform/04-TerraformVariables/04-02-TerraformOutputValues/README.md) | Exportar atributos de recursos |
| [04-03 Local Values](Obligatorias/01-Terraform/04-TerraformVariables/04-03-TerraformLocalValues/README.md) | DRY con `locals` |

### 05 — Data Sources

| Lab | Contenido |
|-----|-----------|
| [05 Data Sources](Obligatorias/01-Terraform/05-TerraformDatasources/README.md) | Obtener el AMI ID de Amazon Linux 2023 dinámicamente |

### 06 — State

| Lab | Contenido |
|-----|-----------|
| [06-01 Remote State](Obligatorias/01-Terraform/06-TerraformState/06-01-TerraformRemoteStateStorageandLocking/README.md) | Backend S3 + locking con DynamoDB |
| [06-02 State Commands](Obligatorias/01-Terraform/06-TerraformState/06-02-TerraformStateCommands/README.md) | show, refresh, state mv/rm, force-unlock, -replace |

### 07 — Workspaces

| Lab | Contenido |
|-----|-----------|
| [07 Workspaces](Obligatorias/01-Terraform/07-TerraformWorkspaces/README.md) | Múltiples entornos (dev, qa, prod) con un solo código base |

### 08 — Provisioners

| Lab | Contenido |
|-----|-----------|
| [08-01 File Provisioner](Obligatorias/01-Terraform/08-TerraformProvisioners/08-01-File-Provisioner/README.md) | Copiar archivos a instancias remotas |
| [08-02 remote-exec](Obligatorias/01-Terraform/08-TerraformProvisioners/08-02-remote-exec-provisioner/README.md) | Ejecutar comandos en instancias remotas vía SSH |
| [08-03 local-exec](Obligatorias/01-Terraform/08-TerraformProvisioners/08-03-local-exec-provisioner/README.md) | Ejecutar comandos locales al momento de aplicar |
| [08-04 Null Resource](Obligatorias/01-Terraform/08-TerraformProvisioners/08-04-Null-Resource/README.md) | `terraform_data` / `null_resource` para lógica auxiliar |

### 09 — Modules

| Lab | Contenido |
|-----|-----------|
| [09-01 Module Basics](Obligatorias/01-Terraform/09-TerraformModules/09-01-Terraform-Modules-Basics/README.md) | Usar módulos publicados en el Terraform Registry |
| [09-02 Build a Module](Obligatorias/01-Terraform/09-TerraformModules/09-02-Terraform-Build-a-Module/README.md) | Crear un módulo reutilizable desde cero |

### 10 — HCP Terraform

| Lab | Contenido |
|-----|-----------|
| [10-01 HCP Terraform + GitHub](Obligatorias/01-Terraform/10-TerraformCloudandEnterpriseCapabilities/10-01-Terraform-Cloud-Github-Integration/README.md) | VCS workflow, remote runs y state remoto en HCP Terraform |

## Prerequisitos

- Cuenta de AWS con permisos para crear recursos (EC2, VPC, S3, DynamoDB, IAM)
- Git instalado y configurado
- Terminal (bash/zsh en macOS/Linux, PowerShell en Windows)
