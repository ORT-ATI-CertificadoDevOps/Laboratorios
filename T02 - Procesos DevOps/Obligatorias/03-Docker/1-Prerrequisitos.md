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
docker run hello-world
```

<p align = "center">
<img src="/Extras/Imagenes/laboratorioDocker/helloWorld.png" width=100%>
</p>

## Próximos pasos
Para el siguiente paso del laboratorio, diríjase a [2 - Generar nuestro primer contenedor](./2-Generar_nuestro_primer_contenedor.md)