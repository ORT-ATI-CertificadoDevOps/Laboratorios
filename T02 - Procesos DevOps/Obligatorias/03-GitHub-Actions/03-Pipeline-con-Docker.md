# GitHub Actions — Pipeline con Docker

En este paso vamos a reemplazar los steps simulados del lab anterior por acciones reales: construir la imagen Docker y publicarla en Docker Hub.

Esto implementa las etapas **Build** y **Artifact** del pipeline visto en clase:

```
push → Build imagen Docker → Push a Docker Hub (registry)
```

## 3.1 Crear credenciales en Docker Hub

1. Ingresar a [hub.docker.com](https://hub.docker.com)
2. Ir a **Account Settings → Security → New Access Token**
3. Nombre del token: `github-actions-lab`
4. Copiar el token generado (se muestra una sola vez)

## 3.2 Agregar secrets en GitHub

Los secrets son variables cifradas que el workflow puede usar sin exponer en el código.

1. En el repositorio, ir a **Settings → Secrets and variables → Actions**
2. Hacer clic en **New repository secret** y agregar:

| Nombre | Valor |
|--------|-------|
| `DOCKERHUB_USERNAME` | Tu usuario de Docker Hub |
| `DOCKERHUB_TOKEN` | El token generado en el paso anterior |

## 3.3 Actualizar el workflow

Reemplazar el contenido de `.github/workflows/pipeline.yml`:

```yaml
name: Pipeline DevOps

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:

jobs:
  build-and-push:
    name: Build & Push Docker Image
    runs-on: ubuntu-latest
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
          push: ${{ github.event_name != 'pull_request' }}
          tags: ${{ secrets.DOCKERHUB_USERNAME }}/lab-github-actions:latest

  test:
    name: Test
    runs-on: ubuntu-latest
    needs: build-and-push
    steps:
      - name: Simular tests
        run: echo "Tests pendientes — se implementan en el siguiente paso"
```

## 3.4 Ejecutar y verificar

```bash
git add .github/workflows/pipeline.yml
git commit -m "ci: build and push docker image to Docker Hub"
git push origin main
```

1. Ir a **Actions** y observar la ejecución
2. En el step **Build y push imagen**, expandir los logs para ver el proceso de build
3. Ir a [hub.docker.com](https://hub.docker.com) → tu perfil → **Repositories**
4. Verificar que existe el repositorio `lab-github-actions` con el tag `latest`

## 3.5 Versionado de imágenes con tags de Git

En producción no se usa `latest` — cada build tiene un tag único. Agregar versionado automático usando el SHA del commit:

```yaml
      - name: Build y push imagen
        uses: docker/build-push-action@v5
        with:
          context: .
          push: ${{ github.event_name != 'pull_request' }}
          tags: |
            ${{ secrets.DOCKERHUB_USERNAME }}/lab-github-actions:latest
            ${{ secrets.DOCKERHUB_USERNAME }}/lab-github-actions:${{ github.sha }}
```

Hacer push y verificar en Docker Hub que aparecen dos tags: `latest` y el SHA del commit.

> **¿Por qué el SHA?** Permite saber exactamente qué código contiene cada imagen. Si hay un bug en producción, se puede identificar el commit exacto que lo introdujo.

## Próximos pasos

Continuar con [04 - Pipeline Completo](04-Pipeline-Completo.md)
