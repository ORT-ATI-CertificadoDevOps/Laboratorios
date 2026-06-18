# Load Balancing

## 01: What are we going to learn?
- Vamos a aprender a construir 2 aplicaciones en containares con nginx que manejan 2 contextos diferentes /app1 y /app2.
- Vamos a crear los ECS task definitions y services para ambas aplicaciones aprovechando el balanceador de carga de una sola aplicación y haciendo enrutamiento basado en URI.
- Implementaremos autoscaling para ECS tasks.

## 02: Pre-requisite - Construir las imágenes Docker

Construir dos imágenes con los contextos `/app1` y `/app2`. Los Dockerfiles y el contenido estático están en las carpetas `Application-1/` y `Application-2/`.

Definir la URL del registry ECR (se obtiene desde la consola de ECR):

```bash
ECR_REGISTRY=<account-id>.dkr.ecr.us-east-1.amazonaws.com
```

Autenticarse en ECR:

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin $ECR_REGISTRY
```

### Build

```bash
cd Application-1
docker build -t $ECR_REGISTRY/nginxapp1:latest .

cd ../Application-2
docker build -t $ECR_REGISTRY/nginxapp2:latest .
```

### Probar localmente

- **App1:** http://localhost:81/app1
- **App2:** http://localhost:82/app2

```bash
docker run --name nginxapp1 -p 81:80 --rm -d $ECR_REGISTRY/nginxapp1:latest
docker run --name nginxapp2 -p 82:80 --rm -d $ECR_REGISTRY/nginxapp2:latest
```

### Detener los contenedores

```bash
docker stop nginxapp1
docker stop nginxapp2
```

### Push a ECR

```bash
docker push $ECR_REGISTRY/nginxapp1:latest
docker push $ECR_REGISTRY/nginxapp2:latest
```

## 03: Create Application Load Balancer
- Create Application Load Balancer
    - **Name:** aws-ecs-nginx-lb
    - **Availability Zones:** elegir la VPC creada anteriormente con las subnets **PÚBLICAS**
    - **Load Balancer Security Group:** alb-ecs-nginx permitiendo puerto 80.
    - **Target Group:** temp-tg
    - **Register Targets:** dejar por defecto.

## 04: Create Task Definitions for both App1 and App2
- **App1 Task Definition:** aws-nginx-app1
    - **Launch Type:** Fargate
    - **Container Image:** <replace-with-your-docker-registry-id>or<ecr-registry-aws>/nginxapp1
- **App2 Task Definition:** aws-nginx-app2
    - **Launch Type:** EC2
    - **Container Image:** <replace-with-your-docker-registry-id>or<ecr-registry-aws>/nginxapp2
    

## 05: Create ECS Service for Nginx App1
### Create Service for App1
- **Service Name:** aws-nginx-app1-svc
- **Cluster:** fargate-demo
- Number of Tasks: 2
- Elegir la VPC y security group generado anteriormente
- Select Application Load Balancer
    - Container to Load Balance
    - Target Group Name: ecs-nginx-app1-tg
    - Path Pattern: /app1*
    - Health Check Path: /app1/index.html
- Health check grace period: 147   - **configuración muy importante**
- Testar la app accediendo por URL del Load Balancer. 


## 06: Create ECS Service for Nginx App2 and leverage same load balancer
- Vamos a aprovechar el mismo balanceador que usa app1.
- Vamos a proporcionar el patrón de ruta de App2 como /app2*
- Vamos a necesitas tener **EC2-Linux Lauch Type** cluster creado y preparado con almenos 1 EC2 instance para generar este servicio. 
    - **EC2 Linux Cluster Name:** ecs-ec2-demo (puede ser el generado anteriormente, en caso que ya exista, usarlo, sino crear uno nuevo).

### Create Service for App2

- **Service Name:** aws-nginx-app2-svc
- **Cluster:** ecs-ec2-demo
- Number of Tasks: 2
- Elegir la VPC con las subredes **PÚBLICAS** y el security group.
- Select Application Load Balancer
    - Container to Load Balance
    - Target Group Name: ecs-nginx-app2-tg
    - Path Pattern: /app2*
    - Health Check Path: /app2/index.html
- Health check grace period: 147   - **parametro super importante**
- Testar la app accediendo por URL del Load Balancer. 

**Nota importante:** En resumen, aprovecharemos el uso de un único balanceador de carga de aplicaciones para múltiples aplicaciones alojadas en ECS con enrutamiento basado en URI.


# Service Autoscaling

## 01: Autoscaling: Target Tracking Policy
- Actualice cualquiera de los servicios existentes para agregar la política de escalado automático
- **Service Name:** aws-nginx-app1-svc
    - Minimum Number of Tasks: 1
    - Desired Number of Tasks: 1
    - Maximum Number of Tasks: 3
    - Scaling Policy: Target Tracking
        - Policy Name: RequestPerTarget
        - ECS Service Metric: RequestPerTarget
        - Target Value: 1000
        - Scale-out cooldown period: 60
        - Scale-in cooldown period: 60

## 02: Spin up AWS EC2 Instance, Install and use ApacheBench for generating load
- **East us AMI ID:** Cualquier Amazon Linux AMI.
- **Network:** Elegir la VPC creada anteriormente 
con las subntes **PÚBLICAS**.
- **Security grouo:** Generar un nuevo SG para habilitar el puerto 22.
- **Key pair:** Utilizar la key pair generada anteriormente.

- Conectarse al servidor.
- Instalar **ApacheBench (ab)** utilidad para realizar miles de solicitudes HTTP a su balanceador de carga en un corto período de tiempo.

```
sudo yum install -y http httpd-tools
ab -n 500000 -c 1000 http://<REPLACE-WITH-ALB-URL-IN-YOUR-ENVIRONMENT>.us-east-1.elb.amazonaws.com/app1/index.html
```

- **Scale-Out Activity**: Siga agregando carga hasta que veamos una alarma en CloudWatch y nuevas tareas (2 contenedores más) creadas y registradas en el balanceador de carga
- **Scale-In Activity**: 
Detenga la carga ahora y espere de 5 a 10 minutos.


## 03: Clean up resources
- Actualizar el service para: 
    - Remove Autoscaling policy in service and disable autoscaling
    - Remover Autosclaing policy en service y disable autoscaling.
    - Pasar número de tasks a 0.
    - Esperar entre 5 y 10 minutos y verificar que las taks esten en 0 (containers) ejecutandose.
    - **Hacemos esto para no consumir todo el credito :)**
- Eliminar Load Balancer (ALB) si no lo estamos usando. 
- Eliminar **ecs-ec2-demo** cluster que sigue manteniendo la EC2 instance asociada.
- Eliminar la instancia de **ApacheBench EC2 Instance** creada para validar las politicas de escalado.
