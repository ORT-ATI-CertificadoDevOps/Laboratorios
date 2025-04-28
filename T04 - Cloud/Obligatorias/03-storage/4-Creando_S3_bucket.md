## Trabajando con S3: Web app en S3

### Objetivos

Vamos a usar la capacidad de S3 para publicar una web html sencilla.

**Parte A**

* Crear un bucket de S3
  * Nombre: `{napellido}-bucket` donde `napellido` es la primera letra del nombre + apellido.
  * Asignarle el perfil "WebHosting" (Luego de creado el bucket, bajo la pestañana **"Properties"**)
* Descargar la aplicación "Cafe App" de Aulas
  * En la web de la materia > material complementario > [`Static WebSite - Cafe App`](https://aulas.ort.edu.uy/mod/resource/view.php?id=386634)
* Probar acceso a la aplicación

**Parte B**

* Crear una policy y darle acceso solo a nuestra ip pública
* Crear un folder de nombre `for_archive`
* Crear una `Lifecycle rule` llamada `to_glacier5d` y filtrar por prefijo usando el directorio creado


#### Spoiler Alert

En caso de trancarse, pueden consultar la ayuda [aquí](./soluciones/4-Solucion_crear_s3_bucket.md).

