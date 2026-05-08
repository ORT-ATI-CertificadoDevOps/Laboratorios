# Lambda — Primera función

AWS Lambda ejecuta código sin servidores. El modelo de ejecución es simple: se sube el código (o se escribe inline), se define el trigger que lo invoca, y Lambda se encarga del resto — aprovisionamiento, escalado, disponibilidad.

```
Evento → Lambda → respuesta / efecto secundario
```

Lambda cobra por número de invocaciones y por duración (en GB-segundos). El tier gratuito incluye 1 millón de invocaciones y 400.000 GB-segundos por mes — suficiente para labs y proyectos pequeños sin costo.

## 1.1 Runtimes disponibles

Lambda soporta los siguientes runtimes de forma nativa:

| Runtime | Versiones |
|---------|-----------|
| Node.js | 18.x, 20.x |
| Python | 3.11, 3.12 |
| Java | 11, 17, 21 |
| .NET | 6, 8 |
| Go | 1.x (custom runtime) |
| Ruby | 3.2 |

También es posible usar **container images** como runtime, lo que permite cualquier lenguaje y dependencias.

## 1.2 Crear una función Lambda desde la consola

1. Ir a **AWS Lambda** en la consola
2. Hacer clic en **Create function**
3. Seleccionar **Author from scratch**
4. Configurar:
   - **Function name:** `hola-mundo`
   - **Runtime:** `Node.js 20.x`
   - **Architecture:** `x86_64`
5. En **Permissions**, dejar el rol por defecto (Lambda crea un rol con permisos básicos de CloudWatch Logs)
6. Hacer clic en **Create function**

## 1.3 Escribir el handler

El handler es la función que Lambda invoca. Su firma depende del runtime:

**Node.js:**
```javascript
export const handler = async (event) => {
  console.log('Evento recibido:', JSON.stringify(event, null, 2));

  return {
    statusCode: 200,
    body: JSON.stringify({
      mensaje: 'Hola desde Lambda',
      timestamp: new Date().toISOString(),
    }),
  };
};
```

**Python:**
```python
import json
import datetime

def handler(event, context):
    print('Evento recibido:', json.dumps(event))
    return {
        'statusCode': 200,
        'body': json.dumps({
            'mensaje': 'Hola desde Lambda',
            'timestamp': datetime.datetime.now().isoformat(),
        })
    }
```

En la consola, pegar el código en el editor inline y hacer clic en **Deploy**.

## 1.4 El objeto `event`

El parámetro `event` contiene el input de la invocación. Su estructura depende del origen:

- Invocación directa (test manual): el JSON que se ingresa en el test
- API Gateway: incluye `httpMethod`, `path`, `headers`, `body`, `queryStringParameters`
- S3: información del bucket y el objeto que disparó el evento
- SQS: lista de mensajes con `body`, `messageId`, `attributes`

## 1.5 Invocar la función (test manual)

En la consola, ir a la pestaña **Test**:

1. Hacer clic en **Create new event**
2. Nombre del evento: `test-basico`
3. Contenido:
```json
{
  "nombre": "estudiante",
  "curso": "DevOps"
}
```
4. Hacer clic en **Test**

El resultado muestra el `Response` de la función y los logs de CloudWatch con el output del `console.log`.

## 1.6 Invocar con AWS CLI

```bash
# Invocar la función y ver la respuesta
aws lambda invoke \
  --function-name hola-mundo \
  --payload '{"nombre": "estudiante"}' \
  --cli-binary-format raw-in-base64-out \
  output.json

cat output.json
```

## 1.7 Configuración importante

**Timeout:** por defecto 3 segundos. Máximo 15 minutos. Configurar según lo que hace la función — una query a RDS puede necesitar 10–30 segundos.

**Memory:** entre 128 MB y 10 GB. La CPU se escala proporcionalmente a la memoria — aumentar memoria también aumenta CPU y puede hacer la función más rápida aunque no use más RAM.

**Environment variables:** en la pestaña **Configuration → Environment variables**. Usar para configuración que cambia entre entornos (URLs, nombres de recursos). Para secretos, usar Secrets Manager (ver T06).

## 1.8 Ver logs en CloudWatch

Lambda escribe logs automáticamente en CloudWatch Logs en el grupo `/aws/lambda/<nombre-funcion>`.

Desde la consola Lambda:
1. Ir a **Monitor → View CloudWatch logs**
2. Seleccionar el log stream más reciente (corresponde a una invocación)
3. Ver el output del `console.log` / `print`

Desde la CLI:
```bash
aws logs tail /aws/lambda/hola-mundo --follow
```

El flag `--follow` hace streaming de los logs en tiempo real mientras la función es invocada.

## 1.9 Límites relevantes

| Límite | Valor |
|--------|-------|
| Timeout máximo | 15 minutos |
| Memoria máxima | 10 GB |
| Tamaño del deployment package (zip) | 50 MB (comprimido), 250 MB (descomprimido) |
| Tamaño del payload de invocación | 6 MB (síncrono), 256 KB (asíncrono) |
| Concurrencia por defecto | 1.000 ejecuciones simultáneas por región |

Continuar con [02 - API REST serverless con API Gateway](../02-Lambda-y-API-Gateway/1-API-REST-Serverless.md)
