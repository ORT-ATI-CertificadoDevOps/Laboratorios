# SAST — Análisis Estático de Código

SAST (Static Application Security Testing) analiza el código fuente sin ejecutarlo. Detecta vulnerabilidades como inyección SQL, XSS, manejo inseguro de datos sensibles y configuraciones peligrosas antes de que el código llegue a producción.

```
Código fuente → [analizador SAST] → reporte de vulnerabilidades
                                  → quality gate: pass / fail
```

El análisis ocurre en el pipeline CI, sobre cada PR. Si hay hallazgos críticos, el merge queda bloqueado.

## 1.1 SAST en el contexto del curso

En T02 se configuró SonarCloud para análisis básico de calidad. En este lab se profundiza en las capacidades de seguridad de SonarCloud: cómo interpretar Security Hotspots, cómo configurar Quality Profiles con reglas de seguridad, y cómo usar el Quality Gate como control de acceso a `main`.

## 1.2 Prerrequisitos

- Repositorio conectado a SonarCloud (configurado en T02)
- GitHub Actions pipeline activo
- Acceso a SonarCloud con permisos de administrador en el proyecto

## 1.3 Security Hotspots vs Vulnerabilities

SonarCloud distingue dos categorías de hallazgos de seguridad:

| Tipo | Descripción | Requiere acción |
|------|-------------|-----------------|
| **Vulnerability** | Problema confirmado que debe corregirse | Sí — bloquea el Quality Gate |
| **Security Hotspot** | Código que requiere revisión manual para confirmar si es un problema | Depende del contexto |

Un Security Hotspot no es necesariamente un bug. Por ejemplo, usar `Math.random()` en JavaScript aparece como hotspot porque no es criptográficamente seguro — pero si se usa para generar IDs de sesión es un problema real, y si se usa para ordenar una lista, no lo es.

## 1.4 Revisar hallazgos de seguridad en SonarCloud

1. Ingresar a [sonarcloud.io](https://sonarcloud.io) y abrir el proyecto
2. Ir a la pestaña **Security**
3. Explorar las secciones **Vulnerabilities** y **Security Hotspots**

Para cada hallazgo, SonarCloud muestra:
- La línea de código afectada
- Por qué es un problema (con referencia a OWASP o CWE)
- Cómo corregirlo
- El nivel de severidad: `BLOCKER`, `CRITICAL`, `MAJOR`, `MINOR`

## 1.5 Configurar el Quality Gate de seguridad

El Quality Gate define cuándo un análisis "pasa" o "falla". Por defecto el gate de SonarCloud bloquea si hay nuevas vulnerabilidades. Se puede personalizar:

1. En SonarCloud, ir a **Quality Gates** (menú principal, no dentro del proyecto)
2. Hacer clic en **Create** para crear un gate personalizado
3. Agregar condiciones:

| Métrica | Operador | Valor |
|---------|----------|-------|
| New Vulnerabilities | is greater than | 0 |
| New Security Hotspots Reviewed | is less than | 100% |
| New Bugs | is greater than | 0 |

4. En el proyecto, ir a **Project Settings → Quality Gate** y seleccionar el gate recién creado

## 1.6 Interpretar el resultado en el PR

Con el Quality Gate configurado, cada PR mostrará en GitHub un check de SonarCloud. Hacer clic en **Details** lleva al análisis del PR en SonarCloud, donde se pueden ver solo los hallazgos introducidos en esa rama.

```
PR: feature/login-form
──────────────────────
✅ 0 Bugs
❌ 1 Vulnerability        ← falla el Quality Gate
⚠️  2 Security Hotspots
✅ 0 Code Smells nuevos
Coverage: 72% (umbral: 70% ✅)
```

## 1.7 Introducir y corregir una vulnerabilidad

Para practicar, introducir una vulnerabilidad conocida en el código del repositorio:

**Ejemplo en Node.js — SQL Injection:**

```javascript
// Código vulnerable: concatena input del usuario en una query SQL
app.get('/user', (req, res) => {
  const query = "SELECT * FROM users WHERE id = " + req.query.id;
  db.query(query, (err, result) => res.json(result));
});
```

SonarCloud detecta la concatenación directa de input en una query y lo marca como vulnerability con severidad `CRITICAL`.

**Corrección — usar parámetros preparados:**

```javascript
app.get('/user', (req, res) => {
  const query = "SELECT * FROM users WHERE id = ?";
  db.query(query, [req.query.id], (err, result) => res.json(result));
});
```

1. Hacer commit del código vulnerable en una branch
2. Abrir un PR y esperar el análisis de SonarCloud
3. Observar que el Quality Gate falla
4. Corregir el código, hacer push
5. Verificar que el análisis nuevo pasa el gate

## 1.8 Agregar SAST al workflow de GitHub Actions

Si el workflow de SonarCloud no corre en PRs, ajustar el trigger:

```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
```

El análisis de SonarCloud en PRs es automático si el proyecto está configurado con la integración de GitHub. Verificar en **Project Settings → Pull Request Decoration** que esté habilitado.

## 1.9 Resumen

| Herramienta | Qué detecta | Cuándo actúa |
|-------------|-------------|--------------|
| SonarCloud SAST | Vulnerabilidades en código fuente | En cada PR y push |
| Quality Gate | Define el umbral de aceptación | Bloquea merge si falla |
| Security Hotspots | Patrones que requieren revisión | Visibles en dashboard |

Continuar con [02 - Secret Scanning con Gitleaks](../02-SecretScanning/1-Gitleaks.md)
