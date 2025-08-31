# Instalación del Ambiente

En esta guía vamos a configurar nuestro ambiente de trabajo para poder realizar los laboratorios de DevOps. Tenemos dos opciones principales:

- **VirtualBox** (Windows y macOS) - _**Recomendada**_
- **WSL** (solo Windows)

## ¿Cuándo seguir esta guía?

Esta guía es **obligatoria** si:
- Trabajas en Windows
- Trabajas en macOS y quieres mantener tu ambiente de laboratorio aislado del sistema principal

## Opción 1: VirtualBox (Recomendada)

### Prerrequisitos importantes

⚠️ **IMPORTANTE**: Debes activar la virtualización en el BIOS/UEFI de tu equipo antes de continuar. Sin esto, VirtualBox no funcionará correctamente.

### Paso 1: Descargar e instalar VirtualBox

1. Ve a la página oficial: [https://www.virtualbox.org/](https://www.virtualbox.org/)
2. Descarga la versión correspondiente a tu sistema operativo
3. Ejecuta el instalador y sigue las instrucciones por defecto

### Paso 2: Descargar la imagen de Linux

Para este curso utilizaremos **CentOS**:

1. Ve a: [https://centos.org/download/](https://centos.org/download/)
2. Descarga la imagen ISO (archivo con extensión .iso)
3. Guarda el archivo en una ubicación que recuerdes

### Paso 3: Crear la máquina virtual

#### 3.1 Iniciar la creación

1. Abre VirtualBox
2. Haz clic en **"Nueva"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion01.jpg" title="static">

#### 3.2 Configurar la VM

Completa los siguientes campos:
- **Nombre**: Escribe un nombre descriptivo (ej: "_CentOS-DevOps_").
- **Carpeta**: Deja la ubicación por defecto o elige donde guardar la VM.
- **Imagen ISO**: Selecciona el archivo .iso de CentOS que descargaste.

> 💡 **Nota**: Los demás campos se completarán automáticamente cuando selecciones la imagen ISO.

3. Haz clic en **"Siguiente"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion02.jpg" title="static">

#### 3.3 Configurar credenciales

Puedes modificar el usuario y contraseña si quieres, o mantener los valores por defecto. 

⚠️ **IMPORTANTE**: Anota (o recordá) estos datos, los necesitarás para acceder a la VM.

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion03.jpg" title="static">

#### 3.4 Asignar recursos del sistema

Configura los siguientes valores **mínimos**:
- **Memoria RAM**: 4096 MB (4 GB)
- **CPUs**: 2 procesadores

> 📋 **Recomendación**: Si tu equipo tiene más recursos disponibles, puedes asignar más RAM y CPUs para mejor rendimiento.

Haz clic en **"Siguiente"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion04.jpg" title="static">

#### 3.5 Configurar almacenamiento

- **Tamaño del disco**: Mínimo 40 GB
- Deja las demás opciones por defecto

Haz clic en **"Siguiente"** y luego en **"Finalizar"**

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion05.jpg" title="static">

### Paso 4: Instalar el sistema operativo

#### 4.1 Iniciar la instalación

1. Selecciona tu VM recién creada
2. Haz clic en **"Iniciar"**
3. Deberías ver el menú de instalación de CentOS
4. Selecciona la primera opción para instalar

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion06.jpg" title="static">

#### 4.2 Configuraciones importantes durante la instalación

Durante el proceso de instalación, asegúrate de:

1. **Habilitar el usuario root**
2. **Permitir el acceso SSH** (lo usaremos con Visual Studio Code)

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion07.jpg" title="static">

#### 4.3 Completar la instalación

Una vez configurados todos los valores, la instalación comenzará automáticamente.

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion08.jpg" title="static">

### Paso 5: Configurar SSH

Una vez completada la instalación, necesitamos configurar el acceso SSH.

#### 5.1 Instalar y configurar SSH en CentOS

1. Inicia sesión en la consola de VirtualBox con tu usuario
2. Ejecuta los siguientes comandos:

```bash
sudo yum install openssh-server
sudo systemctl start sshd.service
sudo systemctl enable sshd.service
```

3. Verifica que el servicio esté funcionando:

```bash
sudo systemctl status sshd
```

Deberías ver que el servicio está "active (running)".

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion09.jpg" title="static">

#### 5.2 Configurar la red

1. En VirtualBox, ve a **Configuración** de tu VM
2. En la sección **Red**, verifica que esté configurada como **"Adaptador puente"**
3. Esto permitirá que la VM obtenga una IP de tu red local

#### 5.3 Obtener la dirección IP

En la consola de la VM, ejecuta:

```bash
ip addr show
```

o el comando tradicional:

```bash
ifconfig
```

Anota la dirección IP que aparece (ej: 192.168.1.100).

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion10.jpg" title="static">

### Paso 6: Conectar desde Visual Studio Code

#### 6.1 Instalar la extensión SSH

1. Abre Visual Studio Code
2. Ve a la sección de extensiones
3. Busca e instala **"Remote - SSH"**

#### 6.2 Configurar la conexión

1. Presiona `Ctrl+Shift+P` (o `Cmd+Shift+P` en Mac)
2. Busca "Remote-SSH: Connect to Host"
3. Selecciona "Add New SSH Host"
4. Ingresa: `root@[IP_DE_TU_VM]` (ej: `root@192.168.1.100`)
5. Sigue las instrucciones para guardar la configuración

<img src="/Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion11.jpg" title="static">

¡Listo! Ahora puedes conectarte a tu VM desde Visual Studio Code usando SSH.

---

## Opción 2: WSL (Solo Windows)

### Paso 1: Instalar WSL

1. Abre **PowerShell** como administrador
2. Ejecuta el siguiente comando:

```powershell
wsl --install
```

3. **Reinicia tu equipo** cuando se complete la instalación

### Paso 2: Configurar Ubuntu

1. Después del reinicio, busca **"Ubuntu"** en el menú de inicio
2. Ábrelo y completa la configuración inicial:
   - Crea un nombre de usuario
   - Establece una contraseña
   - Confirma la contraseña

### Paso 3: Instalar SSH en Ubuntu

Una vez dentro de Ubuntu, ejecuta:

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

Anota la dirección IP y úsala para conectarte desde Visual Studio Code siguiendo los mismos pasos del **Paso 6** de la opción VirtualBox.

---

## Verificación final

Para confirmar que todo funciona correctamente:

1. Deberías poder conectarte a tu VM/WSL desde Visual Studio Code
2. Puedes abrir una terminal en VS Code y ejecutar comandos Linux
3. Tienes acceso completo al sistema de archivos

¡Tu ambiente está listo para los laboratorios del curso! 🚀