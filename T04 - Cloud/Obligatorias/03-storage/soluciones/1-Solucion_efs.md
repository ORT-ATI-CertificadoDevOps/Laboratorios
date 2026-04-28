### Solución: EFS

#### Paso 1: Crear el share EFS

En la consola: `EFS > Create file system`

* Name: `efs-practico`
* VPC: seleccionar el VPC de trabajo
* Availability and Durability: `Regional`

Click en **Create**.

#### Paso 2: Configurar el Security Group para NFS

El EFS necesita un SG que permita el puerto `TCP 2049` (NFS) desde las instancias EC2.

* `EC2 > Security Groups > Create security group`
  * Nombre: `sg-efs-nfs`
  * Inbound rule: `TCP 2049` desde el SG de las instancias EC2

#### Paso 3: Crear Mount Target

En el EFS creado: `Network > Manage`

* Agregar el SG `sg-efs-nfs` a los mount targets en cada AZ

#### Paso 4: Montar en la instancia

```bash
# Amazon Linux — instalar soporte NFS
sudo yum install nfs-utils -y

# Ubuntu
sudo apt install nfs-common -y

# Crear directorio y montar
sudo mkdir /mnt/efs
sudo mount -t nfs4 -o nfsvers=4.1 <efs-dns-name>:/ /mnt/efs

# Verificar
df -h | grep efs
```

> El DNS del EFS tiene el formato: `<fs-id>.efs.<region>.amazonaws.com`

#### Paso 5: Verificar acceso desde segunda instancia

```bash
# En instancia 1: crear un archivo
echo "hola desde instancia 1" | sudo tee /mnt/efs/test.txt

# En instancia 2: verificar que se ve el archivo
cat /mnt/efs/test.txt
```
