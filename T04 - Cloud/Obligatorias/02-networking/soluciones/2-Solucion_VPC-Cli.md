### Solución del trabajo práctico: VPC y Cli

1. Creación del VPC: `aws ec2 create-vpc --cidr-block 10.1.0.0/16` (Copiar y dejar a mano el ID del VPC que devuelve)
2. Creación de una subnet: `aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.1.1.0/24` (Copiar y dejar a mano el ID de la subnet)
3. Creación del IGW: `aws ec2 create-internet-gateway` (Copiar y dejar a mano el ID del IGW)
4. Attach del IGW al VPC: `aws ec2 attach-internet-gateway --internet-gateway-id <igw-id> --vpc-id <vpc-id>`
5. Crear una Route Table: `aws ec2 create-route-table --vpc-id <vpc-id>` (Copiar y dejar a mano el ID de la route table que devuelve)
6. Crear una ruta por default en la route table: `aws ec2 create-route --route-table-id <route-table-id> --destination-cidr-block 0.0.0.0/0 --gateway-id <igw-id>`
7. Asociar la route table a la subnet: `aws ec2 associate-route-table --route-table-id <route-table-id> --subnet-id <subnet-id>`
8. Crear un SG asociado al VPC: `aws ec2 create-security-group --group-name <security-group-name> --description "<description>" --vpc-id <vpc-id>` (Copiar y dejar a mano el ID del SG)
9. Crear una regla en el SG: `aws ec2 authorize-security-group-ingress --group-id <security-group-id> --protocol tcp --port 22 --cidr 0.0.0.0/0`
10. Crear una instancia: `aws ec2 run-instances --image-id <ami-id> --instance-type t2.micro --count 1 --subnet-id <subnet-id> --security-group-ids <security-group-id> --associate-public-ip-address --key-name vockey`

> Para crear un TAG a un recurso basta con ejecutar el comando "`aws ec2 create-tags --resources <resource-id> --tags Key=<some_key>,Value=<some_value>`". Por ejemplo para asignarle el nombre a una instancia lo podemos hacer con el siguiente comando: "`aws ec2 create-tags --resources <instance-id> --tags Key=Name,Value=nombre_instancia`"