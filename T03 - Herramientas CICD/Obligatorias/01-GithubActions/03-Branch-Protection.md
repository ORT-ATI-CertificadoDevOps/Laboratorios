# GitHub Actions — Branch Protection y Pull Requests

Hasta ahora el pipeline corre con cada push directo a `main`. El problema: si alguien pushea código con CVEs críticos o que falla el análisis de Semgrep, el pipeline falla *después* de que el código ya está en la rama principal.

**Branch protection** invierte esa lógica: el código solo puede entrar a `main` si el pipeline pasó primero. Esto convierte el pipeline en un gate real, no un sistema de alertas post-hoc.

```
feature → [PR] → pipeline corre → [todos los checks OK] → merge habilitado
                                → [algún check falla]  → merge bloqueado
```

## 3.1 Activar Branch Protection

1. En el repositorio, ir a **Settings → Branches**
2. Hacer clic en **Add branch protection rule**
3. En **Branch name pattern**, escribir `main`
4. Activar las siguientes opciones:

| Opción | Descripción |
|--------|-------------|
| **Require a pull request before merging** | Prohíbe el push directo a `main` |
| **Require status checks to pass before merging** | Bloquea el merge hasta que los checks pasen |
| **Require branches to be up to date before merging** | Obliga a que la branch tenga los últimos cambios de `main` |

5. Hacer clic en **Save changes**

## 3.2 Agregar los status checks requeridos

Después de activar **Require status checks**, aparece un campo de búsqueda. GitHub solo muestra checks que ya corrieron al menos una vez en el repositorio.

Buscar y agregar:
- `Build Docker Image`
- `Scan de seguridad (Trivy)`
- `Quality Gate (Semgrep)`

> Si los checks no aparecen, hacer un push a una branch (no a `main`) para que corran primero, y luego volver a configurar.

## 3.3 Ajustar el workflow para PRs

Con branch protection activa, el flujo cambia: los pushes van a branches de feature, y el pipeline corre en el PR. Es importante que los jobs de escaneo corran también en PRs, no solo en pushes a `main`.

El workflow actual ya tiene `pull_request` en el trigger, pero el job `push-artifact` tiene un `if: github.event_name != 'pull_request'` que lo excluye correctamente — no se hace push a Docker Hub por cada PR, solo cuando el código llega a `main`.

Verificar que el trigger esté configurado así:

```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
  workflow_dispatch:
```

Este trigger hace que el pipeline corra:
- En PRs hacia `main` (todos los jobs excepto `push-artifact`)
- En pushes directos a `main` (todos los jobs, incluyendo `push-artifact`)

## 3.4 Probar el flujo con un PR

```bash
# Crear una branch de feature
git checkout -b feature/test-protection

# Hacer un cambio menor
echo "<!-- updated -->" >> index.html
git add index.html
git commit -m "test: verify branch protection"
git push origin feature/test-protection
```

1. En GitHub, ir a la pestaña **Pull requests → New pull request**
2. Base: `main`, compare: `feature/test-protection`
3. Crear el PR

GitHub va a mostrar en el PR los checks corriendo en tiempo real. El botón **Merge pull request** permanece gris hasta que todos los status checks requeridos pasen.

Una vez que todos los checks están en verde, el botón se habilita. Hacer merge.

## 3.5 Probar el bloqueo

Para verificar que el gate funciona, introducir un fallo intencional:

```bash
git checkout -b feature/test-block

# Agregar credencial hardcodeada (Semgrep p/secrets lo detecta como finding)
cat >> credenciales.js << 'EOF'
const awsKey = "AKIAIOSFODNN7EXAMPLE";
const awsSecret = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
EOF

git add credenciales.js
git commit -m "test: trigger semgrep failure"
git push origin feature/test-block
```

Crear el PR. Observar que el job `Quality Gate (Semgrep)` falla y el merge queda bloqueado con el mensaje:

> **"Required status checks haven't passed yet"**

Borrar el archivo y hacer push para que el PR se limpie:

```bash
rm credenciales.js
git add credenciales.js
git commit -m "test: remove test file"
git push origin feature/test-block
```

## 3.6 Protección adicional: CODEOWNERS

El archivo `CODEOWNERS` define quién debe revisar cambios en partes específicas del repositorio. Cuando está configurado junto con **Require review from Code Owners** en branch protection, nadie puede hacer merge a `main` sin aprobación del owner del área modificada.

Crear `.github/CODEOWNERS`:

```
# Todo el repositorio requiere revisión de estos usuarios
*   @TU-USUARIO

# Workflows de CI/CD requieren revisión del equipo de infraestructura
.github/workflows/   @TU-USUARIO
```

> En equipos reales, `.github/workflows/` suele requerir revisión del equipo de plataforma para evitar que cualquier desarrollador modifique el pipeline y eluda los gates de seguridad.

Para activarlo en GitHub, en **Settings → Branches → [regla de main]**, activar **Require review from Code Owners**.

## 3.7 Resumen del pipeline como gate

Con branch protection activa, el proceso CI completo queda así:

```
Developer → git push → PR → pipeline corre en PR
                              ↓
                    ┌─ build: imagen construida ─────────────────┐
                    ├─ scan: Trivy sin CVEs críticos ────────────┤ todos deben pasar
                    └─ test: Semgrep sin findings críticos ───────┘
                              ↓
                    Merge habilitado → push a main
                              ↓
                    pipeline corre en main → push-artifact → Docker Hub
```

| Capa de protección | Herramienta | Cuándo actúa |
|--------------------|-------------|--------------|
| Análisis de código | Semgrep | En el PR, antes del merge |
| Vulnerabilidades en imagen | Trivy | En el PR, antes del merge |
| Build reproducible | Docker | En el PR, antes del merge |
| Publicación del artefacto | Docker Hub | Solo en main, post-merge |

Continuar con [04 - Environments y Ambientes](04-Environments.md)

---

## Ejercicio Integrador — Fase 6: Proteger el portfolio

Aplicar branch protection al repositorio `portfolio-devops`:

1. En **Settings → Branches**, agregar una regla para `main`
2. Activar **Require status checks** y agregar los checks del workflow de deploy
3. Crear una branch `feature/update-portfolio`, hacer un cambio y abrir un PR
4. Verificar que el deploy no se ejecuta hasta que el merge sea aprobado

Esto completa el ciclo: el portfolio solo se publica automáticamente cuando el código pasó por el pipeline y fue mergeado a `main`.
