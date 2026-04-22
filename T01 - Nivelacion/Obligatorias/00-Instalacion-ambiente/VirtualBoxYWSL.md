## Instalación del Ambiente

> **Tiempo estimado:** 45 minutos

En esta guía vamos a configurar el ambiente de trabajo para los laboratorios de DevOps. Hay dos opciones principales:

- **VirtualBox** (Windows y macOS) — *Recomendada*
- **WSL** (solo Windows)

> **Nota sobre la distribución Linux:** Esta guía utiliza **CentOS** como distribución de referencia. CentOS llegó a su fin de vida (EOL) en 2021. Si tienen dificultades para descargarlo, se recomienda **AlmaLinux** o **Rocky Linux** como alternativa compatible (mismos comandos, misma estructura, soporte activo).

### ¿Cuándo seguir esta guía?

Esta guía es **obligatoria** si:
- Utilizamos Windows
- Utilizamos macOS con Apple Silicon (M1/M2/M3) → seguir la guía de **Colima** (ver Opción 3)
- Utilizamos macOS con Intel → el ambiente puede usarse directamente, aunque se recomienda VirtualBox para mantenerlo aislado

---

### Opción 1: VirtualBox

#### Prerrequisitos

> **Importante:** Activar la virtualización en el BIOS/UEFI antes de continuar. Sin esto, VirtualBox no funcionará correctamente.

> **Usuarios de Mac con Apple Silicon (M1/M2/M3):** VirtualBox no tiene soporte estable para arquitectura ARM. Usar **Colima** (ver Opción 3).

#### Paso 1: Descargar e instalar VirtualBox

1. Ir a la página oficial: [https://www.virtualbox.org/](https://www.virtualbox.org/)
2. Descargar la versión correspondiente al sistema operativo
3. Ejecutar el instalador y seguir las instrucciones por defecto

#### Paso 2: Descargar la imagen de Linux

Para este curso utilizaremos **CentOS**:

1. Ir a: [https://centos.org/download/](https://centos.org/download/)
2. Descargar la imagen ISO (archivo `.iso`)
3. Guardar el archivo en una ubicación fácil de recordar

#### Paso 3: Crear la máquina virtual

**3.1 Iniciar la creación**

1. Abrir VirtualBox
2. Hacer clic en **"Nueva"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion01.jpg" title="static">

**3.2 Configurar la VM**

Completar los siguientes campos:
- **Nombre:** un nombre descriptivo (ej: `CentOS-DevOps`)
- **Carpeta:** ubicación por defecto o la deseada
- **Imagen ISO:** seleccionar el archivo `.iso` de CentOS descargado

> Los demás campos se completarán automáticamente al seleccionar la imagen ISO.

Hacer clic en **"Siguiente"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion02.jpg" title="static">

**3.3 Configurar credenciales**

Modificar el usuario y contraseña o mantener los valores por defecto.

> **Importante:** Anotar estas credenciales — serán necesarias para acceder a la VM.

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion03.jpg" title="static">

**3.4 Asignar recursos del sistema**

Valores mínimos recomendados:
- **Memoria RAM:** 4096 MB (4 GB)
- **CPUs:** 2 procesadores

Hacer clic en **"Siguiente"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion04.jpg" title="static">

**3.5 Configurar almacenamiento**

- **Tamaño del disco:** mínimo 40 GB
- Dejar las demás opciones por defecto

Hacer clic en **"Siguiente"** y luego en **"Finalizar"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion05.jpg" title="static">

#### Paso 4: Instalar el sistema operativo

**4.1 Iniciar la instalación**

1. Seleccionar la VM recién creada
2. Hacer clic en **"Iniciar"**
3. Seleccionar la primera opción del menú de instalación de CentOS

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion06.jpg" title="static">

**4.2 Configuraciones importantes durante la instalación**

1. Habilitar el usuario root
2. Permitir el acceso SSH (necesario para conectarse desde Visual Studio Code)

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion07.jpg" title="static">

**4.3 Completar la instalación**

Una vez configurados todos los valores, la instalación comenzará automáticamente.

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion08.jpg" title="static">

#### Paso 5: Configurar SSH

**5.1 Instalar y habilitar SSH en CentOS**

Iniciar sesión en la consola de VirtualBox y ejecutar:

```bash
sudo yum install openssh-server
sudo systemctl start sshd.service
sudo systemctl enable sshd.service
```

Verificar que el servicio esté activo:

```bash
sudo systemctl status sshd
# Debe aparecer como "active (running)"
```

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion09.jpg" title="static">

**5.2 Configurar la red**

En VirtualBox → **Configuración** de la VM → sección **Red** → verificar que esté en **"Adaptador puente"**. Esto permite que la VM obtenga una IP de la red local.

**5.3 Obtener la dirección IP**

```bash
ip addr show
```

Anotar la dirección IP que aparece (ej: `192.168.1.100`).

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion10.jpg" title="static">

#### Paso 6: Conectar desde Visual Studio Code

**6.1 Instalar la extensión SSH**

1. Abrir Visual Studio Code
2. Buscar e instalar la extensión **"Remote - SSH"**

**6.2 Configurar la conexión**

1. Presionar `Ctrl+Shift+P` (o `Cmd+Shift+P` en Mac)
2. Buscar "Remote-SSH: Connect to Host"
3. Seleccionar "Add New SSH Host"
4. Ingresar: `root@<IP_DE_TU_VM>` (ej: `root@192.168.1.100`)
5. Seguir las instrucciones para guardar la configuración

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion11.jpg" title="static">

---

### Opción 2: WSL (Solo Windows)

#### Paso 1: Instalar WSL

1. Abrir **PowerShell** como administrador
2. Ejecutar:

```powershell
wsl --install
```

> Este comando instala **Ubuntu** por defecto. En Windows 11 funciona directamente; en Windows 10 puede ser necesario habilitar WSL manualmente desde "Activar o desactivar características de Windows" antes de ejecutarlo.

3. **Reiniciar el equipo** al completar la instalación

#### Paso 2: Configurar Ubuntu

1. Buscar **"Ubuntu"** en el menú de inicio
2. Completar la configuración inicial: crear usuario y contraseña

#### Paso 3: Instalar SSH en Ubuntu

```bash
sudo apt update
sudo apt install openssh-server
sudo systemctl start ssh
sudo systemctl enable ssh
```

#### Paso 4: Verificar la instalación

```bash
sudo systemctl status ssh
```

#### Paso 5: Obtener la IP de WSL

```bash
ip addr show eth0
```

Usar esa IP para conectarse desde Visual Studio Code.

> **Alternativa más simple:** VS Code incluye la extensión **"WSL"** (buscarla en el Marketplace) que permite conectarse a WSL directamente sin necesitar SSH ni dirección IP. Es la forma recomendada para usuarios de WSL.

---

### Opción 3: Colima (Mac con Apple Silicon — M1/M2/M3)

[Colima](https://github.com/abiosoft/colima) permite levantar una VM Linux emulando arquitectura x86 en Macs con Apple Silicon.

#### Paso 1: Instalar dependencias

Asegurarse de tener [Homebrew](https://brew.sh/) instalado y ejecutar:

```bash
brew install colima
brew install docker
```

#### Paso 2: Iniciar Colima con emulación x86

```bash
colima start --arch x86_64 --vm-type vz --rosetta
```

> El primer inicio puede tardar varios minutos.

#### Paso 3: Verificar que funciona

```bash
colima ssh

# Verificar la arquitectura dentro de la VM
uname -m
# Esperado: x86_64
```

#### Paso 4: Conectar desde Visual Studio Code

1. Instalar la extensión **"Remote - SSH"** en VS Code
2. Obtener la IP de la VM:

```bash
colima ssh -- ip addr show col0
```

3. Conectarse con `root@<IP>` siguiendo el Paso 6 de la Opción VirtualBox

---

### Verificación

Para confirmar que el ambiente quedó configurado correctamente:

1. Conectarse a la VM/WSL desde Visual Studio Code
2. Abrir una terminal en VS Code y ejecutar:

```bash
uname -a
```

Debe mostrar información del sistema Linux (kernel, arquitectura, etc.).
