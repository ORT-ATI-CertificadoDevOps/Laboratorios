## Instalación

### Prerrequisitos
No se necesitan habilidades específicas para este laboratorio más allá de un manejo básico con la línea de comandos y el uso de un editor de texto.

La experiencia previa en el desarrollo de aplicaciones web será útil, pero no es necesaria. A medida que avance en el tutorial, haremos uso de [Docker Cloud](https://cloud.docker.com/). 

Hacerse una cuenta en la página anterior.

### Preparando nuestro ambiente
Configurar todas las herramientas en su ambiente de trabajo puede ser una tarea que demante mucho tiempo, pero hacer que Docker esté en funcionamiento en su sistema operativo de preferencia se ha vuelto muy fácil.

La *guía de introducción* en Docker tiene instrucciones detalladas para configurar Docker en [Mac](https://docs.docker.com/docker-for-mac/), [Linux](https://docs.docker.com/engine/installation/linux/) and [Windows](https://docs.docker.com/docker-for-windows/).

*Si está utilizando Docker para Windows* asegúrese de tener [shared your drive](https://docs.docker.com/docker-for-windows/#shared-drives).

*Nota importante* Si está usando una versión anterior de Windows o MacOS, es posible que deba usar [Docker Machine](https://docs.docker.com/machine/overview/).

*Todos los comandos funcionan en bash o Powershell en Windows*

Una vez que haya terminado de instalar Docker, valide su instalación de Docker ejecutando lo siguiente:
```
$ docker run hello-world
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
03f4658f8b78: Pull complete
a3ed95caeb02: Pull complete
Digest: sha256:8be990ef2aeb16dbcb9271ddfe2610fa6658d13f6dfb8bc72074cc1ca36966a7
Status: Downloaded newer image for hello-world:latest

Hello from Docker.
This message shows that your installation appears to be working correctly.
...
```
## Próximos pasos
Para el siguiente paso del laboratorio, diríjase a [2 - Generar nuestro primer contenedor](./2-Generar_nuestro_primer_contenedor.md)