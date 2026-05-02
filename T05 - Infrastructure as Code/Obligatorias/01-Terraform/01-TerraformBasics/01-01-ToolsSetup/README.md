# Terraform CLI — Instalación

> **Tiempo estimado:** 20 minutos

En los laboratorios de Cloud ya instalamos y configuramos la AWS CLI. El único requisito pendiente para arrancar con Terraform es instalar el binario de Terraform CLI y el plugin de VS Code. Este laboratorio cubre eso para los tres sistemas operativos, con foco en Linux donde se usará principalmente durante el curso.

### Puntos a tener en consideración
- Verificar que las credenciales de AWS ya estén configuradas con `aws sts get-caller-identity` antes de continuar.
- Las credenciales de AWS **nunca** deben subirse a un repositorio Git.
- Verificar la instalación de Terraform con `terraform version` antes de pasar al siguiente lab.

---

## 01 - Introducción

- Instalar Terraform CLI
- Instalar la extensión HashiCorp Terraform para VS Code

---

## 02 - Linux: Instalar Terraform

Este es el método principal para el curso. Terraform publica repositorios APT y YUM oficiales vía HashiCorp.

**Ubuntu / Debian:**

```bash
# Agregar el repositorio oficial de HashiCorp
sudo apt-get update && sudo apt-get install -y gnupg software-properties-common

wget -O- https://apt.releases.hashicorp.com/gpg | \
  sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg

echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] \
  https://apt.releases.hashicorp.com $(lsb_release -cs) main" | \
  sudo tee /etc/apt/sources.list.d/hashicorp.list

sudo apt-get update && sudo apt-get install -y terraform

# Verificar
terraform version
```

**RHEL / Amazon Linux:**

```bash
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://rpm.releases.hashicorp.com/RHEL/hashicorp.repo
sudo yum install -y terraform

# Verificar
terraform version
```

> Para actualizar en el futuro: `sudo apt-get update && sudo apt-get install --only-upgrade terraform`

---

## 03 - Linux: Autocompletado en la terminal

```bash
# Habilitar autocompletado (bash)
terraform -install-autocomplete

# Recargar la sesión
source ~/.bashrc
```

Con esto, `terraform <Tab>` completa comandos y subcomandos en la terminal.

---

## 04 - IDE — VS Code + Extensión HashiCorp Terraform

- Descargar [Visual Studio Code](https://code.visualstudio.com/download)
- Instalar la extensión [HashiCorp Terraform](https://marketplace.visualstudio.com/items?itemName=HashiCorp.terraform)

La extensión provee: sintaxis highlighting, autocompletado, `terraform fmt` al guardar, y validación inline de errores.

---

## 05 - macOS: Instalar Terraform

El método recomendado en macOS es vía **Homebrew**:

```bash
brew tap hashicorp/tap
brew install hashicorp/tap/terraform

# Verificar versión
terraform version
```

> Para actualizar en el futuro: `brew upgrade hashicorp/tap/terraform`

---

## 06 - Windows: Instalar Terraform

- Descargar el binario desde [developer.hashicorp.com/terraform/downloads](https://developer.hashicorp.com/terraform/downloads)
- Descomprimir y mover `terraform.exe` a un directorio como `C:\terraform-bins\`
- Agregar ese directorio al `PATH` en las variables de entorno del sistema
- Verificar: abrir una nueva terminal y ejecutar `terraform version`

---

## 07 - Verificar credenciales de AWS

La AWS CLI ya está instalada y configurada de los laboratorios de Cloud. Verificar que sigue funcionando:

```bash
# Verificar identidad configurada
aws sts get-caller-identity

# Verificar que las credenciales están disponibles
cat $HOME/.aws/credentials
```

Si las credenciales no están configuradas, ejecutar `aws configure` con las Access Keys del usuario IAM.

---

## Referencias

- [Terraform Downloads](https://developer.hashicorp.com/terraform/downloads)
- [Install Terraform CLI](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)

---

Continuar con [01-02 — Terraform Command Basics](../01-02-TerraformCommandBasics/README.md)
