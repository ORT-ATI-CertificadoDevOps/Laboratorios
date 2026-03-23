# 10 - Jenkins + GIT

Vamos a generar nuestro propio servidor de Git en un contenedor. 
**En caso de estar virtualizando, leer con atención la nota:**
>Nota: Verificar que la máquina virtual cuenta con suficiente memoria ram para poder realizar este laboratorio, para este caso vamos a necesitar al menos que nuestra máquina virtual cuente entre 6-10GB de RAM, ya que la instalación de un Git server utiliza mucha memoria ram. 

## 10.1 - Crear Git Server utilizando Docker
 
- Agregar en el docker-compose el siguiente código debajo de `web`:
```
git:
    container_name: git-server
    image: 'gitlab/gitlab-ce:latest'
    hostname: 'gitlab.example.com'
    ports:
        - '8090:80'
    volumes:
        - '/srv/gitlab/config:/etc/gitlab'
        - '/srv/gitlab/logs:/var/log/gitlab'
        - '/srv/gitlab/data:/var/opt/gitlab'
    networks:
        - net
```
- Ejecutar el comando `docker-compuse up -d`.
> Nota: En caso de querer acceder mediante el hostname gitlab.example.com es necesario agregar un registro en el archivo host del equipo haciendo referencia a esa dirección, en caso contrario ingresar directamente por la web con la dirección ip como se venía haciendo anteriormente.

## 10.2 - Generar nuestro primer repositorio

- Generar un grupo llamado jenkins que sea privado.
- Generar un nuevo proyecto para dicho grupo con el nombre `maven` que también sea privado.

## 10.3 - Generar usuario para interactuar con el repositorio

- Generar un nuevo usuario y hacerlo miembro de grupo anterior.

## 10.4 - Subir el código utilizado en el laboratorio anterior al repositorio

>Nota: Verificar que se tenga instalado git, en caso contrario instalarlo para poder hacer esta parte.
>Nota: Pueden existir problemas de resolución de nombre, en ese caso solucionarlo agregando en el archivo hosts la entrada correspondiente o

- Clonar el repositorio [Maven app example](https://github.com/jenkins-docs/simple-java-maven-app.git) sobre la ruta `/home/jenkins/jenkins-data`.
- Verificar que el repositorio fue clonado de manera correcta.
- Ingresar a nuesro servidor de Git y clonarnos el repositorio generado en el paso 11.2.
- Copiar el contenido de repositorio [Maven app example](https://github.com/jenkins-docs/simple-java-maven-app.git) a nuestro repositorio `maven`. (utilizar el comando cp -r)
- Commitear los cambios y subir el mismo al repositorio.
- Verificar que fue todo subido de manera correcta.

## 10.5 - Integrar nuestro Git server con nuestro Maven job

Vamos a modificar el job de Maven generado en el ejercicio 10 y pasar a utilizar nuestro repositorio.

- Agregar el usuario generado anteriormente como global credentials en el Jenkins.
- Modificar el Job de maven generado en el ejercicio 10, pasando a utilizar la URL de nuestro repositorio alojado en nuestro nuevo servidor de Git y el usuario almacenado en el paso anterior.
- Ejecutar el job y verificar que se construye el .jar a partir del repositorio alojado en nuestro Git server.

## Próximos pasos
Para el siguiente paso del laboratorio, diríjase a [11 - Jenkins y DSL](11-Jenkins_y_DSL.md)