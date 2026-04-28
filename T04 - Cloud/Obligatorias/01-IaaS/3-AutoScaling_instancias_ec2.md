## Trabajando con Instancias EC2

### Parte 3: Auto-Scaling de Instancias EC2

Vamos a crear instancias pero esta vez a partir de un Auto Scaling Group o ASG. Para esto tenemos que crear primero un `Launch Template` (`LT`). Los `LT` cumplen con:  

* Se pueden versionar. Es decir, si queremos modificar algo, basta con editarlo y eso genera una nueva versión del `LT`.
* Habilitan a tener múltiples tipos de instancia, por ejemplo, podemos correr algunas instancias de `EC2` usando instancias on-demand y otras usando SPOT instances.  

> **Tiempo estimado:** 25 minutos

#### Ejercicios

* Crear un `LT`. (Se puede buscar en la barra de Servicios. Es un EC2 Feature)
  * Nombre: `test-lt-devops`
  * AMI: `ami-098e39bafa7e7303d` *(si no está disponible, buscar el ID actual de Amazon Linux)*
  * Instance Type: `t3.micro`
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


#### Limpieza de recursos

Para evitar consumo innecesario de créditos, eliminar en el siguiente orden:

1. `EC2 > Auto Scaling Groups` → eliminar `asg-devops`
2. `EC2 > Launch Templates` → eliminar `test-lt-devops`

> Las instancias se terminan automáticamente al eliminar el ASG.

```bash
# Alternativa vía CLI
aws autoscaling delete-auto-scaling-group --auto-scaling-group-name asg-devops --force-delete
aws ec2 delete-launch-template --launch-template-name test-lt-devops
```

#### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/3-Solucion_autoscaling_instancias_ec2.md).
