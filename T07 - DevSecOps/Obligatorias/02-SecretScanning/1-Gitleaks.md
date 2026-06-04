# Secret Scanning con Gitleaks

Un secret hardcodeado en el código fuente — una clave de AWS, un token de API, una contraseña de base de datos — es una de las vulnerabilidades más comunes y más costosas. Una vez que el secreto está en el historial de Git, aunque se borre en el próximo commit, sigue siendo recuperable. La única solución real es rotarlo.

**Gitleaks** escanea repositorios y el historial de Git buscando patrones que coincidan con secretos conocidos: tokens de GitHub, claves de API, contraseñas de alta entropía, certificados privados, y más de 100 patrones predefinidos.

## 2.1 Instalar Gitleaks localmente

**macOS:**
```bash
brew install gitleaks
```

**Linux:**
```bash
curl -sSfL https://github.com/gitleaks/gitleaks/releases/latest/download/gitleaks_$(uname -s)_x64.tar.gz | tar xz -C /usr/local/bin
```

**Verificar instalación:**
```bash
gitleaks version
```

## 2.2 Repositorio de práctica

El laboratorio usa el repositorio `oops-i-leaked-again`, el mismo usado en el lab de SAST. Simula una API REST real con un secreto de AWS introducido en un commit específico del historial.

Si ya está clonado del lab anterior:

```bash
cd oops-i-leaked-again
```

Si no:

```bash
git clone git@github.com:ORT-ATI-CertificadoDevOps/oops-i-leaked-again.git
cd oops-i-leaked-again
```

Verificar el historial:

```bash
git log --oneline
```

Salida esperada (del más reciente al más antiguo):

```
2deed18 chore: align app with "Oops I Leaked Again" theme
3099c94 feat: wire routes and add health check endpoint     ← puede haber commits adicionales
42375b9 feat: add request validation middleware               si ya se hizo el lab de SAST
a03cd29 feat: add JWT authentication middleware
dd77c7f feat: add order management routes
4d5a090 feat: add product CRUD routes
87d095d feat: add AWS S3 integration for image uploads      ← secreto aquí
9501eaa feat: add order model
4e63074 feat: add product model
e305e77 feat: add database connection module
cbfca87 feat: add express app entry point
61c7fea feat: initial project structure
```

## 2.3 Escanear el repositorio

Desde dentro de `store-api`, ejecutar un escaneo completo del historial:

```bash
gitleaks detect --source . --log-opts="--all" --verbose
```

Salida esperada — Gitleaks encuentra el secreto:

```
Finding:     secretAccessKey: 'aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ'
Secret:      aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ
RuleID:      generic-api-key
Entropy:     5.142664
File:        src/config/aws.js
Line:        5
Commit:      87d095d...
Message:     feat: add AWS S3 integration for image uploads
```

El campo `Entropy` mide la aleatoriedad del valor: a mayor entropía, más probable que sea un secreto real en lugar de un placeholder. El `RuleID: generic-api-key` indica que Gitleaks detectó el patrón por el nombre de la variable (`secretAccessKey`) combinado con un valor de alta entropía.

## 2.4 El punto ciego: secreto más allá de la ventana de escaneo

Este es el escenario central del laboratorio: **un escaneo acotado puede pasar por alto un secreto que sí existe en el historial**.

### Contexto

El secreto fue introducido en el commit `feat: add AWS S3 integration for image uploads` (`87d095d`). Antes de empezar, localizar su posición actual en el historial:

```bash
git log --oneline | grep -n "AWS S3"
```

La salida indica en qué línea (= posición desde HEAD) está el commit. Por ejemplo:

```
7:87d095d feat: add AWS S3 integration for image uploads
```

Línea 7 significa que es el 7.º commit desde HEAD, es decir HEAD~6. Guardar ese número: `N=6` (el índice empieza en 0).

```
HEAD      ← línea 1
HEAD~1    ← línea 2
HEAD~2    ← línea 3
HEAD~3    ← línea 4
HEAD~4    ← línea 5
HEAD~5    ← línea 6
HEAD~6 ── feat: add AWS S3 integration for image uploads  ← SECRETO (línea 7)
HEAD~7    ← línea 8
...
```

### Paso 1 — Escanear con ventana que NO alcanza el secreto

Usar `-n N` donde N es la posición del commit (sin incluirlo):

```bash
gitleaks detect --source . --log-opts="-n 6" --verbose
```

Salida:

```
INF 6 commits scanned.
INF no leaks found
```

El escaneo analiza HEAD hasta HEAD~5 — el commit con el secreto (HEAD~6) queda fuera de la ventana.

### Paso 2 — Ampliar la ventana para incluir el secreto

```bash
gitleaks detect --source . --log-opts="-n 7" --verbose
```

Salida:

```
INF 7 commits scanned.
WRN leaks found: 1

Finding:     secretAccessKey: 'aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ'
Secret:      aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ
RuleID:      generic-api-key
Entropy:     5.142664
File:        src/config/aws.js
Line:        5
Commit:      87d095d...
Message:     feat: add AWS S3 integration for image uploads
```

Ahora sí. Al incluir el commit HEAD~6, el secreto aparece.

### Conclusión del escenario

> Un escaneo acotado a los últimos commits en un pipeline de CI que no cubre el commit donde se introdujo el secreto hubiera aprobado este código sin detectar el leak.

**La regla práctica:** para detectar secretos en historial preexistente, usar `--log-opts="--all"`. Para PR checks, el `fetch-depth: 0` en el checkout (ver sección 2.5) garantiza que el historial completo esté disponible.

### Identificar exactamente en qué commit entró el secreto

```bash
gitleaks detect --source . --log-opts="--all" --report-format json --report-path leaks.json
cat leaks.json
```

El campo `commit` del JSON tiene el hash completo. Inspeccionarlo:

```bash
git show 87d095d --stat
git show 87d095d -- src/config/aws.js
```

## 2.5 Integrar Gitleaks en GitHub Actions

Agregar el siguiente job al workflow de CI:

```yaml
jobs:
  secret-scanning:
    name: Secret Scanning (Gitleaks)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # historial completo para analizar todos los commits

      - name: Run Gitleaks
        uses: gitleaks/gitleaks-action@v2
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

El `fetch-depth: 0` es crítico: sin él, `actions/checkout` descarga solo el commit más reciente y Gitleaks no puede analizar el historial completo (exactamente el punto ciego del escenario anterior).

## 2.6 Configurar reglas personalizadas

Gitleaks usa un archivo `.gitleaks.toml` para extender o sobrescribir las reglas predefinidas.

**Ejemplo — agregar regla para tokens de un sistema interno:**

```toml
# .gitleaks.toml
title = "Gitleaks config"

[[rules]]
id = "internal-api-token"
description = "Internal API token with MYAPP- prefix"
regex = '''MYAPP-[A-Za-z0-9]{32}'''
tags = ["token", "internal"]

[allowlist]
description = "Allowlist para tests y ejemplos"
regexes = [
  '''example-token-.*''',
]
paths = [
  '''tests/.*''',
  '''docs/.*''',
]
```

El `allowlist` evita falsos positivos en archivos de tests o documentación con secretos ficticios.

**Usar el archivo en el workflow:**

```yaml
- name: Run Gitleaks
  uses: gitleaks/gitleaks-action@v2
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  with:
    config-path: .gitleaks.toml
```

## 2.7 Pre-commit hook (opcional pero recomendado)

Para detectar secretos antes de que lleguen al repositorio:

```bash
# Instalar pre-commit
pip install pre-commit

# Crear .pre-commit-config.yaml
cat > .pre-commit-config.yaml << 'EOF'
repos:
  - repo: https://github.com/gitleaks/gitleaks
    rev: v8.18.0
    hooks:
      - id: gitleaks
EOF

# Activar los hooks
pre-commit install
```

A partir de ese momento, cada `git commit` ejecuta Gitleaks automáticamente. Si detecta algo, el commit falla:

```
❯ git commit -m "add config"
Detect hardcoded secrets.................................................Failed
- hook id: gitleaks
- exit code: 1
```

## 2.8 Probar el gate en un PR

1. Crear una branch de feature:
```bash
git checkout -b feature/test-gitleaks
```

2. Agregar una clave hardcodeada:
```bash
echo 'const token = "ghp_R8kT5mN2pL9vX4jH7aQ1eW0cB3yF6sDuZAB";' >> src/config/github.js
git add src/config/github.js
git commit -m "test: trigger gitleaks"
git push origin feature/test-gitleaks
```

3. Abrir un PR en GitHub. El job `Secret Scanning (Gitleaks)` falla y el merge queda bloqueado (si branch protection está configurada con este check).

4. Eliminar el secreto, hacer push, verificar que el check pasa.

## 2.9 ¿Qué hacer si se detecta un secreto real?

1. **Rotar el secreto inmediatamente** — revocar en el proveedor (AWS IAM, GitHub Settings, etc.)
2. **Eliminar del código** — reemplazar por referencia a variable de entorno o Secret Manager
3. **Limpiar el historial** (opcional pero recomendado) — con `git filter-repo` o BFG Repo Cleaner
4. **Auditar accesos** — verificar si el secreto fue usado por alguien no autorizado

> El paso 3 requiere force-push y coordinación con el equipo. En repos públicos, asumir que el secreto fue leído y rotarlo es suficiente; limpiar el historial no garantiza que nadie lo haya descargado.

## 2.10 Resumen

| Acción | Comando |
|--------|---------|
| Escanear historial completo | `gitleaks detect --source . --log-opts="--all" --verbose` |
| Escanear últimos N commits | `gitleaks detect --source . --log-opts="-n 10" --verbose` |
| Exportar hallazgos a JSON | `gitleaks detect --source . --report-format json --report-path leaks.json` |
| Integración CI | `gitleaks/gitleaks-action@v2` con `fetch-depth: 0` |
| Reglas custom | `.gitleaks.toml` |
| Pre-commit hook | `pre-commit install` con config YAML |

**Lección clave:** `--log-opts="-n 5"` no detecta lo mismo que `--log-opts="--all"`. Un pipeline que analiza solo los commits del PR puede pasar por alto secretos introducidos antes. Siempre escanear el historial completo.

Continuar con [03 - Container Scanning con Trivy](../03-ContainerScanning/1-Trivy.md)
