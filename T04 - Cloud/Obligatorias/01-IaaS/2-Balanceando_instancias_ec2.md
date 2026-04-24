## Balanceando Instancias con ALB

> **Tiempo estimado:** 40 minutos

### Contexto

Se pueden reutilizar las instancias del lab anterior si no fueron eliminadas. En este lab se agrega un Application Load Balancer (ALB) para distribuir el tráfico HTTP entre instancias.

> El Classic Load Balancer (ELB Classic) está en estado *legacy* desde 2022. AWS recomienda usar **ALB** para tráfico HTTP/HTTPS y **NLB** para TCP/UDP de baja latencia.

### Objetivos

* Desplegar instancias EC2 con un web server configurado via user-data
* Crear un Application Load Balancer con un Target Group
* Verificar la distribución de tráfico y el comportamiento ante fallos

### Tareas

Desplegar dos instancias EC2. En **Advanced details → User data**, colocar el siguiente script:

```bash
#!/bin/bash
yum install -y httpd
systemctl start httpd
systemctl enable httpd
TOKEN=$(curl -s -X PUT "http://169.254.169.254/latest/api/token" \
  -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")
INSTANCE_ID=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/meta-data/instance-id)
HOSTNAME=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/meta-data/local-hostname)
cat > /var/www/html/index.html <<EOF
<html>
<body style="font-family: Arial; text-align: center; padding: 50px;">
  <h1>EC2 Web Server</h1>
  <p><strong>Instance ID:</strong> $INSTANCE_ID</p>
  <p><strong>Hostname:</strong> $HOSTNAME</p>
</body>
</html>
EOF
```

> Cada instancia mostrará su propio `Instance ID` y `Hostname`. Al probar el ALB, refrescar varias veces para ver cómo alterna entre instancias.

Crear un **Application Load Balancer (ALB)**:
* Crear un `Target Group` con ambas instancias
* Crear el `ALB` apuntando al `Target Group`

### Para discutir en grupo

* ¿Se puede acceder a la aplicación directamente? ¿Qué falta configurar?
* Detener una de las instancias — ¿cómo responde el ALB?

### Limpieza de recursos

* `EC2 > Load Balancers` → eliminar el ALB
* `EC2 > Target Groups` → eliminar el Target Group
* `EC2 > Instances` → terminar las instancias

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/2-Solucion_balanceando_instancias_ec2.md).
