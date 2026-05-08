# Tracing Distribuido con AWS X-Ray

Las métricas dicen "hay un problema de latencia". Los logs dicen "estas son las líneas que se ejecutaron". Las **trazas** dicen "esta request tardó 2.3 segundos: 1.8s esperando a RDS, 0.3s en el handler de Lambda, 0.2s en una llamada a S3".

**AWS X-Ray** instrumenta las aplicaciones para registrar cada operación relevante — llamadas a bases de datos, servicios externos, subtareas — y los agrupa en una traza que muestra el flujo completo de una request, incluyendo cuánto tardó cada parte.

```
Request → [API Gateway] → [Lambda A] → [RDS]
                                     → [Lambda B] → [S3]
                       → respuesta

Traza: 450ms total
  ├─ API Gateway: 5ms
  ├─ Lambda A: 400ms
  │   ├─ RDS query: 350ms  ← cuello de botella
  │   └─ Lambda B: 40ms
  │       └─ S3 GetObject: 35ms
  └─ overhead: 5ms
```

## 3.1 Conceptos

| Concepto | Descripción |
|----------|-------------|
| **Trace** | El recorrido completo de una request a través del sistema |
| **Segment** | Una unidad de trabajo dentro del trace (ej: una Lambda) |
| **Subsegment** | Una operación dentro de un segment (ej: una query SQL) |
| **Trace ID** | Identificador único que viaja en los headers HTTP para conectar todos los segments |

## 3.2 Habilitar X-Ray en Lambda

La forma más simple de habilitar X-Ray en Lambda es desde la consola o el template SAM:

**Consola:**
1. Abrir la función Lambda
2. Ir a **Configuration → Monitoring and operations tools**
3. En **AWS X-Ray**, activar **Active tracing**

**SAM template:**
```yaml
Globals:
  Function:
    Tracing: Active
```

Con esto, Lambda automáticamente registra segmentos para cada invocación en X-Ray — sin cambiar el código.

## 3.3 Instrumentar el código (subsegmentos)

Para ver el detalle de qué hace la función internamente, agregar subsegmentos manualmente:

**Node.js:**
```javascript
const AWSXRay = require('aws-xray-sdk-core');
const AWS = AWSXRay.captureAWS(require('aws-sdk'));

// AWSXRay.captureAWS() intercepta todas las llamadas al SDK de AWS
// y las registra automáticamente como subsegmentos

const s3 = new AWS.S3();
const dynamodb = new AWS.DynamoDB.DocumentClient();

export const handler = async (event) => {
  // Subsegmento manual para lógica de negocio
  const segment = AWSXRay.getSegment();
  const subsegment = segment.addNewSubsegment('validar-input');

  try {
    // lógica de validación
    const input = JSON.parse(event.body);
    if (!input.id) throw new Error('id requerido');
    subsegment.addAnnotation('userId', input.id);
    subsegment.close();
  } catch (err) {
    subsegment.addError(err);
    subsegment.close();
    throw err;
  }

  // Esta llamada a DynamoDB aparece automáticamente en la traza
  const item = await dynamodb.get({
    TableName: 'mi-tabla',
    Key: { id: event.pathParameters.id }
  }).promise();

  return { statusCode: 200, body: JSON.stringify(item.Item) };
};
```

**Python:**
```python
from aws_xray_sdk.core import xray_recorder
from aws_xray_sdk.core import patch_all

patch_all()  # intercepta boto3, requests, sqlalchemy, etc.

def handler(event, context):
    with xray_recorder.in_subsegment('procesar-datos') as subsegment:
        subsegment.put_annotation('ambiente', 'prod')
        subsegment.put_metadata('input', event)
        # lógica de procesamiento
        resultado = procesar(event)

    return {'statusCode': 200, 'body': resultado}
```

## 3.4 Propagar el trace entre servicios

Cuando Lambda A llama a Lambda B via SDK, X-Ray propaga el trace ID automáticamente si se usa el SDK instrumentado. Para llamadas HTTP directas (a otros servicios o APIs externas), propagar el header manualmente:

```javascript
const AWSXRay = require('aws-xray-sdk-core');
const https = AWSXRay.captureHTTPs(require('https'));

// Usar https en vez del módulo nativo — X-Ray inyecta el trace header
https.get('https://api.externa.com/data', (res) => {
  // ...
});
```

## 3.5 Ver las trazas en la consola

En **AWS X-Ray → Traces**:

1. Seleccionar el rango de tiempo
2. Ver la lista de trazas ordenadas por duración (las más lentas primero)
3. Hacer clic en una traza para ver el **Service Map** y el **Timeline**

El **Service Map** muestra un grafo de los servicios involucrados con el porcentaje de errores y la latencia promedio en los bordes.

El **Timeline** muestra la línea de tiempo de segments y subsegmentos:
```
Lambda mi-funcion [450ms]
  ├─ Initialization [80ms]      ← cold start
  ├─ validar-input [2ms]
  ├─ DynamoDB GetItem [320ms]   ← cuello de botella
  └─ S3 GetObject [45ms]
```

## 3.6 X-Ray en API Gateway

Para trazar el request desde API Gateway (no solo desde Lambda):

1. En la API HTTP de API Gateway, ir a **Stages → $default**
2. En **Logging**, activar **X-Ray Tracing**

Ahora el trace empieza en API Gateway e incluye el tiempo de enrutamiento antes de invocar Lambda.

## 3.7 X-Ray Insights — Detección de anomalías

X-Ray Insights analiza las trazas automáticamente y detecta anomalías: un aumento repentino en la tasa de errores, latencia fuera de lo normal en un subsegmento específico, o patrones que indican un problema emergente.

En **X-Ray → X-Ray Insights**, ver los insights activos. Cada insight muestra el período del problema, el servicio afectado y el impacto estimado.

## 3.8 Resumen

| Acción | Cómo |
|--------|------|
| Activar tracing en Lambda | `Tracing: Active` en SAM, o consola |
| Instrumentar SDK de AWS | `AWSXRay.captureAWS(require('aws-sdk'))` |
| Subsegmento manual | `segment.addNewSubsegment('nombre')` |
| Ver trazas | X-Ray → Traces |
| Service Map | X-Ray → Service map |
| Anomalías automáticas | X-Ray → X-Ray Insights |

Continuar con [04 - Alerting con Alertmanager](../04-Alerting/1-Alerting.md)
