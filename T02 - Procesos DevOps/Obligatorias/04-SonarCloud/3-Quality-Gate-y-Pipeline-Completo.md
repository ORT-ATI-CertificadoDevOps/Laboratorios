# SonarCloud — Quality Gate y Pipeline Completo

En este paso integramos SonarCloud como etapa de **Quality Gate** dentro del pipeline de `02-GitHub-Actions`, y armamos la versión final que une todo lo visto en T02:

```
push → Build imagen → Scan seguridad (Trivy) → Quality Gate (SonarCloud) → Push a registry
         (Docker)          (03-Trivy)              (SonarCloud)                (Docker Hub)
```

Este pipeline une los cuatro módulos de T02 en un flujo real de CI.

## 3.1 Obtener token de SonarCloud

1. Ingresar a [sonarcloud.io](https://sonarcloud.io)
2. Ir a **My Account → Security → Generate Tokens**
3. Nombre: `github-actions-lab`
4. Copiar el token generado

## 3.2 Crear organización y proyecto en SonarCloud

1. En SonarCloud, ir a **+** → **Analyze new project**
2. Seleccionar el repositorio `lab-github-actions-t02` de GitHub
3. Elegir el plan gratuito
4. En **Set up project for Clean as You Code**, seleccionar **With GitHub Actions**
5. SonarCloud mostrará el valor de `SONAR_TOKEN` y el `projectKey` — anotarlos

## 3.3 Agregar secret en GitHub

En **Settings → Secrets and variables → Actions**, agregar:

| Nombre | Valor |
|--------|-------|
| `SONAR_TOKEN` | Token generado en SonarCloud |

## 3.4 Agregar archivo de configuración de SonarCloud

Crear `sonar-project.properties` en la raíz del repositorio:

```properties
sonar.projectKey=TU_USUARIO_lab-github-actions-t02
sonar.organization=TU_ORGANIZACION_EN_SONARCLOUD
sonar.sources=.
sonar.exclusions=**/*.md,**/.github/**
```

> Reemplazar `TU_USUARIO` y `TU_ORGANIZACION_EN_SONARCLOUD` con los valores obtenidos al crear el proyecto en SonarCloud.

## 3.5 Agregar el job de SonarCloud al pipeline

Agregar el job `test` en `.github/workflows/pipeline.yml`, después del job `scan` (Trivy). Actualizar el `needs` del job de push para que dependa de `test`:

```yaml
  test:
    name: Quality Gate (SonarCloud)
    runs-on: ubuntu-latest
    needs: scan
    steps:
      - name: Checkout código
        uses: actions/checkout@v4
        with:
          fetch-depth: 0   # necesario para análisis completo de historial

      - name: Análisis SonarCloud
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

```bash
git add .
git commit -m "ci: add SonarCloud quality gate"
git push origin main
```

En SonarCloud, ir al proyecto y observar:

- **Reliability**: bugs detectados
- **Security**: vulnerabilidades
- **Maintainability**: code smells
- **Coverage**: cobertura de tests (0% por ahora — no tenemos tests unitarios)

## 3.6 Pipeline completo

El pipeline final con todos los jobs integrados:

```yaml
name: Pipeline DevOps

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:

jobs:
  build:
    name: Build Docker Image
    runs-on: ubuntu-latest
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Build imagen (sin push)
        uses: docker/build-push-action@v5
        with:
          context: .
          push: false
          tags: lab-github-actions:${{ github.sha }}

  scan:
    name: Scan de seguridad (Trivy)
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Build imagen para escaneo
        uses: docker/build-push-action@v5
        with:
          context: .
          push: false
          load: true
          tags: lab-github-actions:${{ github.sha }}

      - name: Escaneo Trivy
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: lab-github-actions:${{ github.sha }}
          format: table
          exit-code: '1'
          ignore-unfixed: true
          vuln-type: 'os,library'
          severity: 'CRITICAL,HIGH'

  test:
    name: Quality Gate (SonarCloud)
    runs-on: ubuntu-latest
    needs: scan
    steps:
      - name: Checkout código
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Análisis SonarCloud
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  push-artifact:
    name: Push a Docker Hub
    runs-on: ubuntu-latest
    needs: test
    if: github.event_name != 'pull_request'
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Login a Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Build y push imagen
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: |
            ${{ secrets.DOCKERHUB_USERNAME }}/lab-github-actions:latest
            ${{ secrets.DOCKERHUB_USERNAME }}/lab-github-actions:${{ github.sha }}
```

Verificar en GitHub Actions que los jobs se ejecutan en el orden correcto:

```
build → scan → test → push-artifact
```

> **SonarCloud también puede bloquear el pipeline.** Para verlo en acción, crear un archivo `credenciales.js` con `const password = "admin123"` — SonarCloud lo detecta como hardcoded credential y falla el quality gate. Borrar el archivo después de la prueba.

## Resumen del pipeline construido

| Etapa | Herramienta | Módulo | Qué hace |
|-------|-------------|--------|----------|
| Build | Docker + GHA | 01 / 02 | Construye la imagen |
| Scan | Trivy | 03 | Busca CVEs en la imagen Docker |
| Test | SonarCloud | 04 | Analiza calidad y seguridad del código |
| Artifact | Docker Hub | 02 | Publica la imagen si pasan ambos gates |

Este es el núcleo de un pipeline CI real.

## Próximos pasos

Continuar con [T03 — GitHub Actions Avanzado](/T03%20-%20Herramientas%20CICD/Obligatorias/01-GithubActions/01-Prerrequisitos)
