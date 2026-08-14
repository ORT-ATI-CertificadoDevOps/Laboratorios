# Lambda y Eventos — Triggers desde S3 y SQS

Lambda no solo responde a requests HTTP. Puede reaccionar a eventos de docenas de servicios AWS: un archivo subido a S3, un mensaje en una cola SQS, un registro nuevo en DynamoDB, un evento de CloudWatch Events. Este modelo event-driven elimina la necesidad de workers o cron jobs que polleen constantemente.

```
S3 (nuevo objeto)   →  Lambda (procesa el archivo)
SQS (nuevo mensaje) →  Lambda (procesa el mensaje)
DynamoDB stream     →  Lambda (replica / audita cambios)
EventBridge (cron)  →  Lambda (tarea programada)
```

## 3.1 Trigger desde S3 — Procesamiento de archivos

**Caso de uso:** cada vez que se sube un archivo CSV a un bucket S3, Lambda lo lee, lo transforma y guarda el resultado en otro bucket.

### Crear los buckets

```bash
aws s3 mb s3://mi-bucket-entrada-$(aws sts get-caller-identity --query Account --output text)
aws s3 mb s3://mi-bucket-salida-$(aws sts get-caller-identity --query Account --output text)
```

### Crear la función Lambda

```python
import boto3
import csv
import io
import json

s3 = boto3.client('s3')

def handler(event, context):
    # El evento S3 puede contener múltiples objetos (batch de uploads)
    for record in event['Records']:
        bucket_origen = record['s3']['bucket']['name']
        clave = record['s3']['object']['key']

        print(f'Procesando: s3://{bucket_origen}/{clave}')

        # Descargar el archivo desde S3
        response = s3.get_object(Bucket=bucket_origen, Key=clave)
        contenido = response['Body'].read().decode('utf-8')

        # Procesar el CSV
        reader = csv.DictReader(io.StringIO(contenido))
        registros = list(reader)

        # Subir el resultado como JSON al bucket de salida
        bucket_destino = bucket_origen.replace('entrada', 'salida')
        clave_destino = clave.replace('.csv', '.json')

        s3.put_object(
            Bucket=bucket_destino,
            Key=clave_destino,
            Body=json.dumps(registros, ensure_ascii=False),
            ContentType='application/json'
        )

        print(f'Resultado guardado en: s3://{bucket_destino}/{clave_destino}')

    return {'statusCode': 200}
```

### Configurar el trigger S3

En la consola Lambda:
1. Ir a **Configuration → Triggers → Add trigger**
2. Seleccionar **S3**
3. **Bucket:** seleccionar el bucket de entrada
4. **Event types:** `PUT` (se dispara cuando se sube un objeto)
5. **Suffix:** `.csv` (solo archivos CSV)
6. Hacer clic en **Add**

### Permisos necesarios

Lambda necesita permiso para leer del bucket de entrada y escribir en el de salida. Agregar al rol de ejecución:

```json
{
  "Effect": "Allow",
  "Action": ["s3:GetObject"],
  "Resource": "arn:aws:s3:::mi-bucket-entrada-*/*"
},
{
  "Effect": "Allow",
  "Action": ["s3:PutObject"],
  "Resource": "arn:aws:s3:::mi-bucket-salida-*/*"
}
```

### Probar el trigger

```bash
# Crear un CSV de prueba
cat > datos.csv << 'EOF'
nombre,edad,ciudad
Ana,28,Montevideo
Carlos,34,Buenos Aires
María,25,Santiago
EOF

# Subir al bucket de entrada (esto dispara Lambda)
aws s3 cp datos.csv s3://mi-bucket-entrada-ACCOUNT_ID/

# Verificar que se creó el JSON en el bucket de salida
aws s3 ls s3://mi-bucket-salida-ACCOUNT_ID/
aws s3 cp s3://mi-bucket-salida-ACCOUNT_ID/datos.json - | python3 -m json.tool
```

## 3.2 Trigger desde SQS — Procesamiento de mensajes

**Caso de uso:** una aplicación envía tareas a una cola SQS (envío de emails, procesamiento de pagos), y Lambda procesa cada mensaje de forma asíncrona.

### Crear la cola SQS

```bash
aws sqs create-queue --queue-name tareas-procesamiento
```

Guardar la URL que devuelve el comando.

### Crear la función Lambda

```python
import json

def handler(event, context):
    for record in event['Records']:
        # El body viene como string — parsear si es JSON
        mensaje = json.loads(record['body'])

        print(f'Procesando tarea: {mensaje}')

        # Lógica de procesamiento
        tipo = mensaje.get('tipo')
        if tipo == 'email':
            print(f"Enviando email a: {mensaje.get('destinatario')}")
        elif tipo == 'reporte':
            print(f"Generando reporte: {mensaje.get('nombre')}")
        else:
            print(f'Tipo de tarea desconocido: {tipo}')

    # Si la función retorna sin excepción, SQS elimina los mensajes del batch
    return {'batchItemFailures': []}
```

### Configurar el trigger SQS

En la consola Lambda:
1. **Configuration → Triggers → Add trigger**
2. Seleccionar **SQS**
3. **SQS queue:** seleccionar la cola creada
4. **Batch size:** `10` (Lambda recibe hasta 10 mensajes por invocación)
5. Hacer clic en **Add**

### Probar el trigger

```bash
# Enviar un mensaje a la cola
aws sqs send-message \
  --queue-url https://sqs.us-east-1.amazonaws.com/ACCOUNT_ID/tareas-procesamiento \
  --message-body '{"tipo": "email", "destinatario": "usuario@ejemplo.com"}'

# Ver los logs de Lambda en CloudWatch
aws logs tail /aws/lambda/procesador-sqs --follow
```

### Manejo de errores y Dead Letter Queue

Si Lambda lanza una excepción al procesar un mensaje, SQS reintenta la entrega hasta N veces (configurable). Para evitar que mensajes erróneos bloqueen la cola, configurar una **Dead Letter Queue (DLQ)**:

1. Crear una segunda cola: `tareas-procesamiento-dlq`
2. En la cola original, en **Dead-letter queue**, apuntar a la DLQ
3. Configurar **Maximum receives:** `3` (después de 3 fallos, el mensaje va a la DLQ)

Los mensajes en la DLQ pueden analizarse manualmente o procesarse con otra Lambda de retry.

## 3.3 Trigger programado con EventBridge

**Caso de uso:** ejecutar una tarea de mantenimiento todos los días a las 2am.

En la consola Lambda:
1. **Configuration → Triggers → Add trigger**
2. Seleccionar **EventBridge (CloudWatch Events)**
3. **Rule:** Create a new rule
4. **Rule name:** `tarea-diaria-2am`
5. **Schedule expression:** `cron(0 2 * * ? *)` — las 2am UTC todos los días
6. Hacer clic en **Add**

Expresiones de cron en EventBridge usan 6 campos: `segundos minutos horas día-del-mes mes día-de-la-semana año`. El `?` en día-del-mes o día-de-la-semana significa "cualquiera".

Ejemplos:
```
cron(0 8 * * ? *)      # 8am UTC todos los días
cron(0 12 ? * MON-FRI *) # 12pm UTC, lunes a viernes
rate(5 minutes)         # cada 5 minutos
rate(1 hour)            # cada hora
```

## 3.4 Estructura del evento por origen

| Origen | `event.Records[0]` contiene |
|--------|-----------------------------|
| S3 | `s3.bucket.name`, `s3.object.key`, `s3.object.size` |
| SQS | `body`, `messageId`, `attributes`, `receiptHandle` |
| SNS | `Sns.Message`, `Sns.Subject`, `Sns.TopicArn` |
| DynamoDB Streams | `dynamodb.NewImage`, `dynamodb.OldImage`, `eventName` |
| EventBridge | `detail`, `source`, `detail-type` |

Continuar con [04 - AWS SAM](../04-SAM/1-AWS-SAM.md)
