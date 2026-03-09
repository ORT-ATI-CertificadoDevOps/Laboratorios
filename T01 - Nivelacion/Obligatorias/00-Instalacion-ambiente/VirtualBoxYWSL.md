# Instalación del Ambiente

En esta guía vamos a configurar nuestro ambiente de trabajo para poder realizar los laboratorios de DevOps. Tenemos dos opciones principales:

- **VirtualBox** (Windows y macOS) - _**Recomendada**_
- **WSL** (solo Windows)

> ⚠️ **Nota sobre la distribución Linux**: Esta guía utiliza **CentOS** como distribución de referencia. CentOS llegó a su fin de vida (EOL) en 2021, por lo que si tienen dificultades para descargarla o utilizarla, se recomienda como alternativa **AlmaLinux** o **Rocky Linux**, que son compatibles con CentOS (mismos comandos, misma estructura) y tienen soporte activo.

## ¿Cuándo seguir esta guía?

Esta guía es **obligatoria** si:
- Utilizamos Windows
- Utilizamos macOS con Apple Silicon (M1/M2/M3) → seguir la guía de **Colima** (ver abajo)
- Utilizamos macOS con Intel → el ambiente puede usarse directamente sin virtualización, aunque se recomienda VirtualBox para mantenerlo aislado

## Opción 1: VirtualBox (Recomendada para Windows)

### Prerrequisitos importantes

⚠️ **IMPORTANTE**: Debemos activar la virtualización en el BIOS/UEFI de nuestro equipo antes de continuar. Sin esto, VirtualBox no funcionará correctamente.

> ⚠️ **Usuarios de Mac con Apple Silicon (M1/M2/M3)**: VirtualBox no tiene soporte estable para arquitectura ARM. Se recomienda utilizar **Colima** para emular arquitectura x86 (ver **Opción 3** al final de esta guía).

### Paso 1: Descargar e instalar VirtualBox

1. Vamos a la página oficial: [https://www.virtualbox.org/](https://www.virtualbox.org/)
2. Descargamos la versión correspondiente al sistema operativo
3. Ejecutamos el instalador y seguimos las instrucciones por defecto

### Paso 2: Descargar la imagen de Linux

Para este curso utilizaremos **CentOS**:

1. Vamos a: [https://centos.org/download/](https://centos.org/download/)
2. Descargamos la imagen ISO (archivo con extensión .iso)
3. Guardamos el archivo en una ubicación fácil de recordar

### Paso 3: Crear la máquina virtual

#### 3.1 Iniciar la creación

1. Abrimos VirtualBox
2. Hacemos clic en **"Nueva"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion01.jpg" title="static">

#### 3.2 Configurar la VM

Completamos los siguientes campos:
- **Nombre**: Escribimos un nombre descriptivo (ej: "_CentOS-DevOps_").
- **Carpeta**: Dejamos la ubicación por defecto o elegimos donde guardar la VM.
- **Imagen ISO**: Seleccionamos el archivo .iso de CentOS que descargaste.

> 💡 **Nota**: Los demás campos se completarán automáticamente cuando selecciones la imagen ISO.

3. Hacemos clic en **"Siguiente"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion02.jpg" title="static">

#### 3.3 Configurar credenciales

Podemos modificar el usuario y contraseña o mantener los valores por defecto. 

⚠️ **IMPORTANTE**: Anotar (o recordar) estos datos, serán necesarios para acceder a la VM.

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion03.jpg" title="static">

#### 3.4 Asignar recursos del sistema

Configurar los siguientes valores **mínimos**:
- **Memoria RAM**: 4096 MB (4 GB)
- **CPUs**: 2 procesadores

> 📋 **Recomendación**: Si tu equipo tiene más recursos disponibles, puedes asignar más RAM y CPUs para mejor rendimiento.

Hacer clic en **"Siguiente"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion04.jpg" title="static">

#### 3.5 Configurar almacenamiento

- **Tamaño del disco**: Mínimo 40 GB
- Dejar las demás opciones por defecto

Hacer clic en **"Siguiente"** y luego en **"Finalizar"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion05.jpg" title="static">

### Paso 4: Instalar el sistema operativo

#### 4.1 Iniciar la instalación

1. Seleccionar la VM recién creada
2. Hacer clic en **"Iniciar"**
3. El menú de instalación de CentOS debería aparecer
4. Seleccionar la primera opción para instalar

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion06.jpg" title="static">

#### 4.2 Configuraciones importantes durante la instalación

Durante el proceso de instalación, verificar lo siguiente:

1. **Habilitar el usuario root**
2. **Permitir el acceso SSH** (a utilizar con Visual Studio Code)

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion07.jpg" title="static">

#### 4.3 Completar la instalación

Una vez configurados todos los valores, la instalación comenzará automáticamente.

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion08.jpg" title="static">

### Paso 5: Configurar SSH

Una vez completada la instalación, es necesario configurar el acceso SSH.

#### 5.1 Instalar y configurar SSH en CentOS

1. Iniciar sesión en la consola de VirtualBox con tu usuario
2. Ejecutar los siguientes comandos:

```bash
sudo yum install openssh-server
sudo systemctl start sshd.service
sudo systemctl enable sshd.service
```

3. Verificar que el servicio esté funcionando:

```bash
sudo systemctl status sshd
```

Debería reflejar que el servicio está "active (running)".

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion09.jpg" title="static">

#### 5.2 Configurar la red

1. En VirtualBox, ir a **Configuración** de la VM
2. En la sección **Red**, verificar que esté configurada como **"Adaptador puente"**. Esto permitirá que la VM obtenga una IP de tu red local

#### 5.3 Obtener la dirección IP

En la consola de la VM, ejecutar:

```bash
ip addr show
```

o el comando tradicional:

```bash
ifconfig
```

Anotar la dirección IP que aparece (ej: 192.168.1.100).

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion10.jpg" title="static">

### Paso 6: Conectar desde Visual Studio Code

#### 6.1 Instalar la extensión SSH

1. Abrir Visual Studio Code
2. Ir a la sección de extensiones
3. Buscar e instalar **"Remote - SSH"**

#### 6.2 Configurar la conexión

1. Presionar `Ctrl+Shift+P` (o `Cmd+Shift+P` en Mac)
2. Buscar "Remote-SSH: Connect to Host"
3. Seleccionar "Add New SSH Host"
4. Ingresar: `root@[IP_DE_TU_VM]` (ej: `root@192.168.1.100`)
5. Seguir las instrucciones para guardar la configuración

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion11.jpg" title="static">

¡Listo! Ahora es posible realizar la conexión a la VM desde Visual Studio Code usando SSH.

---

## Opción 2: WSL (Solo Windows)

### Paso 1: Instalar WSL

1. Abrir **PowerShell** como administrador
2. Ejecutar el siguiente comando:

```powershell
wsl --install
```

> 💡 **Nota**: Este comando instala **Ubuntu** por defecto. En Windows 11 funciona directamente; en Windows 10 puede ser necesario habilitar WSL manualmente desde "Activar o desactivar características de Windows" antes de ejecutarlo.

3. **Reiniciar el equipo** cuando se complete la instalación

### Paso 2: Configurar Ubuntu

1. Después del reinicio, buscar **"Ubuntu"** en el menú de inicio
2. Abrir y completar la configuración inicial:
   - Crear un nombre de usuario
   - Establecer una contraseña
   - Confirmar la contraseña

### Paso 3: Instalar SSH en Ubuntu

Una vez dentro de Ubuntu, ejecutar:

```bash
sudo apt update
sudo apt install openssh-server
sudo systemctl start ssh
sudo systemctl enable ssh
```

### Paso 4: Verificar la instalación

Verifica que SSH esté funcionando:

```bash
sudo systemctl status ssh
```

### Paso 5: Obtener la IP de WSL

```bash
ip addr show eth0
```

Anotar la dirección IP y usarla para conectarse desde Visual Studio Code.

> 💡 **Alternativa más simple para WSL**: VS Code incluye la extensión **"WSL"** (buscarla en el Marketplace) que permite conectarse directamente a WSL sin necesitar SSH ni dirección IP. Es la forma recomendada para usuarios de WSL.

---

## Verificación final

Para confirmar que todo funciona correctamente:

1. Debería ser posible la conexión a la VM/WSL desde Visual Studio Code
2. Abrir una terminal en VS Code y ejecutar el siguiente comando:

```bash
uname -a
```

Debería mostrar información del sistema Linux (kernel, arquitectura, etc.).

3. Tener acceso completo al sistema de archivos

¡El ambiente está listo para los laboratorios del curso!

---

## Opción 3: Colima (Mac con Apple Silicon — M1/M2/M3)

[Colima](https://github.com/abiosoft/colima) permite levantar una VM Linux emulando arquitectura x86 en Macs con Apple Silicon.

### Paso 1: Instalar dependencias

Asegurarse de tener [Homebrew](https://brew.sh/) instalado y luego ejecutar:

```bash
brew install colima
brew install docker
```

### Paso 2: Iniciar Colima con emulación x86

```bash
colima start --arch x86_64 --vm-type vz --rosetta
```

> 💡 Esto levanta una VM Linux con arquitectura x86_64 usando Rosetta para emulación. El primer inicio puede tardar varios minutos.

### Paso 3: Verificar que funciona

```bash
colima ssh
```

Esto abre una shell dentro de la VM. Para verificar la arquitectura:

```bash
uname -m
# Debería mostrar: x86_64
```

### Paso 4: Conectar desde Visual Studio Code

1. Instalar la extensión **"Remote - SSH"** en VS Code
2. Obtener la IP de la VM:

```bash
colima ssh -- ip addr show col0
```

1. Conectarse con `root@<IP>` siguiendo los mismos pasos del **Paso 6** de la Opción VirtualBox.
