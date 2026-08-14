# API REST Serverless con Lambda y API Gateway

Lambda por sí sola no expone un endpoint HTTP. **API Gateway** actúa como el front-door: recibe requests HTTP del cliente, los transforma en eventos Lambda, espera la respuesta y la devuelve al cliente.

```
Cliente → [API Gateway] → [Lambda] → [lógica de negocio]
       ←                ←          ← respuesta HTTP
```

El resultado es una API REST completamente serverless: sin servidores, sin load balancers, escalado automático hasta millones de requests por segundo.

## 2.1 Tipos de API en API Gateway

| Tipo | Caso de uso | Protocolo |
|------|-------------|-----------|
| **HTTP API** | APIs REST simples, baja latencia, menor costo | HTTP/1.1, HTTP/2 |
| **REST API** | APIs con autenticación avanzada, throttling, caching | HTTP/1.1 |
| **WebSocket API** | Conexiones bidireccionales (chat, notificaciones real-time) | WebSocket |

Para este lab usar **HTTP API** — es más simple, más barata (~70% menos que REST API) y suficiente para la mayoría de los casos.

## 2.2 Crear la API desde la consola

1. Ir a **API Gateway** en la consola AWS
2. Hacer clic en **Create API**
3. Seleccionar **HTTP API → Build**
4. En **Integrations**, agregar:
   - **Integration type:** Lambda
   - **Lambda function:** seleccionar `hola-mundo` (creada en el lab anterior)
5. Configurar la ruta:
   - **Method:** `GET`
   - **Resource path:** `/saludo`
6. En **Stage**, dejar `$default` con auto-deploy activado
7. Hacer clic en **Create**

API Gateway provee una URL del tipo:
```
https://abc123xyz.execute-api.us-east-1.amazonaws.com/saludo
```

Probar con curl:
```bash
curl https://abc123xyz.execute-api.us-east-1.amazonaws.com/saludo
```

## 2.3 Leer parámetros del request en Lambda

Cuando API Gateway invoca Lambda, el evento incluye toda la información del request HTTP:

```javascript
export const handler = async (event) => {
  // Query string: /saludo?nombre=victor
  const nombre = event.queryStringParameters?.nombre ?? 'mundo';

  // Path parameters: /usuarios/{id}
  const userId = event.pathParameters?.id;

  // Headers
  const contentType = event.headers?.['content-type'];

  // Body (en POST/PUT, viene como string — hay que parsear)
  const body = event.body ? JSON.parse(event.body) : null;

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ mensaje: `Hola, ${nombre}!` }),
  };
};
```

Probar con query string:
```bash
curl "https://abc123xyz.execute-api.us-east-1.amazonaws.com/saludo?nombre=victor"
# → {"mensaje":"Hola, victor!"}
```

## 2.4 Crear una API CRUD completa

Extender la API con múltiples rutas y métodos. Primero, crear una Lambda para cada operación o una sola Lambda que use el `event.routeKey` para distinguir:

```javascript
export const handler = async (event) => {
  const { routeKey, pathParameters, body } = event;

  switch (routeKey) {
    case 'GET /items':
      return { statusCode: 200, body: JSON.stringify({ items: [] }) };

    case 'GET /items/{id}':
      return {
        statusCode: 200,
        body: JSON.stringify({ id: pathParameters.id, nombre: 'Ejemplo' }),
      };

    case 'POST /items':
      const data = JSON.parse(body);
      return { statusCode: 201, body: JSON.stringify({ creado: data }) };

    case 'DELETE /items/{id}':
      return { statusCode: 204, body: '' };

    default:
      return { statusCode: 404, body: JSON.stringify({ error: 'Not found' }) };
  }
};
```

En API Gateway, agregar las rutas correspondientes apuntando todas a la misma Lambda:
- `GET /items`
- `GET /items/{id}`
- `POST /items`
- `DELETE /items/{id}`

## 2.5 CORS

Para que una aplicación frontend en un dominio diferente pueda consumir la API, es necesario habilitar CORS:

En la consola API Gateway → seleccionar la API → **CORS**:
- **Access-Control-Allow-Origin:** `*` (o el dominio específico del frontend)
- **Access-Control-Allow-Methods:** `GET, POST, DELETE, OPTIONS`
- **Access-Control-Allow-Headers:** `content-type`

También se pueden agregar los headers en el response de Lambda directamente:

```javascript
return {
  statusCode: 200,
  headers: {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
  },
  body: JSON.stringify(resultado),
};
```

## 2.6 Throttling y cuotas

API Gateway tiene límites de throttling por defecto:
- **Burst limit:** 5.000 requests simultáneos por región
- **Rate limit:** 10.000 requests por segundo por región

Para evitar que un pico de tráfico genere costos inesperados, configurar throttling en el stage:

1. En la API, ir a **Stages → $default**
2. En **Throttling**, configurar:
   - **Rate:** 1.000 req/s
   - **Burst:** 2.000 req

## 2.7 Logs de acceso

Para ver qué requests llegan a la API, habilitar access logging:

1. En **Stages → $default → Logging**
2. Activar **Access logging**
3. Crear un Log Group en CloudWatch: `/aws/apigateway/mi-api`
4. Formato de log recomendado:
```json
{"requestId":"$context.requestId","ip":"$context.identity.sourceIp","routeKey":"$context.routeKey","status":"$context.status","responseLength":"$context.responseLength","duration":"$context.integrationLatency"}
```

## 2.8 Resumen

| Componente | Rol |
|------------|-----|
| API Gateway (HTTP API) | Recibe requests HTTP, enruta a Lambda |
| Lambda handler | Procesa el request, devuelve respuesta HTTP |
| `event.routeKey` | Identifica qué ruta y método se invocó |
| `event.pathParameters` | Parámetros de ruta (`/items/{id}`) |
| `event.queryStringParameters` | Query string (`?nombre=valor`) |
| `event.body` | Body del request (string, parsear con JSON.parse) |

Continuar con [03 - Triggers y Eventos](../03-Lambda-y-Eventos/1-Triggers-y-Eventos.md)
