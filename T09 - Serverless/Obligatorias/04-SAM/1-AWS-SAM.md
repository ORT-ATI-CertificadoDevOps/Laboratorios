# AWS SAM — Infrastructure as Code para Serverless

Crear funciones Lambda desde la consola funciona para experimentar, pero no escala: es manual, no reproducible y difícil de versionar. **AWS SAM** (Serverless Application Model) es el framework de IaC de AWS para aplicaciones serverless — define funciones Lambda, APIs, tablas DynamoDB y colas SQS en un archivo `template.yaml`, y los despliega con un solo comando.

SAM es una extensión de CloudFormation con sintaxis simplificada para recursos serverless.

## 4.1 Instalar SAM CLI

**macOS:**
```bash
brew install aws-sam-cli
```

**Linux:**
```bash
curl -Lo aws-sam-cli.zip https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
unzip aws-sam-cli.zip -d sam-installation
./sam-installation/install
```

**Verificar:**
```bash
sam --version
```

## 4.2 Estructura de un proyecto SAM

```
mi-app-serverless/
├── template.yaml          # definición de la infraestructura
├── src/
│   ├── api/
│   │   └── handler.js     # código de la función de API
│   └── procesador/
│       └── handler.py     # código del procesador de eventos
└── events/
    ├── api-get.json       # evento de prueba para la API
    └── s3-upload.json     # evento de prueba para S3
```

## 4.3 Template SAM básico

```yaml
# template.yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31

Description: Aplicación serverless de ejemplo

Globals:
  Function:
    Runtime: nodejs20.x
    Timeout: 30
    MemorySize: 256
    Environment:
      Variables:
        ENVIRONMENT: !Ref Environment

Parameters:
  Environment:
    Type: String
    Default: dev
    AllowedValues: [dev, staging, prod]

Resources:

  # Función de API REST
  ApiFuncion:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: !Sub 'mi-api-${Environment}'
      CodeUri: src/api/
      Handler: handler.handler
      Events:
        GetItems:
          Type: HttpApi
          Properties:
            Path: /items
            Method: GET
        GetItem:
          Type: HttpApi
          Properties:
            Path: /items/{id}
            Method: GET
        PostItem:
          Type: HttpApi
          Properties:
            Path: /items
            Method: POST

  # Función de procesamiento S3
  ProcesadorFuncion:
    Type: AWS::Serverless::Function
    Properties:
      FunctionName: !Sub 'procesador-s3-${Environment}'
      CodeUri: src/procesador/
      Handler: handler.handler
      Runtime: python3.12
      Policies:
        - S3ReadPolicy:
            BucketName: !Ref BucketEntrada
        - S3WritePolicy:
            BucketName: !Ref BucketSalida
      Events:
        NuevoArchivo:
          Type: S3
          Properties:
            Bucket: !Ref BucketEntrada
            Events: s3:ObjectCreated:*
            Filter:
              S3Key:
                Rules:
                  - Name: suffix
                    Value: .csv

  # Tabla DynamoDB para la API
  TablaItems:
    Type: AWS::DynamoDB::Table
    Properties:
      TableName: !Sub 'items-${Environment}'
      BillingMode: PAY_PER_REQUEST
      AttributeDefinitions:
        - AttributeName: id
          AttributeType: S
      KeySchema:
        - AttributeName: id
          KeyType: HASH

  # Buckets S3
  BucketEntrada:
    Type: AWS::S3::Bucket

  BucketSalida:
    Type: AWS::S3::Bucket

Outputs:
  ApiUrl:
    Description: URL de la API
    Value: !Sub 'https://${ServerlessHttpApi}.execute-api.${AWS::Region}.amazonaws.com'
  TablaItemsArn:
    Value: !GetAtt TablaItems.Arn
```

## 4.4 Comandos principales

**Inicializar un proyecto desde template:**
```bash
sam init
# Seguir el wizard: runtime, template, nombre del proyecto
```

**Construir el proyecto** (instala dependencias, empaqueta el código):
```bash
sam build
```

**Probar localmente** (requiere Docker):
```bash
# Invocar una función con un evento de prueba
sam local invoke ApiFuncion --event events/api-get.json

# Levantar la API completa localmente
sam local start-api --port 3000
curl http://localhost:3000/items
```

**Validar el template:**
```bash
sam validate
```

**Desplegar en AWS:**
```bash
# Primera vez: modo guiado (guarda la configuración en samconfig.toml)
sam deploy --guided

# Siguientes veces
sam deploy
```

**Ver logs de una función:**
```bash
sam logs --name ApiFuncion --tail
```

**Eliminar el stack:**
```bash
sam delete
```

## 4.5 `samconfig.toml` — configuración persistente

Después del primer `sam deploy --guided`, SAM crea `samconfig.toml` con los parámetros elegidos:

```toml
version = 0.1

[default.deploy.parameters]
stack_name = "mi-app-serverless"
s3_bucket = "aws-sam-cli-managed-default-samclisourcebucket-xxxx"
region = "us-east-1"
confirm_changeset = true
capabilities = "CAPABILITY_IAM"
parameter_overrides = "Environment=dev"
```

Este archivo va al repositorio (sin secrets) y permite que el CI/CD haga deploy sin parámetros interactivos.

## 4.6 Políticas predefinidas de SAM

SAM incluye políticas IAM predefinidas para casos comunes. Evitan escribir JSON de IAM a mano:

```yaml
Policies:
  - S3ReadPolicy:
      BucketName: !Ref MiBucket
  - DynamoDBCrudPolicy:
      TableName: !Ref MiTabla
  - SQSPollerPolicy:
      QueueName: !GetAtt MiCola.QueueName
  - SecretsManagerReadWrite:
      SecretArn: !Ref MiSecreto
```

Lista completa en: https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-policy-templates.html

## 4.7 Resumen

| Comando | Qué hace |
|---------|----------|
| `sam init` | Crea proyecto desde template |
| `sam build` | Empaqueta código y dependencias |
| `sam validate` | Valida el template YAML |
| `sam local invoke` | Invoca función localmente con Docker |
| `sam local start-api` | Levanta la API completa localmente |
| `sam deploy --guided` | Deploy interactivo a AWS |
| `sam deploy` | Deploy con configuración guardada |
| `sam logs --tail` | Ver logs en tiempo real |
| `sam delete` | Elimina el stack de CloudFormation |

Continuar con [05 - Deploy automatizado desde GitHub Actions](../05-Lambda-en-Pipeline/1-Deploy-desde-GitHub-Actions.md)
