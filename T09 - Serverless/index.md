# T09 - Serverless

El modelo serverless elimina la gestión de servidores: no hay EC2 que provisionar, no hay OS que parchear, no hay capacidad que planificar. El código corre en funciones que se invocan bajo demanda y escalan automáticamente a cero cuando no hay tráfico.

**AWS Lambda** es el servicio serverless de AWS: ejecuta código en respuesta a eventos (requests HTTP, mensajes en una cola, cambios en un bucket S3, registros en una base de datos) y cobra solo por el tiempo de ejecución en incrementos de 1ms.

## Obligatorias

- [01-Lambda: Primera función](/T09%20-%20Serverless/Obligatorias/01-Lambda/1-Primera-Funcion-Lambda.md)
- [02-Lambda y API Gateway: API REST serverless](/T09%20-%20Serverless/Obligatorias/02-Lambda-y-API-Gateway/1-API-REST-Serverless.md)
- [03-Lambda y Eventos: Triggers desde S3 y SQS](/T09%20-%20Serverless/Obligatorias/03-Lambda-y-Eventos/1-Triggers-y-Eventos.md)
- [04-SAM: Infrastructure as Code para Serverless](/T09%20-%20Serverless/Obligatorias/04-SAM/1-AWS-SAM.md)
- [05-Lambda en Pipeline: Deploy automatizado](/T09%20-%20Serverless/Obligatorias/05-Lambda-en-Pipeline/1-Deploy-desde-GitHub-Actions.md)

## Recomendadas

- [Exploración autónoma: Serverless Framework, Lambda Layers, Step Functions](/T09%20-%20Serverless/Recomendadas/index.md)
