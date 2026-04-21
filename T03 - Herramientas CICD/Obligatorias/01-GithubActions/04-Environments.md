# GitHub Actions Avanzado — Environments y Ambientes

Los **GitHub Environments** permiten modelar el ciclo de vida de deployments:

```
develop → staging → main
   ↓          ↓        ↓
  dev       staging   prod
```

Cada ambiente puede tener sus propios secrets, variables y reglas de protección.

## 4.1 Crear el repositorio de trabajo

Crear un nuevo repositorio `lab-github-actions-t03` (o reutilizar el de T02 en una branch nueva).

Agregar el mismo `index.html` y `Dockerfile` del lab anterior, más los archivos de infraestructura de S3.

## 4.2 Crear los ambientes en GitHub

1. Ir a **Settings → Environments → New environment**
2. Crear los tres ambientes:

| Ambiente | Configuración |
|----------|---------------|
| `dev` | Sin restricciones |
| `staging` | Sin restricciones |
| `production` | **Required reviewers**: agregar tu usuario |

Para `production`, activar **Required reviewers** y agregar tu usuario. Esto significa que el deploy a prod requiere aprobación manual.

## 4.3 Crear los S3 buckets en AWS

Generar 3 buckets en AWS con hosting estático habilitado:

- `dev-gha-webapp-XXXXX`
- `stg-gha-webapp-XXXXX`
- `prod-gha-webapp-XXXXX`

> Agregar un sufijo único (ej: tus iniciales) ya que los nombres de S3 son globales.

Para cada bucket:
1. Desactivar **Block all public access**
2. En **Properties → Static website hosting**: activar, index document = `index.html`

## 4.4 Agregar secrets por ambiente

En cada ambiente de GitHub (**Settings → Environments → [nombre] → Add secret**):

| Secret | Descripción |
|--------|-------------|
| `AWS_ACCESS_KEY_ID` | Credencial de AWS |
| `AWS_SECRET_ACCESS_KEY` | Credencial de AWS |
| `AWS_SESSION_TOKEN` | Solo para credenciales temporales (AWS Academy / STS) |
| `S3_BUCKET` | Nombre del bucket correspondiente al ambiente |

> Los secrets de ambiente sobreescriben los del repositorio. Así cada ambiente deploya a su propio bucket automáticamente.

> **Nota:** Si usás credenciales de largo plazo (IAM user), no agregues `AWS_SESSION_TOKEN`. Si usás AWS Academy o credenciales temporales vía STS, es obligatorio.

## 4.5 Workflow con ambientes

Crear `.github/workflows/pipeline.yml`:

```yaml
name: Pipeline CI/CD

on:
  push:
    branches: [develop, staging, main]
  pull_request:
    branches: [staging, main]
  workflow_dispatch:

jobs:
  build-and-test:
    name: Build & Test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Build Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: false
          tags: webapp:${{ github.sha }}

      - name: Detect secrets (Gitleaks)
        uses: gitleaks/gitleaks-action@v2
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  deploy-dev:
    name: Deploy → dev
    needs: build-and-test
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    environment: dev
    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
          aws-region: us-east-1

      - name: Deploy a S3 (dev)
        run: aws s3 sync ./ s3://${{ secrets.S3_BUCKET }} --delete

  deploy-staging:
    name: Deploy → staging
    needs: build-and-test
    if: github.ref == 'refs/heads/staging'
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
          aws-region: us-east-1

      - name: Deploy a S3 (staging)
        run: aws s3 sync ./ s3://${{ secrets.S3_BUCKET }} --delete

  deploy-production:
    name: Deploy → production
    needs: build-and-test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: production       # ← requiere aprobación manual
    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
          aws-region: us-east-1

      - name: Deploy a S3 (production)
        run: aws s3 sync ./ s3://${{ secrets.S3_BUCKET }} --delete
```

## 4.6 Probar el flujo de promotion

```bash
# Deploy a dev
git checkout -b develop
git push origin develop
# → Se deploya automáticamente a dev

# Promotion a staging via PR
git checkout -b staging
git merge develop
git push origin staging
# → Se deploya automáticamente a staging

# Promotion a producción via PR (requiere aprobación)
git checkout main
git merge staging
git push origin main
# → GitHub pausa el deploy y envía notificación para aprobar
```

En el último push, ir a **Actions → el workflow en ejecución** y observar que el job `deploy-production` está esperando revisión. Hacer clic en **Review deployments → Approve**.

## Próximos pasos

Continuar con [05 - Optimización y Patrones Avanzados](05-Optimizacion.md)
