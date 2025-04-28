## Práctico de Storage: EBS

### Objetivos

#### Parte A

* Crear una instancia con Ubuntu ó Amazon Linux
* Crear un volumen EBS y presentarlo a la instancia
* Usando LVM sobre el disco nuevo:
  * Crear el PV
  * Crear un VG
  * Crear un LV asociado al VG
* Darle formato y montarlo en `/mnt`
* Crear contenido (Cualquier cosa)
* Bajo la sección de `Snapshots`, seleccionar uno y crear una `AMI` a partir del snapshot

#### Parte B

* Desmontar el disco de la instancia 
* Desde la consola, en la sección ***"Volumes"*** hacerle un `detach` del disco a la instancia
* Crear una nueva instancia (Ubuntu o Amz. Linux)
* Hacer un `attach` del disco a la nueva instancia
* Observar comportamiento
 
#### Referencias para trabajar con LVM

* Para inicializar un disco como Physical Volume: `pvcreate /dev/xxx`
* Para crear un VG: `vgcreate vg_name /dev/xxx`
* Para crear un LV: `lvcreate -n vl_name -l+100%FREE vg_name`
* Para darle formato: `mkfs.{ext4,xfs} /dev/mapper/vg_name-vl_name`
* Para montarlo: `mount -t {xfs,ext4} /dev/mapper/vg_name-vl_name /mnt`



