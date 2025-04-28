## Trabajando con Instancias EC2

### Parte 2: Balanceando instancias EC2

Siguiendo los lineamientos del ejercicio anterior: (Se puede reutilizar las instancias creadas, en caso de no haber sido eliminadas).    

* Desplegar las instancias con nombres diferentes
  * Instalar en cada una el paquete httpd: `yum install httpd`
  * Crear una página web sencilla cuyo contenido refleje el nombre de la instancia
  * Depositar el archivo `index.html` en `/var/www/html`
  * Iniciar servicio httpd: `systemctl start httpd`
* Desplegar un **ELB Classic**
* Agregar al ELB creado, las dos instancias desplegadas
* Crear un **ELB Application**
* Repetir el ejercicio pero usando un `Application Load Balancer`
  * Se debe crear un `Target Group` con la instancia asociada a dicho `TG`
  * Luego crear el `ALB` apuntando al nuevo `TG`
  

#### Para discutir en grupo

* Probar la aplicación y verificar el acceso.
* Se pudo acceder? Que falta?
* Bajar una de las instancias y estudiar comportamiento
#### Spoiler alert!!!

En caso de dudas, se puede consultar la [Solución](./soluciones/2-Solucion_balanceando_instancias_ec2.md)