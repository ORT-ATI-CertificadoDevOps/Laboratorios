## Laboratorio

### 2.1 Generar repositorio de Github

- Generar repositorio de Github privado con una única rama `main` y subir el código de la aplicación que se encuentra en el `.zip` sobre ella, para este laboratorio vamos a manejar un flujo de trunkbased.

### 2.2 Conectar el repo con nuestra cuenta de CircleCi

- Iniciar sesión en CircleCI con nuestra cuenta y conectar el repositorio.

### 2.3 Generar S3 buckets en AWS

- Generar 3 S3 buckets en la cuenta de AWS:
    - Utilizar nombres descriptivos.
        - EJ: `dev-circleci-fdwebapp`,`stg-circleci-fdwebapp`, `circleci-fdwebapp`.
    - **NOTA:** Cambiar el nombre del bucket por su nombre o alguna identificación propia, en mi caso yo puse fd por Federico. 
    - Verificar que los buckets quedan con la opción de `Block all public access` desactivada.
    - Verificar luego de una vez creados los buckets, que en la pestaña de `Properties` quede `Enabled` la opción de `Static website hosting`.

### 2.3 Estudiar carpeta .circleci

- Estudiar los archivos que se encuentran dentro de la carpeta `.circleci`, entender que es lo que realizan, cual es su relacionamiento entre si y que tipos de `ENVIRONMENT VARIABLES` se manejan.

### 2.4 Agregar environment variables necesarios y ejecutar

- Agregar todos los environment variables necesarios para que el build and deploy funcionen, una vez agregados, ejecutar manualmente (se puede llegar a ejecutar automático por tener trigger configurado los archivos) desde la consola de CircleCi ejecutar el flujo.

- Si fue correcto el resultado del actions, buscar la URL de nuestro S3 bucket (la misma se encuentra en la ventana de properties y en el apartado de `Static website hosting`).
- Si podemos llegar al sitio y obtenemos un error 403 como la imagen, estamos en lo correcto: 

<p align = "center">
<img src = "../../../Extras/Imagenes/laboratorioGithubActions/403.png" width=100%>
</p>

- Nos resta solamente habilitar permisos en el S3 bucket (buscar como solucionar permiso 403 en S3 bucket como website), cuando arreglen el error por el permiso, deberían de visualizar la siguiente imagen:

<p align = "center">
<img src = "../../../Extras/Imagenes/laboratorioGithubActions/200.png" width=100%>
</p>

- Seguir ejecutando el deployment desde CircleCI con los approval respectivos para cada ambiente y verificar que se haga el deployment de manera correcta.
