# Container Scanning con Trivy

Una imagen de Docker es un stack de capas: OS base, librerías del sistema, dependencias de la aplicación. Cada una de esas capas puede tener CVEs (Common Vulnerabilities and Exposures) conocidas. **Trivy** escanea imágenes de contenedor y reporta vulnerabilidades con severidad, descripción y, cuando existe, la versión que las corrige.

```
docker build → imagen → [Trivy scan] → lista de CVEs con severidad
                                      → CRITICAL/HIGH: falla el pipeline
```

## 3.1 Instalar Trivy localmente

**macOS:**
```bash
brew install trivy
```

**Linux:**
```bash
curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin
```

**Verificar:**
```bash
trivy --version
```

## 3.2 Escanear una imagen localmente

```bash
# Escanear imagen desde Docker Hub
trivy image nginx:latest

# Escanear imagen local (después de docker build)
trivy image mi-app:latest

# Solo mostrar vulnerabilidades CRITICAL y HIGH
trivy image --severity CRITICAL,HIGH nginx:latest

# Output en formato JSON
trivy image --format json --output resultado.json nginx:latest
```

**Ejemplo de output:**

```
nginx:latest (debian 12.5)
==========================
Total: 147 (UNKNOWN: 0, LOW: 85, MEDIUM: 41, HIGH: 18, CRITICAL: 3)

┌──────────────┬────────────────┬──────────┬───────────────────┬──────────────────┬──────────────────────────────────┐
│   Library    │ Vulnerability  │ Severity │ Installed Version │  Fixed Version   │              Title               │
├──────────────┼────────────────┼──────────┼───────────────────┼──────────────────┼──────────────────────────────────┤
│ libssl3      │ CVE-2024-0727  │ CRITICAL │ 3.1.4-2+deb12u2   │ 3.1.5-1          │ OpenSSL: Processing a malformed  │
│              │                │          │                   │                  │ PKCS12 file may lead to a crash  │
└──────────────┴────────────────┴──────────┴───────────────────┴──────────────────┴──────────────────────────────────┘
```

## 3.3 Escanear el sistema de archivos (dependencias de la app)

Trivy también puede escanear el directorio del proyecto sin construir la imagen:

```bash
# Escanear dependencias en el directorio actual
trivy fs .

# Escanear solo archivos de dependencias
trivy fs --scanners vuln package.json
trivy fs --scanners vuln requirements.txt
```

## 3.4 Integrar Trivy en GitHub Actions

```yaml
jobs:
  container-scan:
    name: Container Scanning (Trivy)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Build Docker image
        run: docker build -t mi-app:${{ github.sha }} .

      - name: Run Trivy scan
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: mi-app:${{ github.sha }}
          format: table
          exit-code: '1'          # falla el job si hay CVEs
          severity: 'CRITICAL,HIGH'
          ignore-unfixed: true    # ignora CVEs sin fix disponible
```

El `exit-code: '1'` es el que convierte el escaneo en un gate real: si hay vulnerabilidades con la severidad indicada, el job falla y bloquea el merge (con branch protection configurada).

**`ignore-unfixed: true`** es importante en la práctica: hay CVEs conocidos para los cuales el mantenedor del paquete aún no publicó un fix. Fallar el pipeline por CVEs que no se pueden corregir genera ruido sin valor.

## 3.5 Publicar el reporte como artefacto

Para conservar el reporte aunque el job falle:

```yaml
      - name: Run Trivy scan (SARIF output)
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: mi-app:${{ github.sha }}
          format: sarif
          output: trivy-results.sarif
          severity: 'CRITICAL,HIGH'
          ignore-unfixed: true

      - name: Upload scan results to GitHub Security
        uses: github/codeql-action/upload-sarif@v3
        with:
          sarif_file: trivy-results.sarif
```

Con el formato SARIF y `upload-sarif`, los hallazgos de Trivy aparecen en la pestaña **Security → Code scanning alerts** del repositorio en GitHub. Esto permite trackear CVEs entre ramas y a lo largo del tiempo sin salir de GitHub.

## 3.6 Reducir la superficie de ataque en la imagen

Trivy no solo detecta — sus reportes guían qué cambiar en el `Dockerfile` para reducir vulnerabilidades:

**Usar imágenes base mínimas:**

```dockerfile
# ❌ Imagen base grande con muchas librerías del sistema
FROM node:20

# ✅ Imagen Alpine: mucho menor superficie de ataque
FROM node:20-alpine

# ✅✅ Distroless: solo el runtime, sin shell ni package manager
FROM gcr.io/distroless/nodejs20-debian12
```

**Multi-stage build para dejar fuera herramientas de build:**

```dockerfile
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .

FROM node:20-alpine AS runtime
WORKDIR /app
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/src ./src
USER node          # no correr como root
EXPOSE 3000
CMD ["node", "src/index.js"]
```

Después de cada cambio en el `Dockerfile`, volver a correr Trivy y comparar el número de CVEs.

## 3.7 Ignorar CVEs específicos

Si hay un CVE que no aplica al contexto (por ejemplo, una vulnerabilidad en una funcionalidad que no se usa), se puede ignorar con un archivo `.trivyignore`:

```bash
# .trivyignore
CVE-2023-12345  # no usamos la función afectada (ver comentario en issue #456)
CVE-2023-67890  # solo afecta a Windows, nuestro runtime es Linux
```

> Documentar siempre el motivo del ignore. Sin contexto, un `.trivyignore` sin comentarios es equivalente a desactivar el escáner.

## 3.8 Resumen

| Acción | Comando |
|--------|---------|
| Escanear imagen | `trivy image nombre:tag` |
| Solo CRITICAL/HIGH | `trivy image --severity CRITICAL,HIGH nombre:tag` |
| Escanear filesystem | `trivy fs .` |
| Integración CI | `aquasecurity/trivy-action@master` |
| Reportes en GitHub | Formato SARIF + `upload-sarif` |
| Ignorar CVEs | `.trivyignore` |

Continuar con [04 - Dependency Scanning con Dependabot](../04-DependencyScanning/1-Dependabot.md)
