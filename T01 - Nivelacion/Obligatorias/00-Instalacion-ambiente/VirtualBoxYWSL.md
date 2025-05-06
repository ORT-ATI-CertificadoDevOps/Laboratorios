
# Instalación ambiente

- Vamos a instalar o VirtualBox (Windows y MacOS) o WSL (Windows), la recomendación nuestra es que instalen VirtualBox y no WSL.
- SOLAMENTE vamos a hacer la guía si:
  - Se encuentran en Windows.
  - Se encuentran en macOS y quieren tener su máquina de trabajo aislada de la máquina que utilicen en el práctico.

## VirtualBox

### Puntos a tener en consideración
- Activar la virtualización en el BIOS de los equipos, sin esto, es muy probable de que no podamos instalar VirtualBox o WSL.

## 1 - Descargar e instalar VirtualBox
- [Link de descarga VBOX](https://www.virtualbox.org/)


## 2 - Descargar imagen de Linux
- Podemos tomar como ejemplo la descarga e instalación de centOS.
- [Link de centOS](https://centos.org/download/)

## 3 - Instalar la VirtualMachine (VM)
- Con VBOX instalado y la imagen del sistema operativo descargada, vamos a instalar la máquina virtual (VM).
- Seleccionamos en nueva
<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion01.jpg" title="static">
- Elegimos:
  - Nombre. 
  - Carpeta en donde alojar.
  - El archivo de la imagen que descargamos.

> Nota: Los demás campos deberían de autocompletarse por defecto.

- Seleccionamos en Next.

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion02.jpg" title="static">

- Si se quiere, podemos modificar username y password, en caso contrario no modificar y visualizar cuales son los valores para las credenciales.

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion03.jpg" title="static">

- Elegir al menos 4GB (4096MB) de memoria RAM y 2 CPU.
- Seleccionamos en next.

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion04.jpg" title="static">

- Elegir al menos 40GB de disco duro.
- Seleccionamos en next.
- Y damos en terminar.

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion05.jpg" title="static">

- Si sale todo ok, deberiamos de visualizar la ventana para empezar la instalación y procedemos a instalar el SO mediante la primer opción:

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion06.jpg" title="static">

- Importante durante la instalación, habilitar el usuario root y permitir el acceso mediante SSH (el cual usaremos con Visual Studio Code):

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion07.jpg" title="static">

- Luego de configurado todos los valores necesarios, comenzara la instalación:

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion08.jpg" title="static">

- Una vez haya terminado la instalación, es necesario instalar el cliente SSH en centOS y luego en Visual Studio Code (VSC), para instalar en centOS nos logeamos mediante la consola del VBOX y ejecutamos los siguientes comandos elevados:

```bash
sudo yum install openssh-server
sudo systemctl start sshd.service
sudo systemctl enable sshd.service
```

- Por último ejecutamos el siguinte comando para verificar que tengamos el servicio SSH ejecutándose: `sudo service sshd status`

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion09.jpg" title="static">

- Verificar que la configuración de red de VBOX esta configurada como `adaptador puente`, por lo cual tomara una dirección IP de nuestro pool de DHCP.

- Ingresar en VBOX por su consolta y ejecutar el comando `ifconfig` para ver la dirección IP que tiene el equipo:

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion10.jpg" title="static">

- Instalaremos el plugin de SSH en el VSC y configuraremos una nueva conexión con la dirección IP anterior y el usuario root como muestra la imagen:

<img src="./Extras/Imagenes/laboratorioNivelacion/Instalacion/Instalacion11.jpg" title="static">

- Si salío todo correctamente, podremos ingresar al equipo mediante SSH por VSC.


## WSL

- Solamente tenemos que correr el comando `wsl.exe --install` en una consola con permisos elevados, una vez que termina, reiniciamos el equipo, se nos van a solicitar datos de usuario y contraseña para finalizar la instalación. Luego veremos que podremos ejecutar un Ubuntu desde el start menu.
- Van a tener que instalar el SSH como en la instalación de centOS y algun otro paquete extra (WSL instala Ubuntu, verificar como serian los comandos necesarios para instalar el SSH en Ubuntu).