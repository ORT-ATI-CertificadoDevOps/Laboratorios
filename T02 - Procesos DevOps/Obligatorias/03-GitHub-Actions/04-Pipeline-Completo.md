# GitHub Actions — Pipeline Completo

En este último paso integramos SonarCloud como etapa de **Test/Quality Gate**, completando el pipeline de T02:

```
push → Build imagen → Scan seguridad (Trivy) → Scan calidad (SonarCloud) → Push a registry
         (Docker)            (Trivy)               (SonarCloud)               (Docker Hub)
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

## 4.5 Agregar el job de SonarCloud al pipeline

Agregar el job `test` en `.github/workflows/pipeline.yml`, después del job `build` existente:

```yaml
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

## 4.6 Escaneo de seguridad (Trivy)

[Trivy](https://trivy.dev) es un escáner de vulnerabilidades open source desarrollado por Aqua Security. A diferencia de SonarCloud (que analiza el código fuente), Trivy escanea los paquetes instalados dentro de la imagen Docker final — incluyendo la imagen base — buscando CVEs conocidos.

No requiere cuenta ni secrets: se ejecuta directamente en el runner con la action oficial de Aqua Security.

Agregar el job `scan` en `.github/workflows/pipeline.yml`, entre `build` y `test`. También actualizar `needs` del job `test` de `build` a `scan`:

```yaml
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
```

> El job `scan` reconstruye la imagen localmente con `load: true` para que quede disponible en el runner, y falla si encuentra vulnerabilidades `CRITICAL` o `HIGH` con fix disponible.

## 4.7 Pipeline completo

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

```bash
git add .
git commit -m "ci: add Trivy security scan to pipeline"
git push origin main
```

Verificar en GitHub Actions que los jobs se ejecutan en el orden correcto:

```
build → scan → test → push-artifact
```

## 4.8 Trivy va a fallar — y eso es lo esperado

Al hacer push del pipeline completo, el job `scan` va a fallar. `nginx:alpine` es una tag flotante que acumula CVEs conocidos con fix disponible, exactamente lo que Trivy está configurado para bloquear.

En la pestaña **Actions** se va a ver el job `scan` en rojo, con una tabla similar a esta en los logs:

```
2024-XX-XX nginx:alpine (alpine 3.x)
===========================================
Total: 3 (HIGH: 3, CRITICAL: 0)

┌──────────┬────────────────┬──────────┬──────────────────┬───────────────┐
│ Library  │ Vulnerability  │ Severity │ Installed Version│ Fixed Version │
├──────────┼────────────────┼──────────┼──────────────────┼───────────────┤
│ libssl3  │ CVE-XXXX-XXXXX │ HIGH     │ 3.x.x-rX         │ 3.x.x-rX+1   │
└──────────┴────────────────┴──────────┴──────────────────┴───────────────┘
```

Los jobs `test` y `push-artifact` quedan cancelados automáticamente — la imagen nunca llega al registry. El gate está funcionando.

## 4.9 Corregir la imagen base

La solución es reemplazar `nginx:alpine` por una imagen diseñada para pasar scanners de seguridad. [Chainguard](https://edu.chainguard.dev/chainguard/chainguard-images/getting-started/nginx/) mantiene imágenes con cero CVEs conocidos, actualizadas continuamente ante nuevas vulnerabilidades.

En el `Dockerfile`, cambiar la imagen base:

```dockerfile
FROM cgr.dev/chainguard/nginx
```

El path de los archivos estáticos es el mismo (`/usr/share/nginx/html/`), por lo que los `COPY` no cambian.

```bash
git add Dockerfile
git commit -m "fix: use Chainguard nginx image to pass Trivy scan"
git push origin main
```

Esta vez el job `scan` pasa, `test` analiza el código con SonarCloud y, si el quality gate pasa también, `push-artifact` publica la imagen en Docker Hub.

> **SonarCloud también puede bloquear el pipeline.** Para verlo en acción, crear un archivo `credenciales.js` con `const password = "admin123"` — SonarCloud lo detecta como hardcoded credential y falla el quality gate. Borrar el archivo después de la prueba.

## Resumen del pipeline construido

| Etapa | Herramienta | Qué hace |
|-------|-------------|----------|
| Build | Docker + GHA | Construye la imagen |
| Scan | Trivy | Busca CVEs en la imagen Docker |
| Test | SonarCloud | Analiza calidad y seguridad del código |
| Artifact | Docker Hub | Publica la imagen si pasan ambos gates |

Este es el núcleo de un pipeline CI real.

## Próximos pasos

Continuar con [05 - Variables y Artefactos](05-Variables-y-Artefactos.md)

---

## Ejercicio Integrador — Fase 3: Deploy Automático del Portfolio

Ya tenés tu portfolio containerizado con Docker (Fase 2). Ahora vas a automatizar el deploy a **GitHub Pages** para que quede disponible públicamente en internet con cada push a `main`.

### 5.1 Habilitar GitHub Pages en el repositorio

1. Ir al repositorio `portfolio-devops` en GitHub
2. Ir a **Settings → Pages**
3. En **Source**, seleccionar **GitHub Actions**

### 5.2 Crear el workflow de deploy

Crear el archivo `.github/workflows/deploy.yml` en tu repositorio `portfolio-devops`:

```yaml
name: Deploy Portfolio

on:
  push:
    branches: [main]
  workflow_dispatch:

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: "pages"
  cancel-in-progress: false

jobs:
  deploy:
    name: Deploy a GitHub Pages
    environment:
      name: github-pages
      url: ${{ steps.deployment.outputs.page_url }}
    runs-on: ubuntu-latest
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Configurar Pages
        uses: actions/configure-pages@v5

      - name: Subir artefacto
        uses: actions/upload-pages-artifact@v3
        with:
          path: '.'

      - name: Deploy a GitHub Pages
        id: deployment
        uses: actions/deploy-pages@v4
```

### 5.3 Hacer push y verificar el deploy

```bash
cd portfolio-devops
mkdir -p .github/workflows
git add .github/workflows/deploy.yml
git commit -m "ci: agregar workflow de deploy a GitHub Pages"
git push origin main
```

1. Ir a la pestaña **Actions** del repositorio y observar el workflow ejecutarse
2. Una vez completado, ir a **Settings → Pages** y copiar la URL pública
3. Verificar que el portfolio está disponible en `https://TU_USUARIO.github.io/portfolio-devops`

> **¿Qué hace este workflow?**
> 1. Se activa en cada push a `main`
> 2. Empaqueta el contenido del repositorio como un artefacto estático
> 3. Lo despliega en la infraestructura de GitHub Pages
>
> Resultado: URL pública y permanente, actualizada automáticamente con cada commit.

> **Próxima fase:** En el laboratorio de SonarCloud vas a agregar análisis de calidad como quality gate antes del deploy, para que el portfolio no se publique si tiene vulnerabilidades de seguridad.

---

## Ejercicio Integrador — Fase 5: Security Gate con Trivy

Ya tenés el portfolio desplegándose automáticamente con el workflow de la Fase 3 y el quality gate de SonarCloud de la Fase 4. Ahora vas a agregar **Trivy como security gate**: la imagen Docker del portfolio se escanea antes del deploy, y si tiene CVEs críticos o altos con fix disponible, el pipeline se detiene.

La versión final del workflow (con todos los gates integrados) está en la sección [Fase 5](/Ejercicio-Integrador#fase-5-trivy-security-gate) del Ejercicio Integrador.

### ¿Por qué escanear si el portfolio es HTML estático?

El portfolio corre sobre `nginx:alpine`. Aunque el código fuente sea solo HTML/CSS/JS, la imagen base trae dependencias del sistema operativo que pueden tener CVEs conocidos. Trivy escanea la imagen completa — imagen base incluida — no solo el código propio.

### Verificar el resultado

Después del push, en la pestaña **Actions** del repositorio vas a ver el job `scan` ejecutarse primero. Si la imagen base de nginx está actualizada, Trivy debería pasar. Para probar el bloqueo, podés cambiar temporalmente `severity` a `LOW` y verificar que el deploy queda cancelado.
