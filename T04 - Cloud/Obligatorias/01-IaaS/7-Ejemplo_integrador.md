## Ejemplo Integrador

> **Tiempo estimado:** 60 minutos

### Contexto

Este práctico integra los conceptos del módulo: AMIs custom, Auto Scaling Groups, Load Balancing y Networking. Se compone de dos partes que aumentan en complejidad.

### Prerequisitos

* AMI custom creada en el Lab 4 (Instancias EC2 Customizadas)
* Conceptos de VPC, subnets y NAT Gateway (Labs de Networking)

### Parte A: Arquitectura pública

Desplegar la siguiente arquitectura usando la AMI del Lab 4:

* Dos instancias EC2 bajo un **Auto Scaling Group**
* Un **Application Load Balancer** balanceando las instancias
* Ambas instancias en distintas subnets y AZs dentro de un **VPC dedicado**
* La aplicación debe ser accesible desde internet

<p align = "center">
<img src="/Extras/Imagenes/laboratorioCloud_EC2/ec2/architecture02.png" alt="Arquitectura">
</p>

### Parte B: Arquitectura con subnets privadas

Extender la arquitectura para que las instancias EC2 queden en **subnets privadas**, sin IP pública directa:

* Subnets públicas: ALB y Bastion Host (con acceso al IGW)
* Subnets privadas: instancias EC2 del ASG
* **NAT Gateway** en la subnet pública para que las instancias privadas tengan salida a internet
* **Bastion Host** para conectarse por SSH a las instancias privadas

<p align = "center">
<img src="Extras/Imagenes/laboratorioCloud_EC2/ec2/architecture-complete.png" alt="Arquitectura">
</p>

### Limpieza de recursos

Eliminar en el siguiente orden para evitar errores de dependencia:

1. Auto Scaling Group
2. Application Load Balancer y Target Group
3. NAT Gateway *(genera costo por hora aunque no haya tráfico)*
4. Instancia EC2 (Bastion)
5. Elastic IP asociada al NAT Gateway
6. Subnets, Route Tables, Internet Gateway
7. VPC
