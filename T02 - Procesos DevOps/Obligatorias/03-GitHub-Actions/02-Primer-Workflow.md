# GitHub Actions — Primer Workflow

El objetivo de este paso es entender la estructura de un workflow creando uno simple que refleja las etapas del pipeline DevOps.

## 2.1 Crear el repositorio

1. En GitHub, crear un nuevo repositorio llamado `lab-github-actions-t02`
2. Marcarlo como **Public** (necesario para SonarCloud gratuito)
3. Inicializarlo con un `README.md`
4. Clonarlo localmente:

```bash
git clone https://github.com/TU-USUARIO/lab-github-actions-t02.git
cd lab-github-actions-t02
```

## 2.2 Agregar una aplicación de ejemplo

Vamos a usar una aplicación web simple con un `Dockerfile`. Crear los siguientes archivos:

**`index.html`**
```html
<!DOCTYPE html>
<html>
  <head><title>Lab GitHub Actions</title></head>
  <body>
    <h1>Pipeline DevOps con GitHub Actions</h1>
    <p>Build: <strong>OK</strong></p>
  </body>
</html>
```

**`Dockerfile`**
```dockerfile
FROM nginx:alpine
COPY index.html /usr/share/nginx/html/index.html
```

## 2.3 Crear el primer workflow

Crear el directorio `.github/workflows/` y dentro el archivo `pipeline.yml`:

```bash
mkdir -p .github/workflows
```

**`.github/workflows/pipeline.yml`**
```yaml
name: Pipeline DevOps

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    name: Build
    runs-on: ubuntu-latest
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Simular build
        run: echo "Construyendo la aplicación..."

  test:
    name: Test
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Simular tests
        run: echo "Ejecutando tests..."

  deploy:
    name: Deploy
    runs-on: ubuntu-latest
    needs: test
    steps:
      - name: Simular deploy
        run: echo "Desplegando a producción..."
```

## 2.4 Ejecutar y observar

```bash
git add .
git commit -m "feat: add initial pipeline workflow"
git push origin main
```

1. Ir a la pestaña **Actions** del repositorio en GitHub
2. Observar cómo se ejecutan los jobs en secuencia: Build → Test → Deploy
3. Hacer clic en cada job para ver sus logs
4. Notar que `test` espera a `build` (por el `needs: build`) y `deploy` espera a `test`

## 2.5 Agregar un workflow_dispatch

Modificar el trigger para poder ejecutar el workflow manualmente:

```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:        # permite ejecutar desde la UI de GitHub
```

Hacer push del cambio y verificar que aparece el botón **Run workflow** en la pestaña Actions.

## 2.6 Introducir un fallo intencional

Para entender cómo GitHub Actions reporta errores, modificar el step de test:

```yaml
- name: Simular tests fallidos
  run: |
    echo "Ejecutando tests..."
    exit 1
```

Hacer push y observar:
- El job `test` falla (ícono rojo)
- El job `deploy` **no se ejecuta** (queda skipped) porque `needs: test` no se cumplió
- GitHub envía una notificación al email del owner

Revertir el cambio antes de continuar.

## Próximos pasos

Continuar con [03 - Pipeline con Docker](03-Pipeline-con-Docker.md)
