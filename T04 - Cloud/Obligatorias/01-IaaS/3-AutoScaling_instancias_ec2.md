## Trabajando con Instancias EC2

### Parte 3: Auto-Scaling de Instancias EC2

Vamos a crear instancias pero esta vez a partir de un Auto Scaling Group o ASG. Para esto tenemos que crear primero un `Lauch Configuration` (`LC`) o `Launch Template` (`LT`). Los `LC` y `LT` cumplen la misma función pero con algunas diferencias sutiles, como por ejemplo:  

* Los `LT` se pueden versionar. Es decir, si queremos modificar algo, basta con editarlo y eso genera una nueva versión del `LT`, en cambio, con los `LC`, debemos de generar un nuevo `LC`.
* Los `LT` nos habilitan a tener múltiples tipos de instancia, por ejemplo, podemos correr algunas instancias de `EC2` usando instancias on-demand y otras usando SPOT instances.  

#### Ejercicios

* Crear un `LT`. (Se puede buscar en la barra de Servicios. Es un EC2 Feature)
  * Nombre: `test-lt-devops`
  * AMI: `ami-02f3f602d23f1659d`
  * Instance Type: `t2.micro`
  * Key pair: el que crearon en el lab anterior
  * Security Groups: Seleccionar uno que permita el tráfico SSH
* Crear un `ASG`
  * Nombre: asg-devops
  * Launch Template: Seleccionar el creado en el paso anterior.
  * Seleccionar subnets `us-east-1a` y `us-east-1b`
  * Seleccionar opción `No Load Balancer`
  * Dejar `Desired Capacity` y `Minimum capacity` en 1 y `Maximum Capacity` en 2

#### Para discutir en grupo

* Que pasa si eliminamos una de las intancias creadas?
* Qué pasa si modificamos el valor de `Desired Capacity` a dos?
* Cómo podemos generar una nueva versión del `LT`?


Nota: No olvidar eliminar el `ASG` para poder terminar las instancias. 
#### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/3-Solucion_autoscaling_instancias_ec2.md).
