# SAST — Análisis Estático de Código

SAST (Static Application Security Testing) analiza el código fuente sin ejecutarlo. Detecta vulnerabilidades como inyección SQL, XSS, manejo inseguro de datos sensibles y configuraciones peligrosas antes de que el código llegue a producción.

```
Código fuente → [analizador SAST] → reporte de vulnerabilidades
                                  → quality gate: pass / fail
```

El análisis ocurre en el pipeline CI, sobre cada PR. Si hay hallazgos críticos, el merge queda bloqueado.

## 1.1 Semgrep

Semgrep es un analizador estático open-source que usa reglas basadas en patrones de código. A diferencia de herramientas que solo analizan ASTs, Semgrep permite escribir reglas que lucen casi idénticas al código que buscan detectar.

Ventajas clave:
- Gratuito para proyectos open-source y uso local
- Reglas en YAML legibles y modificables
- Registro público de miles de reglas mantenidas por la comunidad (`semgrep.dev/r`)
- Soporte nativo en GitHub Actions

```
Código fuente → [semgrep --config ruleset] → findings.json / salida en consola
                                           → exit code 1 si hay hallazgos
```

## 1.2 Prerrequisitos

- Python 3.8+ instalado
- Repositorio con código fuente (cualquier lenguaje soportado)
- GitHub Actions pipeline activo

## 1.3 Instalación local

```bash
pip install semgrep
semgrep --version
```

Verificar que funciona con un escaneo rápido sobre el directorio actual:

```bash
semgrep --config auto .
```

`--config auto` descarga automáticamente las reglas recomendadas para los lenguajes detectados en el repositorio.

## 1.4 Rulesets

Semgrep organiza las reglas en rulesets. Los más usados en contexto de seguridad:

| Ruleset | Descripción |
|---------|-------------|
| `p/default` | Reglas de alta confianza para los lenguajes más comunes |
| `p/owasp-top-ten` | Cubre las 10 categorías del OWASP Top Ten |
| `p/secrets` | Detecta secretos hardcodeados (tokens, claves) |
| `p/sql-injection` | Patrones específicos de SQLi |
| `p/xss` | Patrones de Cross-Site Scripting |
| `p/javascript` | Reglas de seguridad para JavaScript/TypeScript |
| `p/python` | Reglas de seguridad para Python |

Se pueden combinar:

```bash
semgrep --config p/owasp-top-ten --config p/secrets .
```

## 1.5 Interpretar resultados

Semgrep imprime cada hallazgo con:

```
/src/app.js:12:  javascript.express.security.audit.sqli.js-node-sqli.js-node-sqli
  Severity: ERROR
  
  app.get('/user', (req, res) => {
    const query = "SELECT * FROM users WHERE id = " + req.query.id;
  
  Details: https://semgrep.dev/r/javascript.express.security.audit.sqli...
```

Campos relevantes:

| Campo | Descripción |
|-------|-------------|
| `archivo:línea` | Ubicación exacta del hallazgo |
| Regla ID | Identifica qué patrón activó el hallazgo |
| `Severity` | `ERROR`, `WARNING`, o `INFO` |
| URL de detalles | Explicación y referencias (OWASP/CWE) |

Semgrep retorna exit code `1` si encuentra hallazgos de severidad `ERROR`. Útil para bloquear pipelines.

## 1.6 Introducir y corregir una vulnerabilidad

El repositorio de práctica es `oops-i-leaked-again`. Trabajar en una branch temporal para no alterar el historial de `main` (esto es importante para el lab de Secret Scanning que viene a continuación):

```bash
git checkout -b lab/sast-exercise
```

Agregar el siguiente endpoint vulnerable en `src/routes/products.js`, antes del `module.exports`:

```javascript
// src/routes/products.js — endpoint vulnerable a SQL Injection
router.get('/search', async (req, res) => {
  const { name } = req.query;
  const { rows } = await db.query(
    "SELECT * FROM products WHERE name LIKE '%" + name + "%'"
  );
  res.json(rows);
});
```

Notar que `name` se concatena directamente en la query sin sanitizar.

Ejecutar Semgrep sobre el archivo:

```bash
semgrep --config p/sql-injection src/routes/products.js
```

Semgrep detecta la concatenación directa de input en la query y reporta el hallazgo.

**Corrección — parámetros preparados:**

Reemplazar el endpoint vulnerable por la versión con placeholder `$1`:

```javascript
router.get('/search', async (req, res) => {
  const { name } = req.query;
  const { rows } = await db.query(
    'SELECT * FROM products WHERE name ILIKE $1',
    [`%${name}%`]
  );
  res.json(rows);
});
```

Este patrón ya existe en el modelo `src/models/order.js` como referencia.

Volver a ejecutar Semgrep — sin hallazgos, exit code `0`.

Descartar la branch temporal:

```bash
git checkout main
git branch -D lab/sast-exercise
```

## 1.7 Semgrep en GitHub Actions

Crear el archivo `.github/workflows/semgrep.yml`:

```yaml
name: SAST - Semgrep

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  semgrep:
    name: Semgrep Scan
    runs-on: ubuntu-latest
    container:
      image: semgrep/semgrep

    steps:
      - uses: actions/checkout@v4

      - name: Run Semgrep
        run: semgrep --config p/default --config p/owasp-top-ten --error .
```

El flag `--error` hace que el job falle (exit code `1`) si Semgrep encuentra hallazgos de severidad `ERROR`, bloqueando el merge del PR.

### Output en formato SARIF (opcional)

SARIF permite que GitHub muestre los hallazgos directamente en la pestaña **Security → Code scanning**:

```yaml
      - name: Run Semgrep (SARIF)
        run: semgrep --config p/default --sarif --output semgrep.sarif .

      - name: Upload SARIF
        uses: github/codeql-action/upload-sarif@v3
        with:
          sarif_file: semgrep.sarif
        if: always()
```

Con `if: always()` el reporte se sube incluso si el paso anterior falló.

## 1.8 Ignorar hallazgos falsos positivos

Para suprimir un hallazgo específico, agregar un comentario en la línea afectada:

```javascript
const id = generateSessionId(); // nosemgrep: javascript.lang.security.audit.unsafe-random
```

Para ignorar un archivo o directorio completo, crear `.semgrepignore`:

```
tests/
migrations/
vendor/
```

Formato idéntico a `.gitignore`.

## 1.9 Resumen

| Concepto | Descripción |
|----------|-------------|
| `--config auto` | Descarga reglas recomendadas para los lenguajes del repo |
| `--config p/<ruleset>` | Aplica un ruleset específico de la comunidad |
| `--error` | Falla con exit code 1 si hay hallazgos `ERROR` |
| `--sarif` | Genera reporte en formato SARIF para GitHub Code Scanning |
| `# nosemgrep` | Suprime hallazgo en una línea específica |
| `.semgrepignore` | Excluye archivos/directorios del análisis |

Continuar con [02 - Secret Scanning con Gitleaks](../02-SecretScanning/1-Gitleaks.md)
