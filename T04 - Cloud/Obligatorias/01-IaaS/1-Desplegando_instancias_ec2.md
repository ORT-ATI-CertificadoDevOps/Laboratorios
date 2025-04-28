## Trabajando con Instancias EC2

### Parte 1: Desplegando dos instancias EC2

Bienvenidos al práctico de EC2. Lo que tendrán que hacer es lo siguiente:  

* Desplegar una instancia EC2 con las siguientes características:    
  * Nombre: test-instance01 (Hint: para agregarle un nombre, hay que usar un Tag -> "Name: nombre_instancia")
  * AMI: Amazon Linux
  * Type: t2.micro
  * AZ: us-east-1a
* Crear un par de claves SSH  
* Configurar un Security Group para permitir tráfico SSH
* Probar acceder a la instancia desplegada
* Repetir pasos para crear la instancia: 
  * Name: test-instance02
  * AMI: Amazon Linux
  * Type: t2.micro
  * AZ: us-east-1b

#### Discutir en el grupo

1. Qué IP privada tiene cada instancia?
2. Por qué son ips de redes distintas
3. Probar ping desde una instancia a la otra
4. Por qué no funciona? Y si funciona...por qué les parece que funciona?
5. Compartir conclusiones obtenidas


#### Spoiler alert!!!

En caso de dudas, se puede consultar la [Solución](./soluciones/1-Solucion_desplegando_instancias_ec2.md)