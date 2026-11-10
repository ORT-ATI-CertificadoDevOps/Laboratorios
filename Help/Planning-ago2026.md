# Plan de Trabajo - Obligatorio Tero (DevOps)

> **Entrega:** 07/12/26 hasta las 21:00 hs en gestion.ort.edu.uy (máx. 40MB, zip/rar/pdf) | **Defensa:** Semanas del 08/12 al 18/12
> **Repositorio de la app:** [Tero](COMPLETAR_URL)
> **Grupos:** hasta 3 personas del mismo dictado

---

## Contexto: qué tiene que poder demostrar Tero ante el auditor

Tero es un marketplace con **seis microservicios** (tienda, carrito, checkout, órdenes, panel de administración, etc.), una base de datos y Redis. Hoy solo corre con `docker compose` en las máquinas del equipo de desarrollo. El objetivo no es "pasar la auditoría con maquillaje": el equipo de Tero tiene que poder **entender, repetir y mantener** lo que ustedes construyan.

Observaciones preliminares del auditor y dónde se resuelve cada una en este plan:

| Observación del auditor | Dónde se resuelve |
|---|---|
| Contraseñas de la base de datos y credenciales del panel de admin en archivos del repositorio | Semana 1 (detección) · Semana 3 (remediación + detector de secretos en CI) |
| Imágenes construidas en un solo paso, sobre imágenes base completas, corriendo como root | Semana 3 (containerización) |
| Base de datos y Redis publicados en puertos de la máquina anfitriona | Semana 2 (red privada / security groups) · Semana 3 (compose) · Semana 4 (orquestación) |
| Sin pruebas automatizadas, ni análisis de código ni de dependencias | Semana 3 (SAST, SCA, escaneo de imágenes) · Semana 4 (testing y quality gates) |
| No se sabe qué versión corre ni se puede repetir un despliegue | Semana 2 (IaC) · Semana 3 (versionado de imágenes) · Semana 4–5 (CD reproducible) |
| Algunos servicios exponen métricas pero nadie las recolecta | Semana 5 (observabilidad) |

---

## Semana 1: Planificación y configuración inicial

1. **Planificación del proyecto**
   - Inscribirse al obligatorio y registrar el equipo en el [Listado de Alumnos DevOps - 2026 (Agosto)](COMPLETAR_URL)
   - Estudio detallado de la letra y de las observaciones del auditor
   - Creación del tablero Kanban o Scrum (documentar **primer estado** con screenshot)
   - El tablero debe reflejar **la totalidad** de las tareas: infraestructura, seguridad, testing y observabilidad
   - Decidir roles y responsabilidades — teniendo en cuenta que en la defensa **todos** deben poder ejecutar y explicar cualquier parte

2. **Análisis del repositorio Tero**
   - Clonar y explorar el repositorio de `Tero`
   - Relevar los seis microservicios: lenguaje, dependencias, puertos, variables de entorno, dependencia de base de datos/Redis
   - Levantar la solución con el `docker compose` actual para entender cómo arranca hoy
   - Identificar Dockerfiles existentes y su estado (single-stage, imagen base completa, usuario root)
   - Detectar **secretos hardcodeados** (contraseñas de BD, credenciales del panel de admin, etc.)
   - Identificar qué servicios **ya exponen métricas** y en qué endpoint/formato

3. **Investigación y selección de herramientas**
   - Herramienta de gestión de repositorios Git (GitHub, GitLab, Bitbucket)
   - Herramienta de CI/CD (GitHub Actions, GitLab CI/CD, Jenkins, CircleCI)
   - Herramienta de análisis de código estático / calidad (SonarQube, ESLint, Semgrep o similar)
   - Herramienta SAST (Semgrep, Bandit, GoSec, NodeJsScan, SonarQube) — según los lenguajes de los servicios
   - Herramienta de SCA para dependencias (Trivy filesystem, Snyk, OWASP Dependency-Check)
   - Herramienta de escaneo de imágenes (Trivy image, Anchore Grype, Clair)
   - Detector de secretos (git-secrets, detect-secrets, TruffleHog, Gitleaks)
   - Herramienta de testing (Postman/Newman, REST-assured, Pact, Selenium, k6, JMeter, Locust)

4. **Configuración de repositorios y estrategia Git**
   - Crear estructura de repositorios (app, infraestructura, pipelines) con separación clara de componentes
   - Definir y **justificar** la estrategia de ramificación (Git Flow, Trunk-Based con feature flags o GitHub Flow) en función del equipo y del contexto del proyecto
   - Configurar protección de ramas: ninguna feature se fusiona a main/develop sin aprobación de **al menos un revisor distinto al autor**

5. **Exploración de la nube (AWS u otra)**
   - Familiarización con la consola
   - Exploración de servicios a utilizar: VPC, ECS/EKS, ECR, S3, Lambda, API Gateway, CloudWatch, Secrets Manager / SSM Parameter Store
   - Creación de cuentas de servicio y configuración de permisos IAM (mínimo privilegio)

---

## Semana 2: Infraestructura como código

1. **Desarrollo de infraestructura como código (Terraform)**
   - **Toda** la infraestructura en Terraform, salvo el bucket del estado
   - Configuración de **backend remoto** (S3, Terraform Cloud u otro)
   - Estructura **modular**: al menos un módulo reutilizable (red, contenedores o base de datos)
   - **Archivos `.tfvars` diferenciados** por ambiente: Dev, Test y Prod
   - Módulo de red (VPC, subnets públicas/privadas, security groups) — la base de datos y Redis **no** deben quedar expuestos públicamente
   - Outputs relevantes documentados con `description`
   - Estrategia para **manejo seguro de secretos**: no almacenar valores sensibles en el estado sin cifrar

2. **Configuración inicial de CI**
   - Pipeline básico para validación de Terraform (`fmt`, `validate`, `plan`)
   - Proceso de Pull Request con revisión de código (se necesitan **mínimo 2 PRs documentados** con comentarios a lo largo del proyecto)
   - Despliegue automatizado de la infraestructura base

3. **Documentación inicial**
   - Actualización del tablero Kanban/Scrum
   - Crear `README.md` base con estructura, pre-requisitos y variables de entorno

---

## Semana 3: Containerización y CI con seguridad

1. **Containerización de Tero**
   - Rehacer los Dockerfiles de los seis servicios aplicando **buenas prácticas**:
     - Imágenes base mínimas (distroless, alpine o similares)
     - Construcción **multi-stage** para reducir el tamaño final
     - Usuario **no-root** dentro del contenedor
     - Archivo `.dockerignore` adecuado
   - Ajustar el `docker-compose` local: base de datos y Redis sin puertos publicados en el host (solo red interna)
   - Comparar tamaños de imagen antes/después (sirve como evidencia para el auditor)

2. **Remediación de secretos**
   - Sacar del repositorio las contraseñas de la base de datos y las credenciales del panel de admin
   - Mover los secretos a variables de entorno / gestor de secretos; `.env` en `.gitignore` y un `.env.example` sin valores reales
   - **Rotar** las credenciales que estuvieron expuestas (quedaron en el historial de Git)

3. **Versionado y trazabilidad**
   - Etiquetar las imágenes con una versión trazable (por ejemplo el SHA del commit y/o versión semántica), no solo `latest`
   - Que sea posible responder "¿qué versión está corriendo en cada ambiente?" y volver a una versión anterior

4. **Configuración de CI con DevSecOps integrado**
   - Pipeline con las etapas mínimas:
     - Build / compilación de artefactos
     - Ejecución de pruebas automatizadas
     - Análisis de calidad de código
     - SAST (al menos una herramienta)
     - SCA — **fallar ante CVEs CRITICAL o HIGH sin parche disponible**
     - **Detección de secretos expuestos** — fallar el build si se detectan credenciales
     - Escaneo de cada imagen **antes** de publicarla en el registry
     - Publicación de imagen en registry (ECR, Docker Hub, GHCR u otro)
     - Despliegue al ambiente correspondiente
   - Pipeline completamente definido como código (YAML o formato declarativo) y reproducible

5. **Primer PR documentado**
   - Al menos un PR con comentarios de revisión entre integrantes del equipo

---

## Semana 4: Orquestación, CD y Testing

1. **Orquestación de contenedores**
   - Despliegue en **ECS o EKS** (o equivalente en Azure/GCP)
   - Servicios y tareas para cada microservicio de Tero, con base de datos y Redis en red privada
   - Balanceo de carga y escalado automático

2. **Configuración de entornos múltiples**
   - Despliegue de infraestructura para el ambiente **Dev**
   - Pipeline de CD completo para Dev
   - Definir **quality gates** entre ambientes (umbrales de calidad y seguridad)
   - Ningún artefacto se promueve al siguiente ambiente si no cumple los umbrales

3. **Testing y Calidad**
   - Implementar **al menos uno** de estos tipos (alcanza con un par de tests):
     - Pruebas funcionales / de integración entre microservicios (Postman/Newman, REST-assured, Pact, Selenium, etc.)
     - Pruebas de carga y rendimiento (k6, JMeter, Locust, etc.)
   - Integrar la herramienta de análisis de código estático en el pipeline de CI
   - Definir umbrales de calidad (coverage mínimo, máximo de code smells, etc.) como quality gate
   - Comenzar a recolectar resultados para el informe

4. **Segundo PR documentado**
   - Al menos un segundo PR con comentarios de revisión entre integrantes

5. **Documentar segundo estado del tablero** (screenshot de mitad de proyecto)

---

## Semana 5: Producción, Serverless y Observabilidad

1. **Ambientes Test y Prod**
   - Despliegue de **Test** y **Prod** con Terraform usando los `.tfvars` diferenciados
   - Pipelines completos para todos los ambientes
   - Aprobaciones y controles de seguridad entre ambientes
   - Verificar configuraciones de alta disponibilidad

2. **Servicio Serverless (elegir al menos uno)**
   - **AWS Lambda**: automatizaciones o procesamiento de eventos (alertas automáticas, análisis de logs de seguridad, notificaciones de vulnerabilidades, rotación de secretos, etc.)
   - **API Gateway**: gestión de acceso y seguridad a APIs (rate limiting, autenticación, WAF básico)
   - Integrarlo con el resto de la arquitectura y documentar su propósito
   - Se valora especialmente que el propósito esté relacionado con **seguridad u observabilidad**

3. **Observabilidad**
   - Recolectar **métricas de infraestructura** (CPU, memoria, red) o **de aplicación** (latencia, tasa de errores, throughput) — aprovechar los servicios que **ya exponen métricas**
   - Centralizar los **logs** de todos los servicios en una plataforma única, con formato estructurado que permita búsquedas y filtros
   - Configurar al menos **1 dashboard** con métricas relevantes para la operación
   - Establecer al menos **2 alertas** (disponibilidad, rendimiento, seguridad y/o uso de recursos), cada una con:
     - Condición de disparo (trigger)
     - Procedimiento básico de respuesta (runbook)
   - Herramientas sugeridas: CloudWatch, Prometheus, Grafana, Loki, OpenTelemetry

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
   - Reportar hallazgos de seguridad (SAST, SCA, escaneo de imágenes) y documentar remediaciones o excepciones justificadas
   - **Uso de IA generativa**: indicar las herramientas utilizadas y el contexto de uso (código, documentación, corrección de errores, etc.)

2. **Pruebas finales e iteraciones**
   - Pruebas end-to-end del sistema completo
   - Verificar que los quality gates **bloqueen** artefactos con vulnerabilidades críticas
   - Revisar cada observación del auditor y confirmar que hay **evidencia concreta** de su resolución
   - Verificación de todos los puntos de la rúbrica
   - **Documentar tercer estado del tablero** (screenshot de cierre)

3. **Preparación para la defensa (~25 min, con el "auditor")**

   La defensa es **obligatoria y eliminatoria**. El auditor decide quién responde o ejecuta en cada parte, así que **todos** deben poder hacerlo. Se puede consultar la documentación propia y la oficial de las herramientas.

   - **Hacer (10 min)** — practicar tareas breves sobre la propia solución, por ejemplo:
     - Volver a una versión anterior (rollback)
     - Rotar una credencial
     - Interpretar un cambio de infraestructura (por ejemplo, leer un `terraform plan`)
   - **Resolver (8 min)** — practicar diagnosticar en voz alta:
     - Un pipeline que falló (¿en qué etapa? ¿por qué? ¿qué harían?)
     - Una alerta disparada (seguir el procedimiento de respuesta documentado)
   - **Explicar (7 min)** — cada integrante debe poder justificar cualquier decisión y responder "¿qué pasaría si cambiaran las condiciones?"
   - Hacer ensayos rotando quién ejecuta cada parte

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
- **Evidencia, no maquillaje**: Para cada observación del auditor, guardar evidencia concreta (capturas, logs de pipeline, PRs, outputs)
- **Practicar la defensa**: Rollback, rotación de credenciales y diagnóstico de fallas tienen que salir con fluidez para cualquier integrante
- **Revisar rúbrica**: Verificar periódicamente el cumplimiento de todos los puntos
- **PRs obligatorios**: Documentar mínimo **2 Pull Requests** con comentarios de revisión entre integrantes
- **Uso de IA**: Citar herramientas y contexto de uso; cada integrante es responsable de entender y poder defender todo lo entregado
