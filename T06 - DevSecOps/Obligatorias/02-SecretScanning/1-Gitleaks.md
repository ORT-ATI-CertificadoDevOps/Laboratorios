# Secret Scanning con Gitleaks

Un secret hardcodeado en el código fuente — una clave de AWS, un token de API, una contraseña de base de datos — es una de las vulnerabilidades más comunes y más costosas. Una vez que el secreto está en el historial de Git, aunque se borre en el próximo commit, sigue siendo recuperable. La única solución real es rotarlo.

**Gitleaks** escanea repositorios y el historial de Git buscando patrones que coincidan con secretos conocidos: claves AWS (`AKIA...`), tokens de GitHub, contraseñas, certificados privados, y más de 100 patrones predefinidos.

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

## 2.2 Escanear el repositorio

**Escanear el directorio actual (staged + unstaged):**
```bash
gitleaks detect --source . --verbose
```

**Escanear el historial completo de Git:**
```bash
gitleaks detect --source . --log-opts="--all" --verbose
```

**Escanear solo los últimos N commits:**
```bash
gitleaks detect --source . --log-opts="-n 50" --verbose
```

Si no hay hallazgos, la salida termina con:
```
WRN No leaks found
```

Si encuentra algo:
```
Finding:     AKIAIOSFODNN7ABCDEFG
Secret:      AKIAIOSFODNN7ABCDEFG
RuleID:      aws-access-token
Entropy:     3.84
File:        config.js
Line:        12
Commit:      a3f2c1d...
```

## 2.3 Simular un hallazgo

Para entender el output, agregar una clave falsa con el patrón que Gitleaks reconoce:

```bash
# En cualquier archivo del repo
echo 'const awsKey = "AKIAIOSFODNN7ABCDEFG";' > test-secret.js
gitleaks detect --source . --verbose
```

Gitleaks detecta el patrón `AKIA` seguido de 16 caracteres alfanuméricos. Verificar que aparece el hallazgo con `RuleID: aws-access-token`.

Eliminar el archivo antes de continuar:
```bash
rm test-secret.js
```

## 2.4 Integrar Gitleaks en GitHub Actions

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

El `fetch-depth: 0` es importante: sin él, `actions/checkout` solo descarga el último commit y Gitleaks no puede analizar el historial.

## 2.5 Configurar reglas personalizadas

Gitleaks usa un archivo de configuración `.gitleaks.toml` para extender o sobrescribir las reglas predefinidas.

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
  '''AKIAIOSFODNN7EXAMPLE''',  # clave de ejemplo de la doc de AWS
  '''example-token-.*''',
]
paths = [
  '''tests/.*''',
  '''docs/.*''',
]
```

El `allowlist` es crítico para evitar falsos positivos en archivos de tests o documentación que usen secretos de ejemplo ficticios.

**Usar el archivo en el workflow:**

```yaml
- name: Run Gitleaks
  uses: gitleaks/gitleaks-action@v2
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  with:
    config-path: .gitleaks.toml
```

## 2.6 Pre-commit hook (opcional pero recomendado)

Para detectar secretos antes de que lleguen al repositorio, configurar Gitleaks como pre-commit hook:

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

## 2.7 Probar el gate en un PR

1. Crear una branch de feature:
```bash
git checkout -b feature/test-gitleaks
```

2. Agregar una clave hardcodeada:
```bash
echo 'const key = "AKIAIOSFODNN7ABCDEFG";' >> config.js
git add config.js
git commit -m "test: trigger gitleaks"
git push origin feature/test-gitleaks
```

3. Abrir un PR en GitHub. El job `Secret Scanning (Gitleaks)` va a fallar y el merge queda bloqueado (si branch protection está configurada con este check).

4. Corregir eliminando el secreto, hacer push, verificar que el check pasa.

## 2.8 ¿Qué hacer si se detecta un secreto real?

1. **Rotar el secreto inmediatamente** — revocar en el proveedor (AWS IAM, GitHub Settings, etc.)
2. **Eliminar del código** — reemplazar por referencia a variable de entorno o Secret Manager
3. **Limpiar el historial** (opcional pero recomendado) — con `git filter-repo` o BFG Repo Cleaner
4. **Auditar accesos** — verificar si el secreto fue usado por alguien no autorizado

> El paso 3 requiere force-push y coordinación con el equipo. En repos públicos, asumir que el secreto fue leído y rotarlo es suficiente; limpiar el historial no garantiza que nadie lo haya descargado.

## 2.9 Resumen

| Acción | Comando / Configuración |
|--------|------------------------|
| Escanear directorio | `gitleaks detect --source . --verbose` |
| Escanear historial | `gitleaks detect --source . --log-opts="--all"` |
| Integración CI | `gitleaks/gitleaks-action@v2` |
| Reglas custom | `.gitleaks.toml` |
| Pre-commit hook | `pre-commit install` con config YAML |

Continuar con [03 - Container Scanning con Trivy](../03-ContainerScanning/1-Trivy.md)
