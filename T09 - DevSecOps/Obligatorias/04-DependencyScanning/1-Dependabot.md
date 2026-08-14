# Dependency Scanning con Dependabot

Las dependencias de una aplicación — librerías npm, paquetes pip, módulos Go — tienen su propio historial de vulnerabilidades. Una librería que hoy es segura puede tener un CVE publicado mañana. **Dependabot** monitorea las dependencias del repositorio y abre Pull Requests automáticos cuando hay versiones con fixes de seguridad disponibles.

## 4.1 Activar Dependabot en el repositorio

Dependabot se configura con un archivo en `.github/dependabot.yml`:

```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "npm"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 10
    labels:
      - "dependencies"
      - "security"

  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "weekly"
    labels:
      - "docker"
      - "dependencies"
```

**`package-ecosystem`** admite: `npm`, `pip`, `maven`, `gradle`, `docker`, `terraform`, `github-actions`, entre otros.

Hacer commit de este archivo a `main`. Dependabot se activa automáticamente y en las próximas horas abre PRs si hay actualizaciones disponibles.

## 4.2 Activar Dependabot Security Alerts

Además de los PRs de actualización, Dependabot puede enviar alertas cuando detecta una vulnerabilidad conocida en una dependencia existente (sin necesariamente tener un fix disponible).

1. En el repositorio, ir a **Settings → Security → Code security and analysis**
2. Activar:
   - **Dependency graph** — analiza los archivos de lock para mapear dependencias
   - **Dependabot alerts** — notifica cuando hay CVEs en dependencias
   - **Dependabot security updates** — abre PRs automáticos con fixes

## 4.3 Revisar las alertas

Las alertas de seguridad están en **Security → Dependabot alerts** en el repositorio. Para cada alerta se muestra:

- El paquete afectado y la versión instalada
- El CVE y su severidad (usando CVSS score)
- La versión que corrige la vulnerabilidad
- El camino de dependencia (directo o transitivo)

Las dependencias **transitivas** son las más comunes y las más difíciles de manejar: son dependencias de las dependencias. Por ejemplo, si `mi-app` depende de `express`, y `express` depende de `qs`, una vulnerabilidad en `qs` aparece como alerta aunque `qs` no esté en el `package.json` del proyecto.

## 4.4 Revisar y mergear PRs de Dependabot

Dependabot abre PRs con mensajes como:

```
Bump lodash from 4.17.15 to 4.17.21
```

Cada PR incluye:
- Un changelog de la librería
- Las notas del CVE corregido
- Los checks de CI corriendo sobre la actualización

**Flujo recomendado:**
1. Verificar que el CI pasa (tests, build)
2. Revisar el changelog para detectar breaking changes
3. Mergear si todo está bien

Para actualizaciones de patch (x.y.Z → x.y.Z+1) sin breaking changes, es seguro hacer merge directamente. Para minor y major, revisar con más cuidado.

## 4.5 Escaneo manual con npm audit

Para NPM, `npm audit` permite ver las vulnerabilidades conocidas en las dependencias instaladas sin esperar a Dependabot:

```bash
# Ver vulnerabilidades
npm audit

# Ver solo CRITICAL y HIGH
npm audit --audit-level=high

# Corregir automáticamente vulnerabilidades con fix disponible
npm audit fix

# Forzar actualizaciones que podrían tener breaking changes
npm audit fix --force
```

**Integrar en el pipeline:**

```yaml
jobs:
  dependency-audit:
    name: Dependency Audit
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - run: npm ci
      - run: npm audit --audit-level=high
```

Este job falla si hay vulnerabilidades con severidad `high` o `critical`, bloqueando el merge del PR.

## 4.6 Alternativa: Snyk

[Snyk](https://snyk.io) es una alternativa a Dependabot con capacidades más avanzadas: analiza dependencias, imágenes Docker, IaC y código (SAST). Tiene integración con GitHub Actions:

```yaml
- name: Run Snyk
  uses: snyk/actions/node@master
  env:
    SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
  with:
    args: --severity-threshold=high
```

Snyk requiere crear una cuenta en snyk.io y agregar el token como secret en el repositorio.

## 4.7 Resumen

| Herramienta | Qué hace | Cuándo actúa |
|-------------|----------|--------------|
| Dependabot alerts | Notifica CVEs en dependencias existentes | Contínuo, según el feed de seguridad |
| Dependabot updates | Abre PRs con versiones que corrigen CVEs | Según el `schedule` configurado |
| `npm audit` | Escaneo manual/CI de dependencias npm | En cada ejecución del pipeline |
| Snyk | SAST + dependencias + Docker + IaC | En el pipeline o contínuo |

Continuar con [05 - IaC Security con Checkov](../05-IaCSecurity/1-Checkov.md)
