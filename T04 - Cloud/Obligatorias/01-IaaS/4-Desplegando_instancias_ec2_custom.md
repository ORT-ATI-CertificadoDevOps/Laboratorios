## Instancias EC2 Customizadas y AMIs

> **Tiempo estimado:** 25 minutos

### Contexto

`cloud-init` es el servicio que ejecuta scripts al arrancar una instancia EC2. El bloque `user-data` permite automatizar la configuración inicial: instalar paquetes, desplegar aplicaciones, configurar servicios. Una vez configurada la instancia, se puede crear una **AMI** (Amazon Machine Image) para reutilizarla como base en futuros despliegues, sin necesidad de repetir la configuración.

### Objetivos

* Desplegar una instancia EC2 configurada automáticamente via user-data
* Crear una AMI custom a partir de la instancia configurada

### Parte A — Despliegue con user-data

Desplegar una instancia de EC2:
* Nombre: `html-web-instance01`
* Tipo: `t2.micro`
* AZ: `us-east-1a`
* Security Group: permitir HTTP (80)
* En **User data**, colocar el siguiente script:

```bash
#!/bin/bash
sudo yum update -y
sudo yum install httpd -y
sudo systemctl enable httpd
curl -O https://gist.githubusercontent.com/mauricioamendola/9113b526ecb157724187fabddaa95aa8/raw/f424d19c98745843f2abcddd2a7de296ada93880/index.html
sudo mv index.html /var/www/html
sudo systemctl start httpd
```

Verificar que la aplicación carga accediendo a la IP pública de la instancia desde el browser.

### Parte B — Crear una AMI custom

Con la instancia en estado `Running`:
* `EC2 > Instances` → seleccionar la instancia → `Actions > Image and templates > Create image`
* Nombre: `html-web-ami`

Esta AMI puede usarse en labs posteriores (Launch Templates, ASG) para desplegar instancias ya configuradas sin ejecutar el user-data nuevamente.

### Limpieza de recursos

* `EC2 > Instances` → terminar `html-web-instance01`
* `EC2 > AMIs` → deregister la AMI *(opcional — no genera costo si no se lanza)*

### Spoiler Alert

En caso de trancarse, se puede consultar la [solución](./soluciones/4-Solucion_desplegando_instancias_custom.md).
