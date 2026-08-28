# Trivy — Prerrequisitos

## ¿Qué es Trivy?

[Trivy](https://trivy.dev) es un escáner de vulnerabilidades open source desarrollado por Aqua Security. Escanea los paquetes instalados dentro de una imagen Docker final — incluyendo la imagen base — buscando CVEs conocidos.

A diferencia de un análisis de código fuente (que revisa lo que vos escribís), Trivy revisa **lo que traés puesto**: la distro de la imagen base, las librerías del sistema, las dependencias empaquetadas.

En términos del pipeline de T02, Trivy es la etapa de **Scan de seguridad**, entre el build de la imagen y su publicación en el registry:

```
push → Build imagen (Docker) → Scan (Trivy) → Push a registry (Docker Hub)
```

## ¿Por qué acá?

En el lab anterior (`02-GitHub-Actions`) armaste un pipeline que buildea una imagen y la publica. Publicar una imagen con CVEs críticos conocidos es un problema de seguridad. Trivy se agrega como **gate**: si la imagen tiene vulnerabilidades `CRITICAL` o `HIGH` con fix disponible, el pipeline se detiene y la imagen nunca llega al registry.

## Prerrequisitos

- Haber completado el lab **02-GitHub-Actions** (pipeline con build y push de imagen funcionando)
- Cuenta en [GitHub](https://github.com/) y [Docker Hub](https://hub.docker.com/)

Trivy **no requiere cuenta ni secrets**: se ejecuta directo en el runner con la action oficial de Aqua Security.

> En **T09 - DevSecOps** se retoma Trivy en profundidad (formatos de reporte, SBOM, integración con el Security tab). Acá el foco es únicamente cómo se agrega una etapa de scan al pipeline.

## Próximos pasos

Continuar con [2 - Scan en el Pipeline](2-Scan-en-el-Pipeline.md)
