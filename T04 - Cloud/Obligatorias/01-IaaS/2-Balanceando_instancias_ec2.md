## Trabajando con Instancias EC2

### Parte 2: Balanceando instancias EC2

> **Tiempo estimado:** 40 minutos

Siguiendo los lineamientos del ejercicio anterior: (Se puede reutilizar las instancias creadas, en caso de no haber sido eliminadas).

* Desplegar las instancias con nombres diferentes
  * Al crear cada instancia, en la sección **Advanced details → User data**, colocar el siguiente script para automatizar la instalación del web server:

```bash
#!/bin/bash
# Instalar el web server
yum install -y httpd
# Iniciarlo y dejarlo para que arranque al inicio
systemctl start httpd
systemctl enable httpd
# Crear una página web simple
echo "<h1>Welcome to my EC2 Web Server!</h1>" > /var/www/html/index.html
```

  > Reemplazar `nombre-instancia` con el nombre real de cada instancia (ej. `web-01`, `web-02`) para poder identificar cuál responde al hacer pruebas con el ALB.
* Crear un **Application Load Balancer (ALB)**
  * Crear un `Target Group` con ambas instancias asociadas
  * Crear el `ALB` apuntando al `Target Group`

> **Nota:** El Classic Load Balancer (ELB Classic) está en estado *legacy* desde 2022 y no se recomienda su uso en nuevos proyectos. AWS recomienda usar **ALB** para tráfico HTTP/HTTPS y **NLB** para tráfico TCP/UDP de baja latencia.

#### Para discutir en grupo

* Probar la aplicación y verificar el acceso.
* Se pudo acceder? Que falta?
* Bajar una de las instancias y estudiar comportamiento

#### Limpieza de recursos

* `EC2 > Load Balancers` → eliminar el ALB
* `EC2 > Target Groups` → eliminar el Target Group
* `EC2 > Instances` → terminar las instancias

#### Spoiler alert!!!

En caso de dudas, se puede consultar la [Solución](./soluciones/2-Solucion_balanceando_instancias_ec2.md)