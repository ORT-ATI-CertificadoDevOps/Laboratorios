# Plan de Trabajo - Obligatorio Tero (DevOps)

> **Entrega:** 07/12/26 hasta las 21:00 hs en gestion.ort.edu.uy (máx. 40MB, zip/rar/pdf) | **Defensa:** Semanas del 08/12 al 18/12
> **Repositorio de la app:** [Tero](COMPLETAR_URL)
> **Grupos:** hasta 3 personas del mismo dictado

---

## Semana 1: Planificación y configuración inicial

1. **Planificación del proyecto**
   - Inscribirse al obligatorio y registrar el equipo en el [Listado de Alumnos DevOps - 2026 (Agosto)](https://docs.google.com/spreadsheets/d/12axQTgXXWl5j1zkNsuz_BJ3p-1hCdKBjWuRe148kuME/edit?gid=461278557#gid=461278557)
   - Estudio detallado de la letra, incluyendo las observaciones preliminares del auditor
   - Creación del tablero Kanban o Scrum (documentar **primer estado** con screenshot)
   - El tablero debe reflejar **la totalidad** de las tareas: infraestructura, seguridad, testing y observabilidad
   - Decidir roles y responsabilidades — en la defensa **todos** deben poder ejecutar y explicar cualquier parte

2. **Análisis del repositorio Tero**
   - Clonar y explorar el repositorio de `Tero`
   - Analizar la arquitectura de microservicios y sus dependencias (base de datos, Redis)
   - Levantar la solución tal como funciona hoy
   - Contrastar el estado del repositorio con las observaciones preliminares del auditor
   - Identificar otras deudas técnicas o de seguridad que el auditor no haya mencionado

3. **Investigación y selección de herramientas**
   - Herramienta de gestión de repositorios Git (GitHub, GitLab, Bitbucket)
   - Herramienta de CI/CD (GitHub Actions, GitLab CI/CD, Jenkins, CircleCI)
   - Herramienta de análisis de código estático (SonarQube, ESLint, Semgrep o similar)
   - Herramienta SAST (Semgrep, Bandit, GoSec, NodeJsScan, SonarQube)
   - Herramienta de SCA para dependencias (Trivy filesystem, Snyk, OWASP Dependency-Check)
   - Herramienta de escaneo de imágenes (Trivy image, Anchore Grype, Clair)
   - Detector de secretos (git-secrets, detect-secrets, TruffleHog, Gitleaks)
   - Herramienta de testing (Postman/Newman, REST-assured, Pact, Selenium, k6, JMeter, Locust)

4. **Configuración de repositorios y estrategia Git**
   - Crear estructura de repositorios
   - Definir y **justificar** la estrategia de ramificación (Git Flow, Trunk-Based con feature flags o GitHub Flow) en función del equipo y del contexto del proyecto
   - Configurar protección de ramas: ninguna feature se fusiona a main/develop sin aprobación de **al menos un revisor distinto al autor**

5. **Exploración de la nube**
   - Familiarización con la consola del proveedor elegido
   - Exploración de los servicios a utilizar
   - Creación de cuentas de servicio y configuración de permisos

---

## Semana 2: Infraestructura como código

1. **Desarrollo de infraestructura como código (Terraform)**
   - **Toda** la infraestructura en Terraform, salvo el bucket del estado
   - Configuración de **backend remoto** para el estado (S3, Terraform Cloud u otro)
   - Estructura **modular**: al menos un módulo reutilizable
   - **Archivos `.tfvars` diferenciados** por ambiente: Dev, Test y Prod
   - Implementar la red
   - Outputs relevantes documentados con descripción
   - Definir la estrategia de **manejo seguro de secretos** (sin valores sensibles en el estado sin cifrar)

2. **Configuración inicial de CI**
   - Pipeline básico para validación de Terraform
   - Proceso de Pull Request con revisión de código (se necesitan **mínimo 2 PRs documentados** con comentarios a lo largo del proyecto)
   - Despliegue automatizado de la infraestructura base

3. **Documentación inicial**
   - Actualización del tablero Kanban/Scrum
   - Crear `README.md` base con estructura, pre-requisitos y variables de entorno

---

## Semana 3: Containerización y CI con seguridad

1. **Containerización de Tero**
   - Optimizar la construcción de las imágenes aplicando **buenas prácticas**, por ejemplo:
     - Imágenes base mínimas (distroless, alpine o similares)
     - Construcción **multi-stage**
     - Usuario **no-root** dentro del contenedor
     - Archivo `.dockerignore` adecuado
   - Pruebas de construcción de imágenes localmente
   - Pruebas de integración local

2. **Configuración de CI con DevSecOps integrado**
   - Pipeline con las etapas mínimas:
     - Build / compilación de artefactos
     - Ejecución de pruebas automatizadas
     - Análisis de calidad de código
     - SAST (al menos una herramienta)
     - SCA — quality gate que bloquee ante vulnerabilidades **CRITICAL o HIGH sin parche disponible**
     - **Detección de secretos expuestos** — fallar el build si se detectan credenciales
     - Escaneo de cada imagen **antes** de publicarla en el registry
     - Publicación de imagen en un container registry
     - Despliegue al ambiente correspondiente
   - Pipeline completamente definido como código y reproducible
   - Verificar que **ningún secreto** quede hardcodeado en el código fuente ni en los archivos de configuración del repositorio

3. **Primer PR documentado**
   - Al menos un PR con comentarios de revisión entre integrantes del equipo

---

## Semana 4: Orquestación, CD y Testing

1. **Orquestación de contenedores**
   - Despliegue en un servicio de orquestación en la nube (ECS, EKS o equivalente en Azure/GCP)
   - Configuración de los servicios de Tero en la plataforma elegida

2. **Configuración de entornos múltiples**
   - Despliegue de infraestructura para el ambiente **Dev**
   - Pipeline de CD completo para Dev
   - Definir **quality gates** entre ambientes (umbrales de calidad y seguridad)
   - Ningún artefacto se promueve al siguiente ambiente si no cumple los umbrales

3. **Testing y Calidad**
   - Implementar **al menos uno** de estos tipos (alcanza con un par de tests):
     - Pruebas funcionales / de integración entre microservicios
     - Pruebas de carga y rendimiento
   - Integrar la herramienta de análisis de código estático en el pipeline de CI
   - Definir umbrales de calidad que actúen como quality gate
   - Comenzar a recolectar resultados para el informe

4. **Segundo PR documentado**
   - Al menos un segundo PR con comentarios de revisión entre integrantes

5. **Documentar segundo estado del tablero** (screenshot de mitad de proyecto)

---

## Semana 5: Producción, Serverless y Observabilidad

1. **Ambientes Test y Prod**
   - Despliegue de **Test** y **Prod** con Terraform usando los `.tfvars` diferenciados
   - Pipelines completos para todos los ambientes
   - Controles entre ambientes

2. **Servicio Serverless (elegir al menos uno)**
   - **AWS Lambda** para automatizaciones y/o procesamiento de eventos
   - **API Gateway** para gestión de acceso y seguridad a APIs
   - Integrarlo con el resto de la arquitectura y documentar su propósito
   - Se valora especialmente que el propósito esté relacionado con **seguridad u observabilidad**

3. **Observabilidad**
   - Recolectar **métricas de infraestructura** (CPU, memoria, red) o **de aplicación** (latencia, tasa de errores, throughput)
   - Centralizar los **logs** de todos los servicios en una plataforma única, con formato estructurado que permita búsquedas y filtros
   - Configurar al menos **1 dashboard** con métricas relevantes para la operación
   - Establecer al menos **2 alertas** (disponibilidad, rendimiento, seguridad y/o uso de recursos), cada una con condición de disparo y procedimiento básico de respuesta

---

## Semana 6: Documentación, entrega y preparación de la defensa

1. **Documentación técnica completa**
   - Finalizar `README.md` con:
     - Instrucciones de despliegue paso a paso
     - Configuración de variables de entorno
     - Pre-requisitos
   - Generar los **diagramas**:
     - Arquitectura general del sistema
     - Flujo completo del pipeline CI/CD con las etapas de seguridad integradas
     - Estrategia de Git (branching model) adoptada
   - Documentar **decisiones de diseño relevantes** (ADRs o formato libre) y **lecciones aprendidas**
   - **Informe de testing y análisis estático**: un párrafo que interprete los resultados, hallazgos significativos y recomendaciones de mejora
   - Reportar hallazgos de seguridad y documentar remediaciones o excepciones justificadas
   - **Uso de IA generativa**: indicar las herramientas utilizadas y el contexto de uso

2. **Pruebas finales e iteraciones**
   - Pruebas end-to-end del sistema completo
   - Verificar que los quality gates bloqueen lo que tienen que bloquear
   - Verificar que cada observación del auditor tenga **evidencia concreta** de su resolución
   - Verificación de todos los puntos de la rúbrica
   - **Documentar tercer estado del tablero** (screenshot de cierre)

3. **Preparación para la defensa (~25 min, con el "auditor")**
   - La defensa es **obligatoria y eliminatoria**, y tiene tres partes: **Hacer** (10 min), **Resolver** (8 min) y **Explicar** (7 min) — ver detalle en la letra
   - El auditor decide quién responde o ejecuta en cada parte: **todos** deben poder hacerlo
   - Ensayar rotando quién ejecuta y quién explica

4. **Entrega**
   - Preparar un único archivo `.zip` o `.rar` con todo el contenido (repositorio git, documentación, imágenes, etc.) — documentos de texto en PDF dentro del archivo — **máximo 40MB**
   - **Entrega final en gestion.ort.edu.uy antes de las 21:00 hs del 07/12/26**
   - Ante problemas con la inscripción o la entrega, contactar a Coordinación **antes de las 20:00 hs** del día de la entrega

---

## Rúbrica de evaluación (50 puntos)

| Criterio | Puntos |
|---|---|
| Implementación general (resuelve el problema de forma completa, es innovadora y demuestra entendimiento de DevOps) | 6 |
| Gestión de proyecto (uso consistente de la metodología y progreso bien documentado) | 4 |
| Documentación técnica (completa, bien estructurada, técnicamente precisa, en Markdown o el formato entregado) | 4 |
| Containerización y despliegue (buenas prácticas + orquestación en la nube) | 4 |
| Gestión de código (repositorios bien organizados, separación de componentes) | 4 |
| Estrategias de ramificación (trabajo colaborativo, uso correcto de la estrategia y PRs) | 4 |
| Implementación serverless (propósito claro y bien integrado) | 4 |
| Infraestructura como código (modular y parametrizada) | 4 |
| Análisis de código estático (informe con hallazgos significativos y recomendaciones accionables) | 4 |
| Testing y Calidad (tipo de testing integrado y criterios de calidad aplicados) | 4 |
| Seguridad (herramientas integradas al SDLC y criterios aplicados) | 4 |
| Observabilidad (señales recolectadas, visualizaciones y alertas) | 4 |
| **Total** | **50** |

---

## Recomendaciones de trabajo

- **Seguimiento constante**: Mantener el tablero actualizado y documentar el estado en al menos **3 momentos** (inicio, mitad, cierre)
- **Documentación progresiva**: Documentar decisiones y configuraciones mientras se avanza; el auditor evalúa que otra persona pueda **entender, repetir y mantener** la solución
- **Enfoque de MVP**: Primero versiones mínimas funcionales, luego mejorar
- **Seguridad desde el día 1**: No dejar SCA, SAST y detección de secretos para el final
- **Pruebas continuas**: No dejar todas las pruebas para el final
- **Evidencia, no maquillaje**: Guardar evidencia concreta de cada control que se implemente
- **Revisar rúbrica**: Verificar periódicamente el cumplimiento de todos los puntos
- **PRs obligatorios**: Documentar mínimo **2 Pull Requests** con comentarios de revisión entre integrantes
- **Uso de IA**: Citar herramientas y contexto de uso; cada integrante es responsable de entender y poder defender todo lo entregado
