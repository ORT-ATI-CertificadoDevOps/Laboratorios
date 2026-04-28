## EFS: Almacenamiento Compartido

> **Tiempo estimado:** 25 minutos

### Objetivos

* Crear un File System EFS desde la consola
* Lanzar dos instancias EC2 en la misma VPC
* Montar el share en ambas y verificar que comparten el contenido

### Requisitos previos

* El EFS y las instancias deben estar en la **misma VPC**
* El Security Group del EFS debe permitir **TCP 2049 (NFS)** desde las instancias EC2
* Instalar soporte NFS antes de montar:

```bash
# Ubuntu / Debian
sudo apt install -y nfs-common

# Amazon Linux
sudo yum install -y nfs-utils
```

### Pasos

**1. Crear el File System EFS** (`EFS > Create file system`)
  * Seleccionar la VPC correcta
  * En la sección *Network*, asignar un SG que permita TCP 2049

**2. Lanzar dos instancias EC2** en esa misma VPC

**3. Montar el EFS en ambas instancias**

```bash
sudo mkdir /mnt/efs
sudo mount -t nfs4 -o nfsvers=4.1 <efs-dns-name>:/ /mnt/efs
```

El DNS del EFS tiene la forma: `fs-xxxxxxxx.efs.us-east-1.amazonaws.com` (visible en la consola EFS bajo *Attach*)

**4. Verificar acceso compartido**

Desde instancia 1:
```bash
echo "hola desde instancia 1" | sudo tee /mnt/efs/test.txt
```

Desde instancia 2:
```bash
cat /mnt/efs/test.txt
```

### Limpieza de recursos

* `EFS > File Systems` → eliminar el File System *(esperar a que no haya mount targets activos)*
* `EC2 > Instances` → terminar las instancias

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/1-Solucion_efs.md).
