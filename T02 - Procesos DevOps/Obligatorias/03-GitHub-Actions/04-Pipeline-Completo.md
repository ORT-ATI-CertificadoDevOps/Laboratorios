# GitHub Actions — Pipeline Completo

En este último paso integramos SonarCloud como etapa de **Test/Quality Gate**, completando el pipeline de T02:

```
push → Build imagen → Scan calidad (SonarCloud) → Push a registry
         (Docker)         (SonarCloud)               (Docker Hub)
```

Este pipeline une los tres labs de T02 en un flujo real de CI.

## 4.1 Obtener token de SonarCloud

1. Ingresar a [sonarcloud.io](https://sonarcloud.io)
2. Ir a **My Account → Security → Generate Tokens**
3. Nombre: `github-actions-lab`
4. Copiar el token generado

## 4.2 Crear organización y proyecto en SonarCloud

1. En SonarCloud, ir a **+** → **Analyze new project**
2. Seleccionar el repositorio `lab-github-actions-t02` de GitHub
3. Elegir el plan gratuito
4. En **Set up project for Clean as You Code**, seleccionar **With GitHub Actions**
5. SonarCloud mostrará el valor de `SONAR_TOKEN` y el `projectKey` — anotarlos

## 4.3 Agregar secret en GitHub

En **Settings → Secrets and variables → Actions**, agregar:

| Nombre | Valor |
|--------|-------|
| `SONAR_TOKEN` | Token generado en SonarCloud |

## 4.4 Agregar archivo de configuración de SonarCloud

Crear `sonar-project.properties` en la raíz del repositorio:

```properties
sonar.projectKey=TU_USUARIO_lab-github-actions-t02
sonar.organization=TU_ORGANIZACION_EN_SONARCLOUD
sonar.sources=.
sonar.exclusions=**/*.md,**/.github/**
```

> Reemplazar `TU_USUARIO` y `TU_ORGANIZACION_EN_SONARCLOUD` con los valores obtenidos al crear el proyecto en SonarCloud.

## 4.5 Pipeline completo

Reemplazar `.github/workflows/pipeline.yml` con la versión final:

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

  test:
    name: Quality Gate (SonarCloud)
    runs-on: ubuntu-latest
    needs: build
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

## 4.6 Ejecutar y analizar el resultado

```bash
git add .
git commit -m "ci: add SonarCloud quality gate to pipeline"
git push origin main
```

Verificar en GitHub Actions que los jobs se ejecutan en el orden correcto:

```
build → test → push-artifact
```

En SonarCloud, ir al proyecto y observar:
- **Reliability**: bugs detectados
- **Security**: vulnerabilidades
- **Maintainability**: code smells
- **Coverage**: cobertura de tests (0% por ahora — no tenemos tests unitarios)

## 4.7 Quality Gate como bloqueante

Notar que si SonarCloud falla el Quality Gate (por ejemplo, si detecta una vulnerabilidad de seguridad), el job `test` falla y el step `push-artifact` **no se ejecuta**. El artefacto nunca llega al registry si no pasa la calidad.

Para probarlo, agregar código con una vulnerabilidad de seguridad obvia:

**`credenciales.js`** (archivo de prueba — borrar después)
```javascript
const password = "admin123";  // hardcoded password — SonarCloud lo detectará
```

Hacer push y observar cómo SonarCloud reporta el security hotspot.

## Resumen del pipeline construido

| Etapa | Herramienta | Qué hace |
|-------|-------------|----------|
| Build | Docker + GHA | Construye la imagen |
| Test | SonarCloud | Analiza calidad y seguridad del código |
| Artifact | Docker Hub | Publica la imagen si pasa el quality gate |

Este es el núcleo de un pipeline CI real. En T03 vamos a extenderlo con múltiples ambientes, deploy a AWS y patrones más avanzados.
