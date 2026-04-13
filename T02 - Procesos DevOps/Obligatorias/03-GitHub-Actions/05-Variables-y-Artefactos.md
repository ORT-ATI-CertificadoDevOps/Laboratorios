# GitHub Actions — Variables, Contextos y Artefactos

Hasta ahora los jobs del pipeline trabajan de forma aislada: cada uno hace su tarea pero no comparte información con los demás. En un pipeline real, los jobs necesitan pasar datos entre sí: el job de build genera una imagen con un tag específico, y los jobs de scan y push tienen que saber cuál es ese tag.

Este lab cubre los mecanismos de comunicación internos de GitHub Actions:

```
build → [outputs el tag] → scan → [usa el tag] → push
                ↓
         [sube artefacto]
                ↓
         descargable desde Actions UI
```

## 5.1 GitHub Contexts

GitHub Actions expone información del entorno a través de **contextos**, accesibles con la sintaxis `${{ context.propiedad }}`.

| Contexto | Qué contiene |
|----------|-------------|
| `github` | Metadata del evento: `github.sha`, `github.ref`, `github.actor`, `github.event_name` |
| `env` | Variables de entorno definidas en el workflow |
| `secrets` | Secrets del repositorio o ambiente |
| `steps` | Outputs de steps anteriores del mismo job |
| `jobs` | Outputs de jobs anteriores (vía `needs`) |
| `runner` | Info del runner: `runner.os`, `runner.arch` |

Agregar un step al pipeline existente para imprimir contexto útil:

```yaml
- name: Imprimir contexto
  run: |
    echo "SHA: ${{ github.sha }}"
    echo "Branch: ${{ github.ref_name }}"
    echo "Evento: ${{ github.event_name }}"
    echo "Actor: ${{ github.actor }}"
    echo "Runner OS: ${{ runner.os }}"
```

## 5.2 Variables de entorno

Las variables de entorno se pueden definir en tres niveles. El nivel más específico tiene precedencia.

```yaml
name: Pipeline DevOps

env:                          # ← nivel workflow (disponible en todos los jobs)
  APP_NAME: lab-github-actions
  REGISTRY: docker.io

jobs:
  build:
    runs-on: ubuntu-latest
    env:                      # ← nivel job (disponible en todos los steps del job)
      BUILD_TARGET: production
    steps:
      - name: Usar variables
        env:                  # ← nivel step (solo en este step)
          STEP_VAR: valor-local
        run: |
          echo "App: $APP_NAME"
          echo "Target: $BUILD_TARGET"
          echo "Step var: $STEP_VAR"
```

Una práctica común es definir el tag de la imagen como variable de workflow para usarlo consistentemente:

```yaml
env:
  IMAGE_TAG: ${{ github.sha }}
```

## 5.3 Step outputs

Un step puede exportar valores para que los steps siguientes del mismo job los usen.

La forma moderna es escribir al archivo `$GITHUB_OUTPUT`:

```yaml
steps:
  - name: Generar tag de imagen
    id: tag           # ← necesario para referenciarlo después
    run: |
      SHORT_SHA=$(echo "${{ github.sha }}" | cut -c1-7)
      echo "value=$SHORT_SHA" >> $GITHUB_OUTPUT

  - name: Usar el tag
    run: echo "Tag: ${{ steps.tag.outputs.value }}"
```

> El formato es `nombre=valor` — sin espacios alrededor del `=`.

## 5.4 Job outputs

Para pasar datos de un job a otro, se usa una combinación de step output + job output + `needs`:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    outputs:
      image-tag: ${{ steps.tag.outputs.value }}   # ← expone el output del job
    steps:
      - name: Generar tag
        id: tag
        run: |
          SHORT_SHA=$(echo "${{ github.sha }}" | cut -c1-7)
          echo "value=$SHORT_SHA" >> $GITHUB_OUTPUT

  scan:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Usar tag del job anterior
        run: echo "Escaneando imagen con tag ${{ needs.build.outputs.image-tag }}"
```

## 5.5 Artefactos

Los **artefactos** son archivos generados durante un workflow que quedan disponibles para descarga desde la UI de Actions durante 90 días. Son útiles para:

- Reportes de tests y cobertura
- Logs de escaneos de seguridad
- Binarios compilados
- Imágenes Docker guardadas como `.tar`

### Subir un artefacto

```yaml
- name: Generar reporte
  run: |
    mkdir -p reports
    echo "Build: OK" > reports/build-report.txt
    echo "SHA: ${{ github.sha }}" >> reports/build-report.txt
    echo "Fecha: $(date -u)" >> reports/build-report.txt

- name: Subir reporte
  uses: actions/upload-artifact@v4
  with:
    name: build-report
    path: reports/
    retention-days: 30       # opcional, default 90
```

### Descargar en un job posterior

```yaml
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Descargar reporte
        uses: actions/download-artifact@v4
        with:
          name: build-report
          path: reports/

      - name: Usar el reporte
        run: cat reports/build-report.txt
```

## 5.6 Aplicar al pipeline

Actualizar `.github/workflows/pipeline.yml` para incorporar lo aprendido: el job `build` genera un tag corto como output y lo usa el job `scan`; además se genera un reporte de build como artefacto.

```yaml
name: Pipeline DevOps

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:

env:
  APP_NAME: lab-github-actions

jobs:
  build:
    name: Build Docker Image
    runs-on: ubuntu-latest
    outputs:
      image-tag: ${{ steps.tag.outputs.value }}
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Generar tag
        id: tag
        run: |
          SHORT_SHA=$(echo "${{ github.sha }}" | cut -c1-7)
          echo "value=$SHORT_SHA" >> $GITHUB_OUTPUT

      - name: Build imagen (sin push)
        uses: docker/build-push-action@v5
        with:
          context: .
          push: false
          tags: ${{ env.APP_NAME }}:${{ steps.tag.outputs.value }}

      - name: Generar reporte de build
        run: |
          mkdir -p reports
          echo "## Build Report" > reports/build-report.md
          echo "- App: ${{ env.APP_NAME }}" >> reports/build-report.md
          echo "- Tag: ${{ steps.tag.outputs.value }}" >> reports/build-report.md
          echo "- SHA: ${{ github.sha }}" >> reports/build-report.md
          echo "- Branch: ${{ github.ref_name }}" >> reports/build-report.md
          echo "- Actor: ${{ github.actor }}" >> reports/build-report.md
          echo "- Fecha: $(date -u)" >> reports/build-report.md

      - name: Subir reporte
        uses: actions/upload-artifact@v4
        with:
          name: build-report-${{ steps.tag.outputs.value }}
          path: reports/

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
          tags: ${{ env.APP_NAME }}:${{ needs.build.outputs.image-tag }}

      - name: Escaneo Trivy
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: ${{ env.APP_NAME }}:${{ needs.build.outputs.image-tag }}
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
    needs: [build, test]
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
            ${{ secrets.DOCKERHUB_USERNAME }}/${{ env.APP_NAME }}:latest
            ${{ secrets.DOCKERHUB_USERNAME }}/${{ env.APP_NAME }}:${{ needs.build.outputs.image-tag }}
```

## 5.7 Verificar

```bash
git add .
git commit -m "ci: add job outputs, artifacts and env variables"
git push origin main
```

En la pestaña **Actions**, al abrir el run:

- En el job `build`: el step **Generar tag** muestra el SHA corto en los logs
- El job `scan` usa ese mismo tag (visible en el step **Build imagen para escaneo**)
- El job `push-artifact` pushea la imagen con el tag corto en lugar del SHA completo
- En la barra lateral derecha del run aparece la sección **Artifacts** — hacer clic en `build-report-XXXXX` para descargar el reporte

## Próximos pasos

Continuar con [06 - Branch Protection](06-Branch-Protection.md)
