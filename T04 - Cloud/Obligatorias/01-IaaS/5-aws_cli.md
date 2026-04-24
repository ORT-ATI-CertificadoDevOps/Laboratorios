## AWS CLI

> **Tiempo estimado:** 35 minutos

### Contexto

La AWS CLI permite gestionar recursos de AWS desde la terminal, habilitando automatización y scripting. Es la base para Infrastructure as Code y pipelines CI/CD.

### Instalación

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install -i /usr/local/aws-cli -b /usr/local/bin
aws --version
# Salida esperada: aws-cli/2.x.x Python/3.x.x ...
```

### Configuración de credenciales

Los datos de acceso están en Vocareum → `AWS Details` → `Show`.

<p align = "center">
<img src="/Extras/Imagenes/laboratorioCloud_EC2/cli/cli01.png">
</p>

<p align = "center">
<img src="/Extras/Imagenes/laboratorioCloud_EC2/cli/cli02.png">
</p>

```bash
aws configure
# AWS Access Key ID []:     <aws_access_key_id>
# AWS Secret Access Key []: <aws_secret_access_key>
# Default region name:      us-east-1
# Default output format:    yaml
```

Configurar el session token por separado:

```bash
aws configure set aws_session_token <aws_session_token>
```

Validar la configuración:

```bash
aws ec2 describe-instances
```

### Ejercicios

Crear una instancia EC2 desde la CLI:
* AMI: `ami-0533f2ba8a1995cf9` *(si no está disponible, obtener el ID actual:)*

```bash
aws ec2 describe-images --owners amazon \
  --filters "Name=name,Values=amzn2-ami-hvm-*-x86_64-gp2" \
  --query "sort_by(Images,&CreationDate)[-1].ImageId" \
  --output text
```

* Tipo: `t2.micro`
* Nombre: `instancia-desde-cli`

Luego investigar cómo:
* Crear un Security Group que permita el puerto 80
* Asociarlo a la instancia creada

Finalmente, terminar la instancia.

#### Referencia de comandos

```bash
# Crear instancia
aws ec2 run-instances \
  --image-id <ami-id> \
  --count 1 \
  --instance-type t2.micro \
  --key-name <key-pair> \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=instancia-desde-cli}]'

# Obtener info de una instancia por nombre
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=instancia-desde-cli"

# Terminar instancia
aws ec2 terminate-instances --instance-ids <instance-id>
```

### Limpieza de recursos

```bash
# Terminar la instancia
aws ec2 terminate-instances --instance-ids <instance-id>

# Eliminar el Security Group (esperar a que la instancia termine)
aws ec2 delete-security-group --group-name web-http
```

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/5-Solucion_aws-cli.md).
