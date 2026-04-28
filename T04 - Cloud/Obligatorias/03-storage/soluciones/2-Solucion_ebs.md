### Solución: EBS

#### Parte A: Crear y montar el volumen con LVM

**1. Crear el volumen EBS**

`EC2 > Volumes > Create volume`
* Type: `gp3`
* Size: `10 GiB`
* AZ: misma AZ que la instancia (ej. `us-east-1a`)

**2. Attach a la instancia**

`EC2 > Volumes` → seleccionar el volumen → `Actions > Attach volume`
* Instance: seleccionar la instancia
* Device: `/dev/sdf` (aparecerá como `/dev/xvdf` o `/dev/nvme1n1` dentro de la instancia)

**3. Configurar LVM dentro de la instancia**

```bash
# Verificar que el disco es visible
lsblk

# Inicializar como Physical Volume
sudo pvcreate /dev/xvdf

# Crear Volume Group
sudo vgcreate vg_datos /dev/xvdf

# Crear Logical Volume usando todo el espacio
sudo lvcreate -n lv_datos -l+100%FREE vg_datos

# Dar formato ext4
sudo mkfs.ext4 /dev/mapper/vg_datos-lv_datos

# Montar
sudo mount -t ext4 /dev/mapper/vg_datos-lv_datos /mnt

# Verificar
df -h /mnt
```

**4. Crear contenido y snapshot**

```bash
echo "datos de prueba" | sudo tee /mnt/test.txt
```

`EC2 > Volumes` → seleccionar volumen → `Actions > Create snapshot`

#### Parte B: Migrar el volumen a otra instancia

```bash
# Desmontar primero
sudo umount /mnt
```

`EC2 > Volumes` → `Actions > Detach volume`

Luego en la nueva instancia:

`EC2 > Volumes` → `Actions > Attach volume` → seleccionar nueva instancia

```bash
# El volumen ya tiene LVM y datos — solo montar
sudo mount -t ext4 /dev/mapper/vg_datos-lv_datos /mnt
cat /mnt/test.txt
```

> Si LVM no activa el VG automáticamente: `sudo vgchange -ay vg_datos`
