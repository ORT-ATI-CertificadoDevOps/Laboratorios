# GitHub Actions Avanzado — Optimización y Patrones

Con el pipeline funcionando, este lab cubre tres patrones que aparecen en pipelines de producción real: **cache**, **matrix builds** y **reusable workflows**.

## 5.1 Cache de dependencias

Cada run de un workflow parte desde cero. El cache permite reutilizar archivos entre runs para acelerar los builds.

### Cache de capas Docker

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Build imagen con cache
        uses: docker/build-push-action@v5
        with:
          context: .
          push: false
          tags: webapp:${{ github.sha }}
          cache-from: type=gha        # lee cache de runs anteriores
          cache-to: type=gha,mode=max # guarda cache para el próximo run
```

Ejecutar el workflow dos veces y comparar el tiempo del step de build. El segundo run debería ser significativamente más rápido porque reutiliza las capas de Docker cacheadas.

### Cache de dependencias npm (ejemplo)

Si la aplicación usara Node.js:

```yaml
      - name: Cache node_modules
        uses: actions/cache@v4
        with:
          path: ~/.npm
          key: ${{ runner.os }}-node-${{ hashFiles('**/package-lock.json') }}
          restore-keys: |
            ${{ runner.os }}-node-

      - name: Install dependencies
        run: npm ci
```

La clave del cache incluye el hash del `package-lock.json`. Si el archivo no cambia, se reutiliza el cache. Si cambia (nuevas dependencias), se invalida automáticamente.

## 5.2 Matrix builds

El matrix strategy ejecuta el mismo job múltiples veces con diferentes parámetros. Útil para testear en múltiples versiones de un lenguaje o múltiples OS.

### Ejemplo: testear en múltiples versiones de Node

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        node-version: [18, 20, 22]
    steps:
      - uses: actions/checkout@v4

      - name: Setup Node ${{ matrix.node-version }}
        uses: actions/setup-node@v4
        with:
          node-version: ${{ matrix.node-version }}

      - run: npm ci
      - run: npm test
```

Esto genera 3 jobs en paralelo: uno por cada versión de Node. Si falla en Node 18 pero pasa en 20 y 22, se ve claramente en la UI de Actions.

### Ejemplo: matrix con múltiples variables

```yaml
    strategy:
      matrix:
        os: [ubuntu-latest, windows-latest]
        node: [18, 20]
      exclude:
        - os: windows-latest
          node: 18   # excluir combinaciones específicas
```

## 5.3 Reusable Workflows

Un reusable workflow es un workflow que puede ser llamado por otros workflows. Evita duplicar la lógica de CI/CD en múltiples repositorios.

### Crear el workflow reutilizable

Crear `.github/workflows/reusable-build.yml`:

```yaml
name: Reusable Build

on:
  workflow_call:              # ← lo que lo hace reutilizable
    inputs:
      image-name:
        required: true
        type: string
    secrets:
      DOCKERHUB_USERNAME:
        required: true
      DOCKERHUB_TOKEN:
        required: true

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ secrets.DOCKERHUB_USERNAME }}/${{ inputs.image-name }}:${{ github.sha }}
```

### Llamar el workflow reutilizable

Desde cualquier otro workflow (incluso en otro repositorio):

```yaml
jobs:
  build:
    uses: TU-USUARIO/lab-github-actions-t03/.github/workflows/reusable-build.yml@main
    with:
      image-name: mi-aplicacion
    secrets:
      DOCKERHUB_USERNAME: ${{ secrets.DOCKERHUB_USERNAME }}
      DOCKERHUB_TOKEN: ${{ secrets.DOCKERHUB_TOKEN }}
```

> En organizaciones con múltiples microservicios, el equipo de plataforma mantiene workflows reutilizables centralizados. Los equipos de producto los consumen sin tener que entender los detalles de CI/CD.

## 5.4 Resumen: comparativa Jenkins vs GitHub Actions

| Aspecto | Jenkins | GitHub Actions |
|---------|---------|----------------|
| Infraestructura | Servidor propio a mantener | Gestionado por GitHub |
| Configuración | Jenkinsfile + plugins + UI | YAML en el repositorio |
| Integración con Git | Via plugins | Nativa |
| Marketplace de actions | Plugins (requieren instalación) | 20.000+ actions públicas |
| Costo | Infraestructura propia | Gratis para repos públicos, minutos incluidos en privados |
| Curva de aprendizaje | Alta (UI + Groovy + admin) | Media (YAML) |
| Auditabilidad | Configuración fuera del repo | Todo versionado en el repo |

GitHub Actions es la elección natural cuando el código ya está en GitHub y no se necesita infraestructura propia.
