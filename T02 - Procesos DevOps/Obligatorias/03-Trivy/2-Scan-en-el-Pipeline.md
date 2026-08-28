# Trivy — Scan en el Pipeline

Partimos del pipeline armado en `02-GitHub-Actions` (`.github/workflows/pipeline.yml`), que tiene un job `build` y un job de push a Docker Hub. Vamos a insertar un job `scan` con Trivy entre medio.

```
build → scan (Trivy) → push-artifact
```

## 2.1 Agregar el job de scan

[Trivy](https://trivy.dev) escanea los paquetes instalados dentro de la imagen Docker final — incluyendo la imagen base — buscando CVEs conocidos. No requiere cuenta ni secrets: se ejecuta con la action oficial de Aqua Security.

Agregar el job `scan` en `.github/workflows/pipeline.yml`, después de `build`. Actualizar el `needs` del job de push para que dependa de `scan`:

```yaml
  scan:
    name: Scan de seguridad (Trivy)
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Checkout código
        uses: actions/checkout@v4

      - name: Build imagen para escaneo
        uses: docker/build-push-action@v5
        with:
          context: .
          push: false
          load: true
          tags: lab-github-actions:${{ github.sha }}

      - name: Escaneo Trivy
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: lab-github-actions:${{ github.sha }}
          format: table
          exit-code: '1'
          ignore-unfixed: true
          vuln-type: 'os,library'
          severity: 'CRITICAL,HIGH'
```

> El job `scan` reconstruye la imagen localmente con `load: true` para que quede disponible en el runner, y falla (`exit-code: '1'`) si encuentra vulnerabilidades `CRITICAL` o `HIGH` con fix disponible (`ignore-unfixed: true`).

```bash
git add .github/workflows/pipeline.yml
git commit -m "ci: add Trivy security scan to pipeline"
git push origin main
```

## 2.2 Trivy va a fallar — y eso es lo esperado

Al hacer push, el job `scan` va a fallar. `nginx:alpine` es una tag flotante que acumula CVEs conocidos con fix disponible, exactamente lo que Trivy está configurado para bloquear.

En la pestaña **Actions** se va a ver el job `scan` en rojo, con una tabla similar a esta en los logs:

```
2024-XX-XX nginx:alpine (alpine 3.x)
===========================================
Total: 3 (HIGH: 3, CRITICAL: 0)

┌──────────┬────────────────┬──────────┬──────────────────┬───────────────┐
│ Library  │ Vulnerability  │ Severity │ Installed Version│ Fixed Version │
├──────────┼────────────────┼──────────┼──────────────────┼───────────────┤
│ libssl3  │ CVE-XXXX-XXXXX │ HIGH     │ 3.x.x-rX         │ 3.x.x-rX+1   │
└──────────┴────────────────┴──────────┴──────────────────┴───────────────┘
```

El job de push queda cancelado automáticamente — la imagen nunca llega al registry. El gate está funcionando.

## 2.3 Corregir la imagen base

La solución es reemplazar `nginx:alpine` por una imagen diseñada para pasar scanners de seguridad. [Chainguard](https://edu.chainguard.dev/chainguard/chainguard-images/getting-started/nginx/) mantiene imágenes con cero CVEs conocidos, actualizadas continuamente ante nuevas vulnerabilidades.

En el `Dockerfile`, cambiar la imagen base:

```dockerfile
FROM cgr.dev/chainguard/nginx
```

El path de los archivos estáticos es el mismo (`/usr/share/nginx/html/`), por lo que los `COPY` no cambian.

```bash
git add Dockerfile
git commit -m "fix: use Chainguard nginx image to pass Trivy scan"
git push origin main
```

Esta vez el job `scan` pasa y el pipeline continúa hasta el push de la imagen.

> **Alternativas a cambiar la imagen base:** usar un archivo `.trivyignore` para CVEs revisados y aceptados, o bajar el umbral de `severity`. Cambiar la base es la opción preferida cuando existe una imagen equivalente sin CVEs.

## Próximos pasos

Con la imagen limpia y publicándose, el siguiente módulo agrega el **quality gate** sobre el código fuente: [04-SonarCloud](/T02%20-%20Procesos%20DevOps/Obligatorias/04-SonarCloud/1-Prerrequisitos).

---

## Ejercicio Integrador — Fase 4: Security Gate con Trivy

Ya tenés el portfolio desplegándose automáticamente a GitHub Pages (Fase 3). Ahora vas a agregar **Trivy como security gate**: la imagen Docker del portfolio se escanea antes del deploy, y si tiene CVEs críticos o altos con fix disponible, el pipeline se detiene.

La versión del workflow con el security gate está en la sección [Fase 4](/Ejercicio-Integrador#fase-4--trivy-security-gate) del Ejercicio Integrador.

### ¿Por qué escanear si el portfolio es HTML estático?

El portfolio corre sobre `nginx:alpine`. Aunque el código fuente sea solo HTML/CSS/JS, la imagen base trae dependencias del sistema operativo que pueden tener CVEs conocidos. Trivy escanea la imagen completa — imagen base incluida — no solo el código propio.

### Verificar el resultado

Después del push, en la pestaña **Actions** del repositorio vas a ver el job `scan` ejecutarse primero. Si la imagen base de nginx está actualizada, Trivy debería pasar. Para probar el bloqueo, podés cambiar temporalmente `severity` a `LOW` y verificar que el deploy queda cancelado.

> **Próxima fase:** En el laboratorio de SonarCloud vas a agregar un quality gate que analiza el código fuente del portfolio antes del deploy.
