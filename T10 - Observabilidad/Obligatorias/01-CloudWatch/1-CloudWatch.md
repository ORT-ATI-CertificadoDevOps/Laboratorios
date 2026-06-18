# CloudWatch — Métricas, Logs y Alarmas

CloudWatch es el servicio de observabilidad nativo de AWS. Todos los servicios de AWS publican métricas en CloudWatch automáticamente: CPU de EC2, errores de Lambda, latencia de ALB, lecturas de RDS. También recibe logs de aplicaciones y permite configurar alarmas que disparan acciones cuando una métrica cruza un umbral.

## 1.1 Métricas

### Métricas de servicio (automáticas)

En la consola CloudWatch, ir a **Metrics → All metrics**. Explorar los namespaces:

| Namespace | Servicio | Métricas clave |
|-----------|----------|----------------|
| `AWS/EC2` | Instancias EC2 | `CPUUtilization`, `NetworkIn`, `NetworkOut`, `StatusCheckFailed` |
| `AWS/ApplicationELB` | ALB | `RequestCount`, `HTTPCode_Target_5XX_Count`, `TargetResponseTime` |
| `AWS/Lambda` | Lambda | `Invocations`, `Errors`, `Duration`, `Throttles` |
| `AWS/RDS` | RDS | `CPUUtilization`, `DatabaseConnections`, `ReadLatency`, `FreeStorageSpace` |
| `AWS/ECS` | ECS | `CPUUtilization`, `MemoryUtilization` |

### Métricas personalizadas

Las aplicaciones pueden publicar sus propias métricas con el SDK de AWS:

```python
import boto3

cloudwatch = boto3.client('cloudwatch', region_name='us-east-1')

def registrar_metrica(nombre, valor, unidad='Count', dimensiones=None):
    cloudwatch.put_metric_data(
        Namespace='MiApp/Negocio',
        MetricData=[{
            'MetricName': nombre,
            'Value': valor,
            'Unit': unidad,
            'Dimensions': dimensiones or []
        }]
    )

# Publicar métricas de negocio
registrar_metrica('PedidosCreados', 1)
registrar_metrica('TiempoProcesamientoPago', 234, 'Milliseconds',
                  [{'Name': 'Ambiente', 'Value': 'prod'}])
```

**Desde la CLI:**
```bash
aws cloudwatch put-metric-data \
  --namespace "MiApp/Negocio" \
  --metric-name "PedidosCreados" \
  --value 1 \
  --unit Count
```

### Crear un dashboard

1. En CloudWatch, ir a **Dashboards → Create dashboard**
2. Nombre: `mi-app-produccion`
3. Agregar widgets:
   - **Line chart:** `AWS/EC2 → CPUUtilization` de las instancias del entorno
   - **Number:** `AWS/Lambda → Errors` (últimas 1h)
   - **Line chart:** `AWS/ApplicationELB → TargetResponseTime` del ALB
4. Guardar el dashboard

Configurar el período de auto-refresh (ícono de reloj en la esquina superior) a 1 minuto para monitoreo en tiempo real.

## 1.2 Logs

### Ver logs de una función Lambda

Los logs de Lambda se almacenan automáticamente en el grupo `/aws/lambda/<nombre-funcion>`.

**Desde la CLI:**
```bash
# Ver los últimos 5 minutos de logs
aws logs tail /aws/lambda/mi-funcion --since 5m

# Seguir logs en tiempo real
aws logs tail /aws/lambda/mi-funcion --follow

# Filtrar por patrón
aws logs filter-log-events \
  --log-group-name /aws/lambda/mi-funcion \
  --filter-pattern "ERROR"
```

### Enviar logs de aplicación a CloudWatch

Para aplicaciones corriendo en EC2, instalar el **CloudWatch Logs Agent**:

```bash
# Instalar el agente en Amazon Linux 2
sudo yum install -y amazon-cloudwatch-agent

# Configurar el agente
cat > /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json << 'EOF'
{
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/var/log/mi-app/app.log",
            "log_group_name": "/mi-app/produccion/app",
            "log_stream_name": "{instance_id}",
            "timestamp_format": "%Y-%m-%d %H:%M:%S"
          }
        ]
      }
    }
  }
}
EOF

sudo systemctl start amazon-cloudwatch-agent
sudo systemctl enable amazon-cloudwatch-agent
```

### CloudWatch Logs Insights — Consultas

Logs Insights permite consultar logs con un lenguaje tipo SQL:

En **CloudWatch → Logs Insights**, seleccionar el log group y ejecutar:

```sql
-- Contar errores por tipo en las últimas 24h
fields @timestamp, @message
| filter @message like /ERROR/
| stats count() by bin(1h)
| sort @timestamp desc

-- Latencia promedio de requests HTTP
fields @timestamp, responseTime
| filter ispresent(responseTime)
| stats avg(responseTime), max(responseTime), p99(responseTime) by bin(5m)

-- Top 10 IPs con más requests
fields remoteIP
| stats count() as requestCount by remoteIP
| sort requestCount desc
| limit 10
```

## 1.3 Alarmas

### Crear una alarma básica

**CPU de EC2 sobre 80%:**

```bash
aws cloudwatch put-metric-alarm \
  --alarm-name "EC2-CPU-Alto" \
  --alarm-description "CPU de instancia EC2 supera el 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/EC2 \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --dimensions Name=InstanceId,Value=i-1234567890abcdef0 \
  --alarm-actions arn:aws:sns:us-east-1:123456789:mi-topic-alertas
```

**Parámetros clave:**
- `--period 300`: evalúa en ventanas de 5 minutos
- `--evaluation-periods 2`: la alarma se activa cuando 2 períodos consecutivos superan el umbral
- `--alarm-actions`: ARN de un topic SNS para enviar notificaciones

### Crear un topic SNS para recibir alertas por email

```bash
# Crear el topic
aws sns create-topic --name alertas-produccion

# Suscribir un email
aws sns subscribe \
  --topic-arn arn:aws:sns:us-east-1:ACCOUNT_ID:alertas-produccion \
  --protocol email \
  --notification-endpoint tu@email.com
```

Confirmar la suscripción desde el email recibido.

### Alarmas compuestas

Una alarma compuesta combina múltiples alarmas con lógica booleana. Útil para evitar falsos positivos:

```bash
# Alarma solo cuando CPU > 80% Y hay errores Lambda
aws cloudwatch put-composite-alarm \
  --alarm-name "Alerta-Servicio-Degradado" \
  --alarm-rule "ALARM(\"EC2-CPU-Alto\") AND ALARM(\"Lambda-Errores-Alto\")"
```

## 1.4 Resumen

| Funcionalidad | Cómo acceder |
|--------------|--------------|
| Métricas de AWS | CloudWatch → Metrics → All metrics |
| Métricas custom | SDK `put_metric_data` o CLI |
| Dashboards | CloudWatch → Dashboards |
| Logs de Lambda | `/aws/lambda/<nombre>` |
| Logs de EC2 | CloudWatch Logs Agent → grupo configurado |
| Consultas de logs | CloudWatch → Logs Insights |
| Alarmas | CLI `put-metric-alarm` o consola |
| Notificaciones | SNS topic → email / SMS / Lambda |

Continuar con [02 - Prometheus y Grafana](../02-Prometheus-Grafana/1-Prometheus-Grafana.md)
