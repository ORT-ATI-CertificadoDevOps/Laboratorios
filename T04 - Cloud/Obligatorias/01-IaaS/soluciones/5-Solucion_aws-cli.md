### Solución parte 5: Trabajando con la Cli


#### Crear instancia

```
aws ec2 run-instances --image-id ami-0533f2ba8a1995cf9 \
    --count 1 --instance-type t2.micro \
    --key-name nombre-keypair \
    --security-groups nombre-sg \
    --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=instancia-desde-cli}]'
```

#### Crear un security Group que permita el puerto 80

```
aws ec2 create-security-group \
    --vpc-id id_del_vpc \
    --group-name web-http \
    --description "web access"
```

Debemos de asociarle un Ingress que permita el puerto 80, pero para esto, primero debemos de obtener el Id del security group creado.  

`aws ec2 describe-security-groups --filters Name=group-name,Values=web-http`

Con el Id podemos crear un ingress.  

```
aws ec2 authorize-security-group-ingress \
    --group-id sg-##### \
    --protocol tcp \
    --port 80 \
    --cidr 0.0.0.0/0
```

#### Asociar security group a la instancia creada

Para esto debemos obtener el Id de la instancia y debemos especificar TODOS los security ids que queremos, incluso aquellos que ya están agregados, de lo contrario, estaremos reemplazando los Security Groups en lugar de agregando.  

`aws ec2 modify-instance-attribute <instance-id> --groups <group-id1> <group-idX>`

#### Terminar la instancia 

Debemos de tener a mano el Id de la instancia.  

`aws ec2 terminate-instances --instance-ids <instance_id>`
