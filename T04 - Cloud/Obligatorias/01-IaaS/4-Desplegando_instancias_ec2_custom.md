## Trabajando con Instancias EC2

### Parte 4: Trabajando con Instancias Custom

El objetivo de este práctico es aprender a crear instancias de EC2 customizadas, pasándole al momento de la creación un script que `cloud-init` va a ejecutar en el momento de la creación.

#### Parte A - Tareas

* Desplegar una instancia de `EC2`
  * Tipo: `t2.micro`
  * Nombre: html-web-instance01
  * AZ: us-east-1a
* En el bloque `user-data` colocar el siguiente script:

```bash
#!/bin/bash
sudo yum update -y
sudo yum install httpd -y
sudo systemctl enable httpd
curl -O https://gist.githubusercontent.com/mauricioamendola/9113b526ecb157724187fabddaa95aa8/raw/f424d19c98745843f2abcddd2a7de296ada93880/index.html
sudo mv index.html /var/www/html
sudo systemctl start httpd
```
* Permitir el acceso al servicio web habilitando el puerto `HTTP 80`

#### Parte B - Tareas

Ahora que tenemos nuestra instancia customizada, podemos crear una `AMI` a partir de la instancia creada. Dicha `AMI` la usaremos para desplegar instancias con el servicio ya configurado y la aplicación desplegada. De esta forma podemos generar imágenes de nuestros productos / software base / aplicativos configurados / etc.


#### Spoiler Alert

En caso de trancarse, pueden consultar la ayuda [aquí](./soluciones/4-Solucion_desplegando_instancias_custom.md).
