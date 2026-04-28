## S3: Sitio Web Estático

> **Tiempo estimado:** 30 minutos

### Objetivos

Usar S3 para publicar una web HTML estática.

### Parte A — Hosting estático

* **Crear un bucket de S3**
  * Nombre: `{napellido}-bucket` (primera letra del nombre + apellido, ej: `jperez-bucket`)
  * Los nombres de bucket son **globalmente únicos** en AWS
  * Región: `us-east-1`
* **Deshabilitar "Block Public Access"** en la configuración del bucket
* **Habilitar Static Website Hosting** (pestaña *Properties* → *Static website hosting*)
  * Index document: `index.html`
* **Agregar una Bucket Policy** para permitir lectura pública:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::{napellido}-bucket/*"
    }
  ]
}
```

* **Descargar y subir la aplicación "Cafe App":** [cafeApp en GitHub](https://github.com/ORT-ATI-CertificadoDevOps/cafeApp)
* Acceder al bucket usando la URL del endpoint de static website (visible en *Properties*)

### Parte B — Política por IP y Lifecycle

* Modificar la Bucket Policy para **restringir el acceso a tu IP pública** únicamente
* Crear un folder llamado `for_archive`
* Crear una **Lifecycle rule** llamada `to_glacier5d`:
  * Filtrar por prefijo: `for_archive/`
  * Transición a *Glacier* después de 5 días

### Limpieza de recursos

* Vaciar el bucket antes de eliminarlo: `S3 > Bucket > Empty`
* Eliminar el bucket: `S3 > Bucket > Delete`

```bash
# Alternativa vía CLI
aws s3 rm s3://<nombre-bucket> --recursive
aws s3api delete-bucket --bucket <nombre-bucket> --region us-east-1
```

### Spoiler Alert

En caso de trancarse, pueden consultar la ayuda [aquí](./soluciones/4-Solucion_crear_s3_bucket.md).
