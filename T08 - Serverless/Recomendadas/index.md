# T09 Serverless — Recomendadas

## Serverless Framework

Serverless Framework es una alternativa a SAM multi-cloud (AWS, GCP, Azure) con un ecosistema de plugins. Define la infraestructura en `serverless.yml` y maneja el ciclo de vida completo.

**Para explorar:**
- Instalar: `npm install -g serverless`
- Inicializar proyecto: `serverless create --template aws-nodejs`
- Comparar el `serverless.yml` resultante con el `template.yaml` de SAM

## Lambda Layers

Los Layers son paquetes de dependencias compartidos entre múltiples funciones Lambda. Evitan subir las mismas librerías repetidas en cada función y permiten actualizar dependencias independientemente del código.

**Para explorar:**
- Crear un Layer con una librería pesada (ej: `pandas` en Python)
- Referenciar el Layer desde múltiples funciones en el template SAM
- Observar la reducción en el tamaño del deployment package

## Step Functions

Step Functions orquesta flujos de trabajo complejos como una máquina de estados. En vez de una Lambda monolítica que hace todo, Step Functions encadena varias Lambdas, maneja errores y reintentos, y permite flujos paralelos y condicionales.

**Para explorar:**
- Crear una state machine con tres pasos: validación → procesamiento → notificación
- Agregar manejo de errores con `Catch`
- Visualizar la ejecución en la consola de Step Functions (el grafo de ejecución es muy didáctico)

## Lambda con container images

En lugar de un zip, Lambda puede usar una imagen Docker completa (hasta 10 GB). Permite usar cualquier runtime y herramientas que no caben en el modelo de deployment clásico.

**Para explorar:**
- Crear un `Dockerfile` basado en `public.ecr.aws/lambda/python:3.12`
- Subir la imagen a ECR
- Crear una función Lambda usando la imagen como runtime
