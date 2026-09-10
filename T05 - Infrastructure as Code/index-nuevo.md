# T05 — Infrastructure as Code

> **Borrador de la nueva estructura.** Reemplaza a `index.md` cuando estén escritos todos los laboratorios. Los que dicen *pendiente* todavía no existen.

Infrastructure as Code (IaC) es la práctica de describir la infraestructura en archivos versionados en lugar de crearla a mano. El estado deseado vive en Git, se revisa en un Pull Request y se aplica de forma repetible.

En T04 armaste un servidor web, un balanceador y una VPC desde la consola. En este módulo vas a construir **eso mismo en código con Terraform**, en un único proyecto que crece laboratorio a laboratorio: empieza como un archivo con una instancia y termina como módulos reutilizables, con tres ambientes y un pipeline que aplica los cambios.

## Cómo se trabaja

- Hay **un solo proyecto**, la carpeta `tf-webapp`. Cada laboratorio parte de donde terminó el anterior.
- Cada laboratorio trae una carpeta `solucion/` con el código final. Si te atrasaste, copiala y seguí desde ahí.
- Al final de cada laboratorio se hace `terraform destroy`. Se destruye la infraestructura, **no el código**.
- Las secciones **🔎 Investigá** no tienen la respuesta en el texto: se resuelven con la documentación oficial.

## Laboratorios obligatorios

| # | Laboratorio | Qué le pasa al proyecto | Tiempo |
|---|-------------|-------------------------|--------|
| 00 | [Instalación](Obligatorias/01-Terraform/01-TerraformBasics/01-01-ToolsSetup/README.md) | Terraform CLI, editor y credenciales | 15 min |
| 01 | [Primer apply](Obligatorias/02-Proyecto-WebApp/01-Primer-Apply/README.md) | Un servidor web en un archivo. Plan, apply, state, tipos de cambio y drift | 35 min |
| 02 | [Sintaxis](Obligatorias/01-Terraform/01-TerraformBasics/01-03-TerraformLanguageSyntax/README.md) | Pausa sin AWS: romper y arreglar código HCL | 20 min |
| 03 | [Parametrizar](Obligatorias/02-Proyecto-WebApp/03-Parametrizar/README.md) | Variables, outputs, locals y data sources. El mismo código sirve para `dev` y `prod` | 45 min |
| 04 | [Escalar](Obligatorias/02-Proyecto-WebApp/04-Escalar/README.md) | VPC propia, subnets en dos zonas, varias instancias y balanceador: `count`, `for_each`, `depends_on`, `lifecycle` | 60 min |
| 05 | State remoto *(pendiente)* | Backend en S3 con locking, `state mv/rm`, `import` y recuperación ante drift | — |
| 06 | Módulos y ambientes *(pendiente)* | Módulos `network` y `webapp`; `dev`, `test` y `prod` con `.tfvars` y state separado | — |
| 07 | Pipeline *(pendiente)* | GitHub Actions: `fmt`, `validate` y `plan` en el Pull Request, `apply` al hacer merge | — |

Al terminar el laboratorio 07 el proyecto cubre lo que pide el obligatorio para IaC: backend remoto, estructura modular, `.tfvars` por ambiente y validación en el pipeline.

## Recomendadas

Temas que dejan de ser obligatorios. Los laboratorios existentes pasan a esta sección:

| Tema | Cuándo te sirve |
|------|-----------------|
| Múltiples providers y alias | Desplegar en más de una región o cuenta |
| Dependency lock file | Entender por qué `.terraform.lock.hcl` va a Git |
| Workspaces | Alternativa a un directorio por ambiente; conviene conocer sus límites |
| Provisioners y `terraform_data` | Último recurso cuando no alcanza con `user_data` |
| HCP Terraform | State y ejecuciones remotas como servicio |

Ver también [otras herramientas de IaC](Recomendadas/index.md): Terragrunt, OpenTofu, Pulumi, Ansible y Checkov.

## Prerequisitos

- Módulo T04 terminado (EC2, VPC, security groups)
- AWS Academy Learner Lab iniciado, con las credenciales copiadas en `~/.aws/credentials`
- AWS CLI y Git instalados
