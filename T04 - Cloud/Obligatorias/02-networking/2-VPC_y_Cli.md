## VPC con AWS CLI

> **Tiempo estimado:** 35 minutos

### Prerequisitos

* Haber completado el lab anterior (Configurando un VPC)

### Objetivos

Crear los mismos recursos del lab anterior usando la AWS CLI.
    
<p align = "center">
<img src="/Extras/Imagenes/labNetworking/vpc/arquitectura.png">
</p>

### Orden de creación

1. VPC
2. Subnets (dos, una por AZ)
3. Internet Gateway → asociar al VPC
4. Route Table → agregar ruta al IGW → asociar subnets
5. Security Group con reglas SSH y HTTP
6. Instancia EC2

### Referencias

Las acciones de creación de recursos comienzan con `create-`. Usar `help` para ver todas las opciones:

```bash
aws ec2 create-vpc help
aws ec2 create-subnet help
aws ec2 create-internet-gateway help
aws ec2 create-security-group help
aws ec2 run-instances help
```

> **Tip:** Guardar los IDs retornados en variables de shell simplifica los comandos siguientes:

```bash
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --query 'Vpc.VpcId' --output text)
echo $VPC_ID
```

### Limpieza de recursos

```bash
# Terminar instancia
aws ec2 terminate-instances --instance-ids <instance-id>

# Desasociar y eliminar IGW
aws ec2 detach-internet-gateway --internet-gateway-id <igw-id> --vpc-id <vpc-id>
aws ec2 delete-internet-gateway --internet-gateway-id <igw-id>

# Eliminar subnet, SG y VPC
aws ec2 delete-subnet --subnet-id <subnet-id>
aws ec2 delete-security-group --group-id <sg-id>
aws ec2 delete-vpc --vpc-id <vpc-id>
```

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/2-Solucion_VPC-Cli.md).
