# GitHub Actions — Prerrequisitos

## ¿Qué es GitHub Actions?

GitHub Actions es la plataforma de CI/CD nativa de GitHub. Permite automatizar el pipeline de DevOps directamente en el repositorio: compilar código, ejecutar tests, analizar calidad y publicar artefactos, todo disparado por eventos de Git (push, pull request, tags).

En términos del lifecycle visto en clase:

```
Code  →  Build  →  Test  →  Artifact  →  Deploy
push     docker    sonar     docker       ...
         build     scan      push
```

## Conceptos clave

| Concepto | Descripción |
|----------|-------------|
| **Workflow** | Proceso automatizado definido en un archivo YAML |
| **Job** | Unidad de ejecución dentro de un workflow. Corre en su propio runner |
| **Step** | Comando o action dentro de un job |
| **Runner** | Servidor donde se ejecuta el job (ubuntu-latest, windows, macos) |
| **Trigger** | Evento que dispara el workflow (`push`, `pull_request`, `workflow_dispatch`, etc.) |
| **Secret** | Variable cifrada almacenada en GitHub, accesible en el workflow |
| **Action** | Paso reutilizable publicado en el Marketplace de GitHub |

## Estructura de un workflow

Los workflows viven en `.github/workflows/` dentro del repositorio. Cada archivo `.yml` es un workflow independiente.

```yaml
name: Mi Pipeline

on:                          # trigger
  push:
    branches: [main]

jobs:
  build:                     # nombre del job
    runs-on: ubuntu-latest   # runner
    steps:
      - uses: actions/checkout@v4          # action del marketplace
      - name: Mi primer step
        run: echo "Hola desde GitHub Actions"   # comando shell
```

## Prerrequisitos

Para realizar este laboratorio necesitás:

- Cuenta en [GitHub](https://github.com/)
- Cuenta en [Docker Hub](https://hub.docker.com/) (gratuita)
- Cuenta en [SonarCloud](https://sonarcloud.io/) (gratuita con repositorio público)
- Haber completado los labs **01-Docker** y **02-SonarCloud** de este topic

## Próximos pasos

Continuar con [02 - Primer Workflow](02-Primer-Workflow.md)
