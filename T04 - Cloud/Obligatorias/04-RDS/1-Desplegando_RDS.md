## T/P: Desplegando AWS RDS

### Objetivos

Desplegar el servicio managed de base de datos relacionales de AWS. 


* Desplegar una instancia de RDS
  * Seleccionar `Standard Create`
  * DB Engine: `MySQL` (Cualquier versión)
  * Template: `Free Tier`
  * Sin `MultiAZ`
  * Nombre: my-dbinstance
  * Dejar `admin` como db user
  * Crear una password
  * Bajo connectivity, dejar seleccionado `Don't connect to an EC2 compute resource`
  * Seleccionar el VPC donde asociaremos la db instance
  * `Public access: No`
  * Crear un SG que permita el `tcp 3306`
  * Seleccionar una AZ
  * Dejar el resto de las configuraciones como están
* Sino se cuenta con una instancia de EC2, crear una. La usaremos para consumir la Base de Datos
* Conectar la instancia de EC2 con la instancia de RDS, para esto deben modificar el SG de la base.

```bash
#Conectarse a una instancia de MySQL desde Amazon Linux
$ sudo yum install mysql
$ mysql -h <endpoint_url> -u admin -p
$ show databases; #para listar las bases creadas
$ create database db-test;
$ exit
```
```bash
#Conectarse a una instancia de MySQL desde Ubuntu / Debian
$ sudo apt install mysql-client 
$ mysql -h <endpoint_url> -u admin -p
$ show databases; #para listar las bases creadas
$ create database db-test;
$ exit
```

```bash
#Vamos a importar una base nueva
$ curl -O https://gist.githubusercontent.com/mauricioamendola/d72a811b62129a4af16d6623ae32ed54/raw/d1cfd290385b52c4142a67bd230e793dcb258a5f/simple-mysql-dump
$ mysql -h <endpoint_url> -uadmin -p db-test < simple-mysql-dump # Con esto copiamos la estructura del archivo SQL a la base db-test
```