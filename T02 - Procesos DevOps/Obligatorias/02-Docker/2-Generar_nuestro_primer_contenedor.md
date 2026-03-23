## 2 - Generar nuestro primer contenedor
Ahora que tiene todo configurado en su ambiente, es hora de ensuciarnos las manos. En esta sección, ejecutará una [Alpine Linux](http://www.alpinelinux.org/) container (a lightweight linux distribution) 
en su sistema y luego ejecutara el comando `docker run`.

Para comenzar, ejecutemos lo siguiente en nuestra terminal:

```
$ docker pull alpine
```

> **Nota:** Dependiendo de cómo haya instalado Docker en su sistema, es posible que vea un _permission denied_ error después de ejecutar el comando anterior. Pruebe los comandos del tutorial de introducción a Docker: [verify your installation](https://docs.docker.com/engine/getstarted/step_one/#/step-3-verify-your-installation). Si está en Linux, es posible que deba utilizar `sudo` delante de comando `docker`. Como alternativa puedes [create a docker group](https://docs.docker.com/engine/installation/linux/ubuntulinux/#/create-a-docker-group) para arreglar este inconveniente.

El commando `pull` busca la alpine **image** de **Docker registry** y la guarda en nuestro sistema. Puede utilizar el comando `docker images` para ver todas las imagenes alojadas en nuestro sistema.

```
$ docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             VIRTUAL SIZE
alpine                 latest              c51f86c28340        4 weeks ago         1.109 MB
hello-world             latest              690ed74de00f        5 months ago        960 B
```

### 2.1 Docker Run
Bien! Vamos ahora a correr nuestro Docker  **container** basado en esta imagen. Para hacer esto tenemos que utilizar el comando `docker run`.

```
$ docker run alpine ls -l
total 48
drwxr-xr-x    2 root     root          4096 Mar  2 16:20 bin
drwxr-xr-x    5 root     root           360 Mar 18 09:47 dev
drwxr-xr-x   13 root     root          4096 Mar 18 09:47 etc
drwxr-xr-x    2 root     root          4096 Mar  2 16:20 home
drwxr-xr-x    5 root     root          4096 Mar  2 16:20 lib
```
¿Qué sucedio? Por detras, una gran cantidad de cosas ocurrieron mientras llamamos al comando `run`.
1. El cliente de Docker llamo al servicio/demonio de Docker.
2. El servicio/demonio de Docker valido que se encuentra la imagen descargada localmente (alpine en este caso) y en caso de que no se enceuntre, la descargara desde el registrador correspondiente de Docker.
3. El servicio/demonio de Docker create el contenedor y luego ejecuta un comando sobre el contenedor.
4. El servicio/demonio de Docker imprime la salida de comando sobre el cliente de Docker.

Cuando se corre `docker run alpine`, pasando también el comando(`ls -l`), docker ejecuta el comando en especifico sobre el contenedor y nos lista el contenido.

Vamos a hacer algo más interesante.

```
$ docker run alpine echo "hello from alpine"
hello from alpine
```
OK, esa es una salida real. En este caso, el cliente de Docker ejecutó el comando `echo` en nuestro contenedor alpine y luego lo cerró. Si te has dado cuenta, todo eso sucedió bastante rápido. Imagine iniciar una máquina virtual, ejecutar un comando y luego eliminarlo. ¡Ahora sabes por qué dicen que los contenedores son rápidos!

Probemos otro comando.
```
$ docker run alpine /bin/sh
```

¡Espera, no pasó nada! ¿Es eso un error? Bueno, no. Estos shells interactivos se cerrarán después de ejecutar cualquier comando con secuencias de comandos, a menos que se ejecuten en una terminal interactiva; por lo tanto, para que este ejemplo no se cierre, debe `docker run -it alpine /bin/sh`.

Ahora está dentro del shell del contenedor y puede probar algunos comandos como `ls -l`, `uname -a` y otros. Salga del contenedor dando el comando `exit`.

Bien, ahora es el momento de ver el comando `docker ps`. El comando `docker ps` le muestra todos los contenedores que se están ejecutando actualmente.


```
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```

Como no hay contenedores en ejecución, verá una línea en blanco. Probemos una variante más útil: `docker ps -a`

```
$ docker ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                      PORTS               NAMES
36171a5da744        alpine              "/bin/sh"                5 minutes ago       Exited (0) 2 minutes ago                        fervent_newton
a6a9d46d0b2f        alpine             "echo 'hello from alp"    6 minutes ago       Exited (0) 6 minutes ago                        lonely_kilby
ff0a5c3750b9        alpine             "ls -l"                   8 minutes ago       Exited (0) 8 minutes ago                        elated_ramanujan
c317d0a9e3d2        hello-world         "/hello"                 34 seconds ago      Exited (0) 12 minutes ago                       stupefied_mcclintock
```
Lo que ve arriba es una lista de todos los contenedores que ejecutó. Observe que la columna `STATUS` muestra que estos contenedores salieron hace unos minutos. Probablemente se esté preguntando si hay una forma de ejecutar más de un comando en un contenedor. Probemos eso ahora:
```
$ docker run -it alpine /bin/sh
/ # ls
bin      dev      etc      home     lib      linuxrc  media    mnt      proc     root     run      sbin     sys      tmp      usr      var
/ # uname -a
Linux 97916e8cb5dc 4.4.27-moby #1 SMP Wed Oct 26 14:01:48 UTC 2016 x86_64 Linux
```

Ejecutar el comando `run` con los parámetros `-it` nos adjunta a un tty interactivo en el contenedor. Ahora puede ejecutar tantos comandos en el contenedor como desee. Tómese su tiempo para ejecutar sus comandos favoritos.

Eso concluye un recorrido relámpago por el comando `docker run`, que probablemente sea el comando que usará con más frecuencia. Tiene sentido pasar algún tiempo sintiéndose cómodo con él. Para obtener más información sobre `run`, use `docker run --help` para ver una lista de todos los indicadores que admite. A medida que avance, veremos algunas variantes más de `docker run`.


### 1.2 Terminología

En la última sección, vio mucha jerga específica de Docker que puede resultar confusa para algunos. Entonces, antes de continuar, aclaremos algunos términos que se usan con frecuencia en el ecosistema de Docker.

- *Images* - El sistema de archivos y la configuración de nuestra aplicación que se utilizan para crear contenedores. Para obtener más información sobre una imagen de Docker, ejecute `docker inspect alpine`. En la demostración anterior, utilizó el comando `docker pull` para descargar la imagen de **alpine**. Cuando ejecutó el comando `docker run hello-world`, también hizo un `docker pull` para descargar la imagen de **hello-world**.
- *Containers* - Los contenedores ejecutan las aplicaciones reales. Un contenedor incluye una aplicación y todas sus dependencias. Comparte el kernel con otros contenedores y se ejecuta como un proceso aislado en el espacio del usuario en el sistema operativo host. Creó un contenedor usando `docker run` que hizo usando la imagen alpine que descargó. Se puede ver una lista de contenedores en ejecución usando el comando `docker ps`.
- *Docker daemon* - El servicio en segundo plano que se ejecuta en el host que administra la creación, ejecución y distribución de contenedores Docker.
- *Docker client* - La herramienta de línea de comandos que permite al usuario interactuar con el demonio Docker.
- *Docker Store* - Un [registry](https://store.docker.com/) de imágenes de Docker, donde puede encontrar contenedores, complementos, ediciones de Docker confiables y listos para la empresa. Lo usarás más adelante en este tutorial.

## Next Steps
Para el siguiente paso del laboratorio, diríjase a [3.0 Webapps en Docker](./3-Webapp_en_docker.md)