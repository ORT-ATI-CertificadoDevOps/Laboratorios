## Desplegando Instancias EC2

> **Tiempo estimado:** 30 minutos

### Objetivos

* Desplegar dos instancias EC2 en distintas zonas de disponibilidad
* Crear y usar un par de claves SSH
* Configurar un Security Group para acceso SSH
* Verificar conectividad entre instancias

### Tareas

Desplegar la primera instancia:
* Nombre: `test-instance01` *(usar Tag `Name`)*
* AMI: Amazon Linux
* Tipo: `t3.micro`
* AZ: `us-east-1a`
* Key pair: crear uno nuevo
* Security Group: permitir SSH (22)

Desplegar la segunda instancia con los mismos parámetros:
* Nombre: `test-instance02`
* AZ: `us-east-1b`
* Key pair y Security Group: reutilizar los del paso anterior

Conectarse vía SSH a ambas instancias para verificar el acceso.

### Para discutir en grupo

1. ¿Qué IP privada tiene cada instancia?
2. ¿Por qué son IPs de redes distintas si están en la misma cuenta?
3. Intentar hacer ping desde una instancia a la otra — ¿funciona? ¿por qué?

### Limpieza de recursos

* `EC2 > Instances` → seleccionar ambas instancias → `Instance State > Terminate instance`
* `EC2 > Key Pairs` → eliminar el par de claves creado
* `EC2 > Security Groups` → eliminar el SG creado *(esperar a que las instancias terminen primero)*

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/1-Solucion_desplegando_instancias_ec2.md).
