## AWS RDS: Base de Datos Gestionada

> **Tiempo estimado:** 35 minutos

### Prerequisitos

* Una instancia EC2 en la misma VPC que la instancia RDS *(para conectarse a la base de datos)*

### Objetivos

Desplegar el servicio managed de base de datos relacionales de AWS y conectarse desde una instancia EC2.

### Parte 1 — Crear la instancia RDS

* Ir a `RDS > Databases > Create database`
  * Method: `Standard Create`
  * DB Engine: `MySQL` (cualquier versión)
  * Template: `Free Tier`
  * Sin `Multi-AZ`
  * DB instance identifier: `my-dbinstance`
  * Master username: `admin`
  * Crear una password segura
* **Connectivity:**
  * `Don't connect to an EC2 compute resource`
  * Seleccionar la VPC deseada
  * `Public access: No`
  * Crear un Security Group nuevo que permita **TCP 3306**
  * Seleccionar una AZ
* Dejar el resto con los valores por defecto

> La instancia tarda ~5 minutos en pasar a estado `Available`.

### Parte 2 — Conectar desde EC2

Si no hay una instancia EC2 disponible, crear una en la misma VPC. Luego modificar el SG de la instancia RDS para permitir TCP 3306 **desde el SG de la instancia EC2**.

```bash
# Instalar cliente MySQL en Amazon Linux
sudo dnf install -y mariadb105

# Instalar cliente MySQL en Ubuntu / Debian
sudo apt install -y mysql-client

# Conectarse a la instancia RDS
mysql -h <endpoint_url> -u admin -p

# Dentro de MySQL
show databases;
create database dbtest;
use dbtest;
exit
```

```bash
# Importar una base de datos de ejemplo
curl -O https://gist.githubusercontent.com/mauricioamendola/d72a811b62129a4af16d6623ae32ed54/raw/d1cfd290385b52c4142a67bd230e793dcb258a5f/simple-mysql-dump

mysql -h <endpoint_url> -u admin -p dbtest < simple-mysql-dump
```

### Parte 3 — Gestionando RDS desde la CLI

Explorar la instancia RDS usando la AWS CLI desde la instancia EC2.

```bash
# Describir la instancia RDS
aws rds describe-db-instances \
  --db-instance-identifier my-dbinstance \
  --query "DBInstances[0].{Endpoint:Endpoint.Address,Status:DBInstanceStatus,Engine:Engine}"

# Listar snapshots disponibles
aws rds describe-db-snapshots \
  --db-instance-identifier my-dbinstance

# Crear un snapshot manual
aws rds create-db-snapshot \
  --db-instance-identifier my-dbinstance \
  --db-snapshot-identifier my-dbinstance-snapshot-manual

# Verificar el estado del snapshot (esperar Status: "available")
aws rds describe-db-snapshots \
  --db-snapshot-identifier my-dbinstance-snapshot-manual \
  --query "DBSnapshots[0].{ID:DBSnapshotIdentifier,Status:Status,Size:AllocatedStorage}"
```

> Para investigar: ¿Qué diferencia hay entre un snapshot manual y uno automático? ¿Cuándo se elimina cada uno?

### Limpieza de recursos

> **Importante:** Las instancias RDS generan costo por hora aunque no se usen. Eliminar al terminar el práctico.

* `RDS > Databases` → seleccionar `my-dbinstance` → `Actions > Delete`
  * Desmarcar *Create final snapshot*
  * Confirmar con `delete me`
* `EC2 > Instances` → terminar la instancia EC2
* `EC2 > Security Groups` → eliminar el SG creado para el puerto 3306
