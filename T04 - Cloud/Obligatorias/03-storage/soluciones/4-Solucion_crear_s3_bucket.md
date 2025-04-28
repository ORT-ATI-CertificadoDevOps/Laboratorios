### Solución del trabajo práctico: Web app en S3

#### Parte A:

1. Creación del bucket.

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st01.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st02.png">
</p>

2. Configurar bajo properties, el perfíl de **Web Hosting**, para esto hay que ir  **"Properties"** y buscar **"Static Web Hosting"** . Luego subir el contenido de la web.


<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st03.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st04.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st05.png">
</p>

3. Comprobar el acceso. Deberia de darnos `Forbidden`. Esto es porque el contenido del bucket no está público por default.

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st06.png">
</p>

---
#### Parte B

4. Configurar una Policy para el bucket para permitir el acceso desde nuestra ip pública.
   
<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st07.png">
</p>

**Código de la Policy**
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicRead",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:*",
            "Resource": [
                "arn:aws:s3:::MY-BUCKET-NAME", 
                "arn:aws:s3:::MY-BUCKET-NAME/*"
            ],
            "Condition": {
                "IpAddress": {
                    "aws:SourceIp": "MY_PUBLIC_IP"
                }
            }
        }
    ]
}
```

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st08.png">
</p>

Si todo sale bien se deberia de ver la web:

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st09.png">
</p>

5. Crear un folder para los elementos `archive`
   
<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st10.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st11.png">
</p>

6. Bajo management, crear una `Lifecycle Rule`


<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st12.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st13.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labStorage/st14.png">
</p>

