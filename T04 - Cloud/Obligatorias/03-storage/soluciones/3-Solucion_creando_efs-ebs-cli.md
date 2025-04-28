### Solución del trabajo práctico: EFS-EBS Cli

#### Parte A: EFS

**Creo el share EFS**

```bash
$ aws efs create-file-system \
--encrypted \
--tags Key=Name,Value=test-efs-cli \
--region us-east-1
```

**Crear el mount target**

```bash
$ aws efs create-mount-target \
--file-system-id <efs-id> \
--subnet-id  <subnet-id> \
--security-group <sg-id> \
--region us-east-1
```
#### Parte B: EBS

**Crear el Volumen**

```bash
$ aws ec2 create-volume \
    --volume-type gp2 \
    --size 10 \
    --availability-zone us-east-1a \
    --tag-specifications 'ResourceType=volume,Tags=[{Key=Name,Value=ebs-volume-cli}]'
```

**Attach del volumen**

```bash
$ aws ec2 attach-volume 
    --instance-id <instance-id> \ 
    --volume-id <volume-id> \ 
    --device /dev/xxxx
```
