# GitHub Actions Avanzado — Prerrequisitos

## Punto de partida

Este laboratorio extiende el pipeline construido en **T02 — Lab 03 (GitHub Actions)**. Antes de continuar, asegurarse de tener:

- El repositorio `lab-github-actions-t02` funcionando con el pipeline Build → Test → Push
- Cuenta en [Docker Hub](https://hub.docker.com/)
- No se requieren cuentas adicionales para el scanner de secrets (Gitleaks corre sin autenticación)
- Cuenta en [AWS Academy](https://awsacademy.instructure.com/) activa (para el lab de deploy)

## ¿Qué vamos a agregar?

En T02 construimos el pipeline de CI (integración continua). En este lab agregamos **CD (entrega continua)**:

```
T02:  push → Build → Test → Push artifact

T03:  push → Build → Test → Push artifact → Deploy dev
      PR merge to staging → Deploy staging
      PR merge to main   → Deploy producción (con aprobación)
```

Los conceptos nuevos que se cubren:

| Concepto | Descripción |
|----------|-------------|
| **Variables y contextos** | Pasar datos entre jobs y steps; variables por nivel |
| **Artefactos** | Archivos generados por el pipeline, descargables desde la UI |
| **Branch Protection** | El código solo entra a `main` si el pipeline pasó primero |
| **Environments** | Ambientes con reglas de protección y secrets propios |
| **Required reviewers** | Aprobación manual antes de deployar a prod |
| **Reusable workflows** | Workflows que se llaman desde otros workflows |
| **Cache** | Acelerar builds reutilizando dependencias entre runs |
| **Matrix** | Ejecutar el mismo job en múltiples configuraciones |

## Próximos pasos

Continuar con [02 - Variables y Artefactos](02-Variables-y-Artefactos.md)
