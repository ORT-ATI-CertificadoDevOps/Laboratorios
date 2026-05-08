# DAST con OWASP ZAP

DAST (Dynamic Application Security Testing) prueba la aplicación **en ejecución**, enviando requests reales y analizando las respuestas. A diferencia de SAST (que analiza el código), DAST puede detectar vulnerabilidades que solo se manifiestan en runtime: cabeceras de seguridad ausentes, endpoints sin autenticación, configuraciones del servidor expuestas.

**OWASP ZAP** (Zed Attack Proxy) es el escáner DAST open-source más usado. Intercepta el tráfico HTTP, genera requests de ataque automatizados y reporta hallazgos clasificados por severidad.

## 6.1 Ejecutar ZAP localmente

La forma más sencilla es usar la imagen Docker oficial:

```bash
# Escaneo básico contra una URL (modo "baseline")
docker run --rm ghcr.io/zaproxy/zaproxy:stable \
  zap-baseline.py \
  -t https://tu-app.ejemplo.com \
  -r reporte.html
```

Los modos disponibles son:

| Modo | Descripción | Duración aproximada |
|------|-------------|---------------------|
| `zap-baseline.py` | Escaneo pasivo: observa el tráfico, no ataca | 1–5 min |
| `zap-api-scan.py` | Escaneo de APIs REST con spec OpenAPI/Swagger | 5–15 min |
| `zap-full-scan.py` | Escaneo activo: envía payloads de ataque | 30+ min |

Para los pipelines de CI se usa `baseline` o `api-scan` — el `full-scan` es demasiado lento para correr en cada PR.

## 6.2 Interpretar el reporte

ZAP clasifica los hallazgos en cuatro niveles:

| Nivel | Color | Ejemplos |
|-------|-------|---------|
| High | 🔴 Rojo | SQL Injection, Remote Code Execution |
| Medium | 🟠 Naranja | Falta de Content Security Policy, X-Frame-Options ausente |
| Low | 🟡 Amarillo | Cookies sin HttpOnly flag, información de versión expuesta |
| Informational | 🔵 Azul | Métodos HTTP disponibles, timestamps en respuestas |

**Hallazgos típicos en apps nuevas:**

- `X-Content-Type-Options header missing` — agregar `nosniff`
- `X-Frame-Options header not set` — previene clickjacking
- `Content Security Policy not set` — control de fuentes de scripts
- `Server leaks version information` — nginx/Express exponiendo versión

## 6.3 Corregir hallazgos comunes

**Cabeceras de seguridad faltantes (Node.js/Express):**

```javascript
// Instalar helmet: npm install helmet
const helmet = require('helmet');
app.use(helmet());
```

`helmet` configura automáticamente: `X-Content-Type-Options`, `X-Frame-Options`, `Strict-Transport-Security`, `X-XSS-Protection`, y otras cabeceras de seguridad.

**En nginx:**

```nginx
add_header X-Content-Type-Options nosniff;
add_header X-Frame-Options DENY;
add_header Content-Security-Policy "default-src 'self'";
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains";
```

## 6.4 Integrar ZAP en GitHub Actions

Para correr ZAP en CI, la app debe estar accesible durante el job. La estrategia más simple: levantar la app en el mismo runner y escanearla en `localhost`.

```yaml
jobs:
  dast:
    name: DAST (OWASP ZAP)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Start application
        run: |
          docker build -t mi-app:test .
          docker run -d --name app -p 8080:8080 mi-app:test
          # Esperar a que la app esté lista
          timeout 30 bash -c 'until curl -s http://localhost:8080/health; do sleep 2; done'

      - name: Run ZAP baseline scan
        uses: zaproxy/action-baseline@v0.12.0
        with:
          target: 'http://localhost:8080'
          fail_action: false       # no falla el job, solo reporta
          artifact_name: zap-report
          allow_issue_reporting: false

      - name: Stop application
        if: always()
        run: docker stop app && docker rm app
```

**`fail_action: false`** es lo recomendado para empezar: ZAP puede encontrar muchos hallazgos de nivel `Low` e `Informational` que no son bloqueantes. Una vez que se limpian los hallazgos iniciales, se puede cambiar a `true` para los de nivel `High`.

## 6.5 Escanear una API con spec OpenAPI

Si la aplicación tiene una especificación OpenAPI (Swagger), ZAP puede usarla para generar requests más precisos:

```yaml
- name: Run ZAP API scan
  uses: zaproxy/action-api-scan@v0.7.0
  with:
    target: 'http://localhost:8080/api/openapi.json'
    format: openapi
    fail_action: false
```

ZAP usa la spec para conocer los endpoints, los métodos HTTP esperados, y los parámetros — lo que permite un escaneo más completo que el baseline.

## 6.6 Ver el reporte

El job sube el reporte HTML como artefacto en GitHub Actions. Para descargarlo:

1. Ir a la ejecución del workflow en **Actions**
2. Hacer clic en el job `DAST (OWASP ZAP)`
3. En la sección **Artifacts**, descargar `zap-report`
4. Abrir `report_html.html` en el navegador

## 6.7 Resumen

| Modo ZAP | Cuándo usarlo | Duración |
|----------|---------------|----------|
| `baseline` | En cada PR para detectar cabeceras y configs | 1–5 min |
| `api-scan` | Cuando hay spec OpenAPI disponible | 5–15 min |
| `full-scan` | Auditorías de seguridad planificadas, no en CI diario | 30+ min |

Continuar con [07 - Secrets Management con AWS Secrets Manager](../07-SecretsManagement/1-AWS-Secrets-Manager.md)
