## 3.0 Webapps en Docker
Bien! Ya que tuvimos nuestra primera interacción con el comando `docker run`, practicamos un poco con Doker container y tenemos en claro la terminología. Ya nos encontramos listos para llegar a un ejemplo concreto y desplegar una aplicación web en Docker.

### 3.1 Ejecutar una aplicación web en docker
>**Note:** El código para la web se encuentra en este repo [static-site directory](https://github.com/docker/labs/tree/master/beginner/static-site).

Comencemos por dar pequeños pasos. Primero, usaremos Docker para ejecutar un sitio web estático en un contenedor. El sitio web se basa en una imagen existente. Extraeremos una imagen de Docker de Docker Store, ejecutaremos el contenedor y veremos lo fácil que es configurar un servidor web.

La imagen que va a utilizar es un sitio web de una sola página que ya se creó para esta demostración y está disponible en Docker Store como[`dockersamples/static-site`](https://store.docker.com/community/images/dockersamples/static-site). Puede descargar y ejecutar la imagen directamente de una vez usando `docker run`.

```bash
$ docker run -d dockersamples/static-site
```

>**NOTA:** La versión actual de esta imagen no se ejecuta sin el parámetro `-d` . El parámetro `-d` habilita **detached mode**, que separa el contenedor en ejecución de la terminal / shell y devuelve su indicador después de que se inicia el contenedor. Estamos depurando el problema con esta imagen, pero por ahora, utilizada el parámetro `-d` para este primer ejemplo.

Entonces, ¿qué paso cuándo ejecutamos este comando?

Dado que la imagen no existe en su host de Docker, el demonio de Docker primero la obtiene del registro y luego la ejecuta como un contenedor.

Ahora que el servidor se está ejecutando, ¿ve el sitio web? ¿En qué puerto se está ejecutando? Y lo que es más importante, ¿cómo se accede al contenedor directamente desde nuestra máquina host?

Es muy proable que no puedas responder ninguna de las respuesta ahora! En este caso, el cliente no le dijo a Docker Engine que publicara ninguno de los puertos, por lo que debe volver a ejecutar elcomando `docker run` y agregar dichas instrucciones.

Vamos a volver a ejecutar el comando con algunos parámetros nuevos para publicar puertos y pasar su nombre al contenedor para personalizar el mensaje que se muestra. Usaremos la opción *-d* nuevamente para ejecutar el contenedor en modo separado.

Primero, detenga el contenedor que acaba de lanzar. Para hacer esto, necesitamos el ID del contenedor.

Dado que ejecutamos el contenedor en modo separado, no tenemos que iniciar otra terminal para hacer esto. Ejecuta `docker ps` para ver los contenedores que estan ejecutandose.

```bash
$ docker ps
CONTAINER ID        IMAGE                  COMMAND                  CREATED             STATUS              PORTS               NAMES
a7a0e504ca3e        dockersamples/static-site   "/bin/sh -c 'cd /usr/"   28 seconds ago      Up 26 seconds       80/tcp, 443/tcp     stupefied_mahavira
```

Mira el `CONTAINER ID` en la columna CONTAINER ID. Vas a tener que usar el valor de `CONTAINER ID`, una larga secuencia de caracteres, para identificar el contenedor que desea detener y luego eliminarlo. El siguiente ejemplo proporciona el `CONTAINER ID` en nuestro sistema; debe usar el valor que ve en su terminal.

```bash
$ docker stop a7a0e504ca3e
$ docker rm   a7a0e504ca3e
```

>**Nota:** Una característica interesante es que no es necesario especificar por completo el `CONTAINER ID`. Puede especificar algunos caracteres iniciales y, si es único entre todos los contenedores que ha lanzado, el cliente de Docker lo recogerá de forma inteligente.

Ahora, lancemos un contenedor en modo **detached** como se muestra a continuación:

```bash
$ docker run --name static-site -e AUTHOR="Your Name" -d -P dockersamples/static-site
e61d12292d69556eabe2a44c16cbd54486b2527e2ce4f95438e504afb7b02810
```

Sobre el comando anterior:

*  `-d:` Creará un contenedor con el proceso separado de nuestra terminal.
* `-P:` Publicará todos los puertos de contenedor expuestos en puertos aleatorios en el host de Docker.
* `-e:` Es cómo se pasan las variables de entorno al contenedor.
* `--name:` Le permite especificar un nombre de contenedor.
* `AUTHOR:` Es el nombre de la variable de entorno y `Your Name` es el valor que puedes pasar.

Ahora puede ver los puertos ejecutando con el commando `docker port`.

```bash
$ docker port static-site
443/tcp -> 0.0.0.0:32772
80/tcp -> 0.0.0.0:32773
```

Si estas corriendo [Docker for Mac](https://docs.docker.com/docker-for-mac/), [Docker for Windows](https://docs.docker.com/docker-for-windows/), o Docker en Linux, podes ir a  `http://localhost:[YOUR_PORT_FOR 80/tcp]`. Para este ejemplo hay que ir a `http://localhost:32773`.

Si está utilizando Docker Machine en Mac o Windows, puede encontrar el nombre de host en la línea de comando usando el comando `docker-machine` (asumiendo que estás usando el `default` machine).

```bash
$ docker-machine ip default
192.168.99.100
```
Puedes abrir `http://<YOUR_IPADDRESS>:[YOUR_PORT_FOR 80/tcp]` para ver tu sitio! Por ejemplo: `http://192.168.99.100:32773`.

También puede ejecutar un segundo servidor web al mismo tiempo, especificando una asignación de puerto de host personalizada al servidor web del contenedor.

```bash
$ docker run --name static-site-2 -e AUTHOR="Your Name" -d -p 8888:80 dockersamples/static-site
```

Para implementar esto en un servidor real, solo necesitaría instalar Docker y ejecutar lo anterior comando `docker` (como en este caso, puede ver el `AUTHOR` es Docker que pasamos como una variable de entorno).

Ahora que ha visto cómo ejecutar un servidor web dentro de un contenedor Docker, ¿cómo puede crear su propia imagen de Docker? Esta es la pregunta que exploraremos en la siguiente sección.

Pero primero, detengámonos y eliminemos los contenedores, ya que ya no los usará.

```bash
$ docker stop static-site
$ docker rm static-site
```

Usemos un atajo para eliminar el segundo sitio:

```bash
$ docker rm -f static-site-2
```

Ejecute `docker ps` para asegurarse que el segundo contenedor se elimino correctamente.

```bash
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```

### 3.2 Docker Images

En esta sección, profundicemos en lo que son las imágenes de Docker. Construirá su propia imagen, usará esa imagen para ejecutar una aplicación localmente y, finalmente, enviará algunas de sus propias imágenes a Docker Cloud.

Docker images son la base de los contenedores. En el ejemplo anterior **pulled** la imagen *dockersamples/static-site* el registrador y le pedimos al Docker client ejecutar con contenedor basado en esta imagen. 
Para ver la lista de imágenes que están disponibles localmente en su sistema, ejecute el comando `docker images`.

```bash
$ docker images
REPOSITORY             TAG                 IMAGE ID            CREATED             SIZE
dockersamples/static-site   latest              92a386b6e686        2 hours ago        190.5 MB
nginx                  latest              af4b3d7d5401        3 hours ago        190.5 MB
python                 2.7                 1c32174fd534        14 hours ago        676.8 MB
postgres               9.4                 88d845ac7a88        14 hours ago        263.6 MB
containous/traefik     latest              27b4e0c6b2fd        4 days ago          20.75 MB
node                   0.10                42426a5cba5f        6 days ago          633.7 MB
redis                  latest              4f5f397d4b7c        7 days ago          177.5 MB
mongo                  latest              467eb21035a8        7 days ago          309.7 MB
alpine                 3.3                 70c557e50ed6        8 days ago          4.794 MB
java                   7                   21f6ce84e43c        8 days ago          587.7 MB
```

Arriba hay una lista de imágenes que he extraído del registro y las que he creado yo mismo (en breve veremos cómo). Tendrá una lista diferente de imágenes en su máquina. El `TAG` se refiere a una instantánea particular de la imagen y la `ID` es el identificador único correspondiente para esa imagen.


Para simplificar, puede pensar en una imagen similar a un repositorio de git: las imágenes pueden ser [committed](https://docs.docker.com/engine/reference/commandline/commit/) con cambios y tener múltiples versiones. Cuando no proporciona un número de versión específico, el cliente toma de forma predeterminada `latest`.

Por ejemplo, puede extraer una versión específica imagen de `ubuntu`:

```bash
$ docker pull ubuntu:12.04
```

Si no especifica el número de versión de la imagen, entonces, como se mencionó, el cliente de Docker usará de forma predeterminada una versión llamada `latest`.

Por ejemplo, el comando `docker pull` comando dado a continuación extraerá una imagen llamada `ubuntu:latest`:

```bash
$ docker pull ubuntu
```

Para obtener una nueva imagen de Docker, puede obtenerla de un registro (como Docker Store) o crear la suya propia. Hay cientos de miles de imágenes disponibles en [Docker Store](https://store.docker.com). También puede buscar imágenes directamente desde la línea de comando usando `docker search`.

Una distinción importante con respecto a las imágenes es entre _base images_ t _child images_.

- **Base images:** Son imágenes que no tienen imágenes principales, generalmente imágenes con un sistema operativo como ubuntu, alpine o debian.

- **Child images:** Son imágenes que se basan en imágenes base y agregan funcionalidad adicional.


Otro concepto clave es la idea de _official images_ y _user images_. 
(Ambas pueden ser imágenes base images o child images).

- **Official images:** Son imágenes autorizadas por Docker. Docker, Inc. patrocina un equipo dedicado que es responsable de revisar y publicar todo el contenido de los repositorios oficiales. Este equipo trabaja en colaboración con mantenedores de software ascendentes, expertos en seguridad y la comunidad de Docker en general. Estos no tienen el prefijo de una organización o nombre de usuario. En la lista de imágenes de arriba, `python`, `node`, `alpine` and `nginx` las imágenes son imágenes oficiales (base). Para obtener más información sobre ellos, consulte el [Official Images Documentation](https://docs.docker.com/docker-hub/official_repos/).

- **User images:** Son imágenes creadas y compartidas por usuarios como usted. Se basan en imágenes base y agregan funcionalidad adicional. Por lo general, estos tienen el formato `user/image-name`. El valor `user` en el nombre de la imagen está el nombre de su organización o usuario de Docker Store.

### 3.3 Creando nuestra primer imagen

>**Note:** 
El código de esta sección se encuentra en este repositorio [flask-app](https://github.com/docker/labs/tree/master/beginner/flask-app).


Ahora que comprende mejor las imágenes, es hora de crear las suyas propias. Nuestro objetivo aquí es crear una imagen que contenga una pequeña aplicación [Flask](http://flask.pocoo.org).

**El objetivo de este ejercicio es crear una imagen de Docker que ejecutará una aplicación Flask.**

Haremos esto juntando primero los componentes para un generador de imágenes de gatos aleatorio construido con Python Flask, luego haremos el proceso de _dockerizing_ escribiendo un _Dockerfile_. Finalmente, crearemos la imagen y luego la ejecutaremos.

### 3.3.1 Cree una aplicación Python Flask que muestre imágenes de gatos al azar

Para los propósitos de este taller, hemos creado una pequeña y divertida aplicación Python Flask que muestra un gato al azar `.gif` cada vez que se carga, porque, ya sabes, ¿a quién no le gustan los gatos?

Empiece por crear un directorio llamado ```flask-app``` donde crearemos los siguientes archivos:

- [app.py](#apppy)
- [requirements.txt](#requirementstxt)
- [templates/index.html](#templatesindexhtml)
- [Dockerfile](#dockerfile)

Asegurate que ```cd flask-app``` antes de comenzar a crear los archivos, porque no desea comenzar a agregar un montón de otros archivos aleatorios a su imagen.

#### app.py

Crea el archivo **app.py** con el siguiente contenido:

```python
from flask import Flask, render_template
import random

app = Flask(__name__)

# list of cat images
images = [
   "https://c.tenor.com/6tlB3xGf1AoAAAAM/cat-white.gif",
    "https://c.tenor.com/2v1aDCelTJgAAAAM/cat-cats.gif",
    "https://c.tenor.com/lolcvIC490YAAAAM/happy-smile.gif",
    "https://c.tenor.com/ZhfMGWrmCTcAAAAM/cute-kitty-best-kitty.gif",
    "https://c.tenor.com/e_cOg0wWyQUAAAAM/cat-finger.gif",
    "https://c.tenor.com/Bid8b-ni4EMAAAAM/cat-cat-standing.gif",
    "https://c.tenor.com/_vG3rPAG2v4AAAAM/cat-cat-attack.gif",
    "https://c.tenor.com/ZGld2C6W3O8AAAAM/cat-fight.gif",
    "https://c.tenor.com/TPDruIOzoEkAAAAM/kitten-smol.gif",
    "https://c.tenor.com/1B2lKBCpZnIAAAAM/billu-billu-fight.gif",
    "https://c.tenor.com/ZyKrjlLW-t8AAAAM/cat-cats.gif",
    "https://c.tenor.com/OlM-1hGiioMAAAAM/skate-skateboard.gif"
    ]

@app.route('/')
def index():
    url = random.choice(images)
    return render_template('index.html', url=url)

if __name__ == "__main__":
    app.run(host="0.0.0.0")
```

#### requirements.txt

Para instalar los módulos de Python necesarios para nuestra aplicación, necesitamos crear un archivo llamado **requirements.txt** and add the following line to that file:

```
Flask==0.10.1
```

#### templates/index.html

Crea un directorio llamado `templates` y crea el archivo **index.html** en el directorio con el siguiente contenido:

```html
<html>
  <head>
    <style type="text/css">
      body {
        background: black;
        color: white;
      }
      div.container {
        max-width: 500px;
        margin: 100px auto;
        border: 20px solid white;
        padding: 10px;
        text-align: center;
      }
      h4 {
        text-transform: uppercase;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h4>Cat Gif of the day</h4>
      <img src="{{url}}" />
      <p><small>Courtesy: <a href="http://www.buzzfeed.com/copyranter/the-best-cat-gif-post-in-the-history-of-cat-gifs">Buzzfeed</a></small></p>
    </div>
  </body>
</html>
```

### 3.3.2 Write a Dockerfile

Queremos crear una imagen de Docker con esta aplicación web. Como se mencionó anteriormente, todas las imágenes de usuario se basan en una _imagen base_. Dado que nuestra aplicación está escrita en Python, crearemos nuestra propia imagen de Python basada en [Alpine](https://store.docker.com/images/alpine). La haremos usando un archivo **Dockerfile**.

Un [Dockerfile](https://docs.docker.com/engine/reference/builder/) es un archivo de texto que contiene una lista de comandos que el demonio de Docker llama al crear una imagen. El Dockerfile contiene toda la información que Docker necesita saber para ejecutar la aplicación. 

Una imagen base de Docker desde la que ejecutar, la ubicación del código de su proyecto, las dependencias que tenga y los comandos que se ejecutarán al inicio. Es una forma sencilla de automatizar el proceso de creación de imágenes. La mejor parte es que el [commands](https://docs.docker.com/engine/reference/builder/) que escribe en un Dockerfile son casi idénticos a sus comandos de Linux equivalentes. Esto significa que realmente no tiene que aprender una nueva sintaxis para crear sus propios Dockerfiles.


1. Crear un archivo **Dockerfile**, y agregar el contenido a continuación:

  Comenzaremos especificando nuestra imagen base, usando el comando `FROM`:

  ```
  FROM alpine:3.5
  ```

2. El siguiente paso suele ser escribir los comandos para copiar los archivos e instalar las dependencias. Pero primero instalaremos el paquete pip de Python en la distribución de linux alpine. Esto no solo instalará el paquete pip, sino también cualquier otra dependencia, que incluye el intérprete de Python. Agregue el siguiente comando [RUN](https://docs.docker.com/engine/reference/builder/#run):

  ```
  RUN apk add --update py2-pip
  ```

3. Agreguemos los archivos que componen la aplicación Flask..

  Instale todos los requisitos de Python para que se ejecute nuestra aplicación. Esto se logrará agregando las líneas:

  ```
  COPY requirements.txt /usr/src/app/
  RUN pip install --no-cache-dir -r /usr/src/app/requirements.txt
  ```

  Copie los archivos que ha creado anteriormente en nuestra imagen usando el comando [COPY](https://docs.docker.com/engine/reference/builder/#copy).

  ```
  COPY app.py /usr/src/app/
  COPY templates/index.html /usr/src/app/templates/
  ```

4. Especifique el número de puerto que debe exponerse. Dado que nuestra aplicación de flask se está ejecutando en `5000` eso es lo que expondremos.

  ```
  EXPOSE 5000
  ```

5. El último paso es el comando para ejecutar la aplicación, que es simplemente - `python ./app.py`. Utilice el comando [CMD](https://docs.docker.com/engine/reference/builder/#cmd):

  ```
  CMD ["python", "/usr/src/app/app.py"]
  ```

  El objetivo principal del comando `CMD` es decirle al contenedor qué comando debe ejecutar de forma predeterminada cuando se inicia.

6. Verificar el archivo Dockerfile.

  Nuestro archivo `Dockerfile` ya esta listo. Así es como funcionara:

  ```
  # our base image
  FROM alpine:3.5

  # Install python and pip
  RUN apk add --update py2-pip

  # install Python modules needed by the Python app
  COPY requirements.txt /usr/src/app/
  RUN pip install --no-cache-dir -r /usr/src/app/requirements.txt

  # copy files required for the app to run
  COPY app.py /usr/src/app/
  COPY templates/index.html /usr/src/app/templates/

  # tell the port number the container should expose
  EXPOSE 5000

  # run the application
  CMD ["python", "/usr/src/app/app.py"]
  ```

### 3.3.3 Buildear la imagen

Ahora que tenemos nuestro `Dockerfile`, vamos a construir nuestra imagen. El comando `docker build` comando hace el trabajo pesado de crear una imagen de `Dockerfile`.

Cuando ejecutas el comando `docker build`, asegúrese de reemplazar`<YOUR_USERNAME>` con su nombre de usuario. Este nombre de usuario debe ser el mismo que creó al registrarse en [Docker Cloud](https://cloud.docker.com). Si aún no lo ha hecho, continúe y cree una cuenta.

El comando `docker build` es bastante simple - toma un nombre de etiqueta opcional con el parametro `-t`, y la ubicación del archivo `Dockerfile` - the `.` indica el directorio en donde estoy parado:

```
$ docker build -t <YOUR_USERNAME>/myfirstapp .
Sending build context to Docker daemon 9.728 kB
Step 1 : FROM alpine:latest
 ---> 0d81fc72e790
Step 2 : RUN apk add --update py-pip
 ---> Running in 8abd4091b5f5
fetch http://dl-4.alpinelinux.org/alpine/v3.3/main/x86_64/APKINDEX.tar.gz
fetch http://dl-4.alpinelinux.org/alpine/v3.3/community/x86_64/APKINDEX.tar.gz
(1/12) Installing libbz2 (1.0.6-r4)
(2/12) Installing expat (2.1.0-r2)
(3/12) Installing libffi (3.2.1-r2)
(4/12) Installing gdbm (1.11-r1)
(5/12) Installing ncurses-terminfo-base (6.0-r6)
(6/12) Installing ncurses-terminfo (6.0-r6)
(7/12) Installing ncurses-libs (6.0-r6)
(8/12) Installing readline (6.3.008-r4)
(9/12) Installing sqlite-libs (3.9.2-r0)
(10/12) Installing python (2.7.11-r3)
(11/12) Installing py-setuptools (18.8-r0)
(12/12) Installing py-pip (7.1.2-r0)
Executing busybox-1.24.1-r7.trigger
OK: 59 MiB in 23 packages
 ---> 976a232ac4ad
Removing intermediate container 8abd4091b5f5
Step 3 : COPY requirements.txt /usr/src/app/
 ---> 65b4be05340c
Removing intermediate container 29ef53b58e0f
Step 4 : RUN pip install --no-cache-dir -r /usr/src/app/requirements.txt
 ---> Running in a1f26ded28e7
Collecting Flask==0.10.1 (from -r /usr/src/app/requirements.txt (line 1))
  Downloading Flask-0.10.1.tar.gz (544kB)
Collecting Werkzeug>=0.7 (from Flask==0.10.1->-r /usr/src/app/requirements.txt (line 1))
  Downloading Werkzeug-0.11.4-py2.py3-none-any.whl (305kB)
Collecting Jinja2>=2.4 (from Flask==0.10.1->-r /usr/src/app/requirements.txt (line 1))
  Downloading Jinja2-2.8-py2.py3-none-any.whl (263kB)
Collecting itsdangerous>=0.21 (from Flask==0.10.1->-r /usr/src/app/requirements.txt (line 1))
  Downloading itsdangerous-0.24.tar.gz (46kB)
Collecting MarkupSafe (from Jinja2>=2.4->Flask==0.10.1->-r /usr/src/app/requirements.txt (line 1))
  Downloading MarkupSafe-0.23.tar.gz
Installing collected packages: Werkzeug, MarkupSafe, Jinja2, itsdangerous, Flask
  Running setup.py install for MarkupSafe
  Running setup.py install for itsdangerous
  Running setup.py install for Flask
Successfully installed Flask-0.10.1 Jinja2-2.8 MarkupSafe-0.23 Werkzeug-0.11.4 itsdangerous-0.24
You are using pip version 7.1.2, however version 8.1.1 is available.
You should consider upgrading via the 'pip install --upgrade pip' command.
 ---> 8de73b0730c2
Removing intermediate container a1f26ded28e7
Step 5 : COPY app.py /usr/src/app/
 ---> 6a3436fca83e
Removing intermediate container d51b81a8b698
Step 6 : COPY templates/index.html /usr/src/app/templates/
 ---> 8098386bee99
Removing intermediate container b783d7646f83
Step 7 : EXPOSE 5000
 ---> Running in 31401b7dea40
 ---> 5e9988d87da7
Removing intermediate container 31401b7dea40
Step 8 : CMD python /usr/src/app/app.py
 ---> Running in 78e324d26576
 ---> 2f7357a0805d
Removing intermediate container 78e324d26576
Successfully built 2f7357a0805d
```

Si no tienes la imagen `alpine:3.5`, el cliente primero extraerá la imagen y luego creará su imagen. Por lo tanto, su salida al ejecutar el comando se verá diferente a la mía. Si todo salió bien, ¡tú imagen debería estar lista! Ejecuta `docker images` y mira si tu imagen (`<YOUR_USERNAME>/myfirstapp`).

### 3.3.4 Corre tu imagen
El siguiente paso en esta sección es ejecutar la imagen y ver si realmente funciona.

```bash
$ docker run -p 8888:5000 --name myfirstapp YOUR_USERNAME/myfirstapp
 * Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)
```

Fijate en `http://localhost:8888` y tu aplicación debería de estar corriendo. **Nota** Si está utilizando Docker Machine, es posible que deba abrir otra terminal y determinar la dirección IP del contenedor usando `docker-machine ip default`.


Presionar el botón en la web para ver más imágenes de gatos.


### 3.3.5 Sube tu imagen al registry
Ahora que ha creado y probado su imagen, puede subirlia a [Docker Cloud](https://cloud.docker.com).

Primero debe iniciar sesión en su cuenta de Docker Cloud, para hacer eso:

```bash
docker login
```

Ingresa `YOUR_USERNAME` y `password` cuando se te pidan. 

Ahora todo lo que tienes que hacer es:

```bash
docker push YOUR_USERNAME/myfirstapp
```

Ahora que ha terminado con este contenedor, detengalo y eliminelo, ya que no lo volverá a usar.


Abra otra ventana de terminal y ejecute los siguientes comandos:

```bash
$ docker stop myfirstapp
$ docker rm myfirstapp
```

o

```bash
$ docker rm -f myfirstapp
```

### 3.3.6 Dockerfile resumen comandos


Aquí hay un resumen rápido de los pocos comandos básicos que usamos en nuestro Dockerfile:

- *FROM:* Utilizado para iniciar una nueva construcción de un contenedor y setear la imagen base a ser utilizada. Todo Dockerfile debe de comenzar con la instrucción FROM.
- *RUN:* La instrucción RUN ejecutará cualquier comando en una nueva capa encima de la imagen actual y confirmará los resultados. La imagen resultante se usará para el siguiente paso en el Dockerfile.
- *CMD:* El objetivo principal de un CMD es proporcionar valores predeterminados para un contenedor en ejecución. Estos valores predeterminados pueden incluir un ejecutable o pueden omitir el ejecutable, en cuyo caso también debe especificar una instrucción ENTRYPOINT.
- *LABEL:* La instrucción LABEL agrega metadatos a una imagen. Una ETIQUETA es un par clave-valor. Para incluir espacios dentro de un valor LABEL, use comillas y barras invertidas como lo haría en el análisis de línea de comandos.
- *EXPOSE:* La instrucción EXPOSE informa a Docker que el contenedor escucha en los puertos de red especificados en tiempo de ejecución. Puede especificar si el puerto escucha en TCP o UDP, y el valor predeterminado es TCP si no se especifica el protocolo.
- *ENV:* La instrucción ENV establece la variable de entorno _clave_ en el valor _valor_. Este valor estará en el entorno para todas las instrucciones posteriores en la etapa de construcción y también se puede reemplazar en línea en muchas. El valor se interpretará para otras variables de entorno, por lo que se eliminarán las comillas si no se escapan. Al igual que el análisis de la línea de comandos, se pueden usar comillas y barras invertidas para incluir.
- *ADD:* La instrucción ADD copia nuevos archivos, directorios o direcciones URL de archivos remotos desde _src_ y los agrega al sistema de archivos de la imagen en la ruta _dest_.
- *COPY:* La instrucción COPY copia nuevos archivos o directorios desde _src_ y los agrega al sistema de archivos del contenedor en la ruta _dest_.
- *ENTRYPOINT:* Un ENTRYPOINT le permite configurar un contenedor que se ejecutará como un ejecutable.
- *VOLUME:* La instrucción VOLUME crea un punto de montaje con el nombre especificado y lo marca como que contiene volúmenes montados externamente desde un host nativo u otros contenedores.
- *USER:* La instrucción USER establece el nombre de usuario (o UID) y, opcionalmente, el grupo de usuarios (o GID) que se usará al ejecutar la imagen y para cualquier instrucción RUN, CMD y ENTRYPOINT que la siga en el Dockerfile.
- *WORKDIR:* La instrucción WORKDIR establece el directorio de trabajo para cualquier instrucción RUN, CMD, ENTRYPOINT, COPY y ADD que le sigue en el Dockerfile. Si WORKDIR no existe, se creará incluso si no se usa en ninguna instrucción posterior de Dockerfile.