# Deploy automatizado de Lambda desde GitHub Actions

Con SAM configurado, el siguiente paso es automatizar el deploy: cada merge a `main` construye la aplicación, corre los tests y la despliega en AWS sin intervención manual. Este pipeline es el equivalente serverless del CD de contenedores visto en T07.

```
PR → tests → merge a main → sam build → sam deploy → Lambda actualizada
```

## 5.1 Prerequisitos

- Proyecto SAM con `samconfig.toml` commiteado (del lab anterior)
- Secrets de AWS configurados en GitHub:
  - `AWS_ACCESS_KEY_ID`
  - `AWS_SECRET_ACCESS_KEY`
  - `AWS_REGION` (ej: `us-east-1`)

## 5.2 Workflow completo

```yaml
# .github/workflows/deploy-serverless.yml
name: Deploy Serverless

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  AWS_REGION: ${{ secrets.AWS_REGION }}

jobs:
  test:
    name: Tests
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: src/api/package-lock.json

      - name: Install dependencies
        run: npm ci
        working-directory: src/api

      - name: Run tests
        run: npm test
        working-directory: src/api

  deploy:
    name: Deploy
    runs-on: ubuntu-latest
    needs: test
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    environment: production

    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Setup SAM CLI
        uses: aws-actions/setup-sam@v2

      - name: SAM Build
        run: sam build --use-container

      - name: SAM Deploy
        run: |
          sam deploy \
            --no-confirm-changeset \
            --no-fail-on-empty-changeset \
            --parameter-overrides Environment=prod
```

**`--no-confirm-changeset`:** en CI no hay interactividad — SAM aplica los cambios directamente.

**`--no-fail-on-empty-changeset`:** si no hubo cambios en la infraestructura, SAM no falla el job — simplemente no hace nada.

**`--use-container`:** build dentro de Docker para reproducir el entorno Lambda exacto. Necesario cuando hay dependencias nativas (por ejemplo, módulos Python con C extensions).

## 5.3 Deploy multi-ambiente

Separar dev y prod con environments de GitHub Actions:

```yaml
jobs:
  deploy-dev:
    name: Deploy Dev
    if: github.ref == 'refs/heads/develop'
    environment: dev
    steps:
      # ... configurar AWS
      - run: sam deploy --parameter-overrides Environment=dev

  deploy-prod:
    name: Deploy Prod
    if: github.ref == 'refs/heads/main'
    environment: production   # requiere aprobación manual si está configurado
    needs: deploy-dev
    steps:
      # ... configurar AWS
      - run: sam deploy --parameter-overrides Environment=prod
```

En **Settings → Environments → production**, activar **Required reviewers** para que el deploy a producción requiera aprobación explícita.

## 5.4 Smoke test post-deploy

Después del deploy, verificar que la función responde correctamente antes de dar el pipeline por exitoso:

```yaml
      - name: Get API URL
        id: get-url
        run: |
          API_URL=$(aws cloudformation describe-stacks \
            --stack-name mi-app-serverless \
            --query 'Stacks[0].Outputs[?OutputKey==`ApiUrl`].OutputValue' \
            --output text)
          echo "api_url=$API_URL" >> $GITHUB_OUTPUT

      - name: Smoke test
        run: |
          STATUS=$(curl -s -o /dev/null -w "%{http_code}" ${{ steps.get-url.outputs.api_url }}/items)
          if [ "$STATUS" != "200" ]; then
            echo "Smoke test falló: HTTP $STATUS"
            exit 1
          fi
          echo "Smoke test OK: HTTP $STATUS"
```

## 5.5 Rollback automático

SAM despliega vía CloudFormation, que tiene rollback automático: si el deploy falla durante la actualización, CloudFormation revierte al estado anterior. El pipeline puede detectar esto:

```yaml
      - name: SAM Deploy
        run: |
          sam deploy \
            --no-confirm-changeset \
            --no-fail-on-empty-changeset \
            --parameter-overrides Environment=prod
        # Si CloudFormation hace rollback, sam deploy retorna exit code != 0
        # → el job falla y GitHub notifica
```

Para deploy gradual (canary), usar **Lambda Deployment Preferences** en el template SAM:

```yaml
  ApiFuncion:
    Type: AWS::Serverless::Function
    Properties:
      # ...
      DeploymentPreference:
        Type: Canary10Percent5Minutes
        # Envía el 10% del tráfico a la nueva versión
        # Si no hay errores en 5 minutos, migra el 100%
        # Si hay errores, rollback automático
        Alarms:
          - !Ref ErrorAlarm
```

## 5.6 Resumen del pipeline completo

```
push a main
    ↓
tests unitarios
    ↓  (solo si tests pasan)
sam build --use-container
    ↓
sam deploy --parameter-overrides Environment=prod
    ↓
CloudFormation actualiza la función Lambda
    ↓
smoke test contra la API
    ↓
✅ Deploy exitoso / ❌ rollback automático
```

---

## Resumen del módulo T09

| Lab | Concepto clave |
|-----|----------------|
| 01-Lambda | Handler, event, context, logs en CloudWatch |
| 02-API Gateway | HTTP API, rutas, parámetros, CORS |
| 03-Eventos | S3 trigger, SQS trigger, EventBridge cron |
| 04-SAM | `template.yaml`, `sam build`, `sam deploy`, test local |
| 05-Pipeline | GitHub Actions, multi-ambiente, smoke test, rollback |
