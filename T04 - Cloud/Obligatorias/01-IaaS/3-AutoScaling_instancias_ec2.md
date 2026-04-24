## Auto Scaling Groups

> **Tiempo estimado:** 25 minutos

### Contexto

Los Auto Scaling Groups (ASG) mantienen automáticamente la cantidad de instancias deseada. Si una instancia falla, el ASG la reemplaza; si la carga aumenta, puede agregar instancias hasta el máximo configurado.

Para crear un ASG se necesita primero un **Launch Template (LT)**: define las características de las instancias a lanzar y soporta versionado — modificar un LT genera una nueva versión sin perder la anterior.

### Prerequisitos

* Par de claves SSH creado en el lab anterior

### Objetivos

* Crear un Launch Template con la configuración de la instancia
* Crear un Auto Scaling Group usando el Launch Template
* Explorar el comportamiento de auto-healing y escalado

### Tareas

**1. Crear un Launch Template**

* Nombre: `test-lt-devops`
* AMI: `ami-098e39bafa7e7303d` *(si no está disponible, buscar Amazon Linux en el catálogo)*
* Tipo: `t3.micro`
* Key pair: el creado en el lab anterior
* Security Groups: permitir SSH

**2. Crear un Auto Scaling Group**

* Nombre: `asg-devops`
* Launch Template: el del paso anterior
* Subnets: `us-east-1a` y `us-east-1b`
* Load Balancer: `No Load Balancer`
* Desired Capacity: `1` · Minimum: `1` · Maximum: `2`

### Para discutir en grupo

* ¿Qué pasa si se elimina manualmente una instancia creada por el ASG?
* ¿Qué pasa si se modifica el `Desired Capacity` a 2?
* ¿Cómo se genera una nueva versión del Launch Template?

### Limpieza de recursos

Eliminar en orden:

1. `EC2 > Auto Scaling Groups` → eliminar `asg-devops`
2. `EC2 > Launch Templates` → eliminar `test-lt-devops`

> Las instancias se terminan automáticamente al eliminar el ASG.

```bash
# Alternativa vía CLI
aws autoscaling delete-auto-scaling-group --auto-scaling-group-name asg-devops --force-delete
aws ec2 delete-launch-template --launch-template-name test-lt-devops
```

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/3-Solucion_autoscaling_instancias_ec2.md).
