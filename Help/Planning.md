# Plan de Trabajo - Obligatorio RetailStore (DevOps)

> **Entrega:** 29/06/26 hasta las 21:00 hs | **Defensa:** Semanas del 30/06 al 09/07
> **Repositorio de la app:** [RetailStore](https://github.com/ORT-ATI-CertificadoDevOps/RetailStore)

---

## Semana 1: Planificación y configuración inicial

1. **Planificación del proyecto**
   - Registrar equipo en el [Listado de Alumnos DevOps - 2026 (Marzo)](https://docs.google.com/spreadsheets/d/1R5DnpTsVRxu8SWk2n5g4NykPXEFjypcprOLNdn3WHcg)
   - Estudio detallado de los requerimientos del obligatorio
   - Creación del tablero Kanban o Scrum (documentar **primer estado** con screenshot)
   - El tablero debe incluir tareas de infraestructura, seguridad, testing y observabilidad
   - Decidir roles y responsabilidades dentro del equipo

2. **Análisis del repositorio RetailStore**
   - Clonar y explorar el repositorio de `RetailStore`
   - Analizar arquitectura de microservicios (catálogo, carrito, pagos, autenticación, notificaciones)
   - Identificar Dockerfiles existentes y estado actual de la containerización
   - Detectar secretos hardcodeados u otras deudas técnicas de seguridad

3. **Investigación y selección de herramientas**
   - Herramienta de gestión de repositorios Git (GitHub, GitLab, Bitbucket)
   - Herramienta de CI/CD (GitHub Actions, GitLab CI/CD, Jenkins, CircleCI)
   - Herramienta de análisis de código estático — SAST (SonarQube, Semgrep, Bandit, ESLint, etc.)
   - Herramienta de SCA para dependencias (Trivy filesystem, Snyk, OWASP Dependency-Check)
   - Herramienta de escaneo de imágenes (Trivy image, Anchore Grype, Clair)
   - Detector de secretos (git-secrets, detect-secrets, TruffleHog, Gitleaks)
   - Herramienta de testing (Postman/Newman, k6, JMeter, Locust, Selenium, etc.)

4. **Configuración de repositorios y estrategia Git**
   - Crear estructura de repositorios (app, infraestructura)
   - Definir y **justificar** estrategia de ramificación: Git Flow, Trunk-Based o GitHub Flow
   - Configurar protección de ramas (aprobación mínima de un revisor para fusionar a main/develop)

5. **Exploración de AWS**
   - Familiarización con la consola de AWS
   - Exploración de servicios a utilizar: EC2, VPC, S3, ECS/EKS, ECR, Lambda, API Gateway, CloudWatch
   - Creación de cuentas de servicio y configuración de permisos IAM

---

## Semana 2: Infraestructura como código

1. **Desarrollo de infraestructura como código (Terraform)**
   - Configuración inicial de Terraform con **backend remoto** (S3 o Terraform Cloud)
   - Definir estructura **modular**: al menos un módulo reutilizable (red, contenedores o base de datos)
   - Crear **archivos `.tfvars` diferenciados** por ambiente: Dev, Test y Prod
   - Implementar módulos de red (VPC, subnets, security groups)
   - Documentar outputs relevantes con descripción
   - Definir estrategia para **manejo seguro de secretos** (sin valores sensibles en el estado sin cifrar)

2. **Configuración inicial de CI**
   - Configurar pipeline básico para validación de Terraform (`terraform validate`, `plan`)
   - Implementar proceso de Pull Request con revisión de código (mínimo **2 PRs documentados** con comentarios)
   - Configurar despliegue automatizado de infraestructura base

3. **Documentación inicial**
   - Actualización del tablero Kanban/Scrum
   - Crear `README.md` base con estructura, pre-requisitos y variables de entorno

---

## Semana 3: Containerización y CI con seguridad

1. **Containerización de RetailStore**
   - Revisar y mejorar los Dockerfiles existentes aplicando **buenas prácticas**:
     - Imágenes base mínimas (distroless, alpine o similares)
     - Construcción **multi-stage** para reducir tamaño final
     - Usuario **no-root** dentro del contenedor
     - Archivo `.dockerignore` adecuado
   - Pruebas de construcción de imágenes localmente
   - Implementación de `docker-compose` para pruebas de integración local

2. **Configuración de CI con DevSecOps integrado**
   - Configurar pipeline con las etapas mínimas:
     - Build / compilación de artefactos
     - Ejecución de pruebas automatizadas
     - Análisis de código estático (SAST)
     - Análisis de dependencias (SCA) — fallar ante CVEs CRITICAL o HIGH sin parche
     - **Detección de secretos expuestos** — fallar el build si se detectan credenciales
     - Escaneo de imagen de contenedor antes de publicar en el registry
     - Publicación de imagen en registry (ECR, Docker Hub, GHCR u otro)
     - Despliegue al ambiente correspondiente
   - Verificar que **ningún secreto** quede hardcodeado en el código fuente ni en configuraciones

3. **Primer PR documentado**
   - Realizar al menos un PR con comentarios de revisión entre integrantes del equipo

---

## Semana 4: Orquestación, CD y Testing

1. **Configuración de orquestación de contenedores**
   - Despliegue de **ECS o EKS** (o equivalente en Azure/GCP)
   - Configuración de servicios y tareas para cada microservicio de RetailStore
   - Implementación de balanceo de carga y escalado automático

2. **Configuración de entornos múltiples**
   - Despliegue de infraestructura para ambiente **Dev**
   - Implementar pipeline de CD completo para Dev
   - Definir **quality gates** entre ambientes (umbrales de calidad y seguridad)
   - Ningún artefacto debe promoverse al siguiente ambiente si no cumple los umbrales

3. **Testing y Calidad**
   - Implementar **al menos uno** de los siguientes tipos de testing:
     - Pruebas funcionales / de integración entre microservicios (Postman/Newman, REST-assured, Pact, etc.)
     - Pruebas de carga y rendimiento (k6, JMeter, Locust, etc.)
   - Integrar herramienta de análisis de código estático en el pipeline de CI
   - Definir umbrales de calidad (coverage mínimo, máximo de code smells, etc.) como quality gate
   - Comenzar recolección de métricas para el informe de testing

4. **Segundo PR documentado**
   - Realizar al menos un segundo PR con comentarios de revisión entre integrantes

---

## Semana 5: Producción, Serverless y Observabilidad

1. **Configuración de ambientes Test y Prod**
   - Despliegue de infraestructura para **Test** y **Prod** usando Terraform con `.tfvars` diferenciados
   - Implementar pipelines completos para todos los ambientes
   - Configurar aprobaciones y controles de seguridad entre ambientes
   - Verificar configuraciones de alta disponibilidad

2. **Implementación de servicio Serverless (elegir al menos uno)**
   - **AWS Lambda**: automatizaciones o procesamiento de eventos (alertas, análisis de logs de seguridad, notificaciones de vulnerabilidades, rotación de secretos, etc.)
   - **API Gateway**: gestión de acceso y seguridad a APIs (rate limiting, autenticación, WAF básico)
   - Integrar con el resto de la arquitectura
   - Documentar propósito y funcionamiento (se valorará relación con seguridad u observabilidad)

3. **Implementación de observabilidad**
   - Recolectar **métricas de infraestructura** (CPU, memoria, red) y **de aplicación** (latencia, tasa de errores, throughput)
   - Centralizar **logs** de todos los servicios con formato estructurado (CloudWatch, Loki u otro)
   - Configurar al menos **1 dashboard** con métricas relevantes para operación
   - Establecer al menos **2 alertas** con condición de disparo y procedimiento básico de respuesta:
     - Disponibilidad / rendimiento / seguridad / uso de recursos
   - Herramientas sugeridas: CloudWatch, Prometheus, Grafana, Loki, OpenTelemetry

4. **Documentar tercer estado del tablero** (screenshot de cierre)

---

## Semana 6: Documentación y entrega final

1. **Documentación técnica completa**
   - Finalizar `README.md` con:
     - Instrucciones de despliegue paso a paso
     - Configuración de variables de entorno
     - Pre-requisitos
   - Generar los siguientes **diagramas**:
     - Diagrama de arquitectura general del sistema
     - Flujo completo del pipeline CI/CD con etapas de seguridad integradas
     - Estrategia de Git (branching model) adoptada
     - Arquitectura de observabilidad (cómo fluyen métricas, logs y trazas)
   - Documentar **decisiones de diseño relevantes** (Architecture Decision Records o formato libre)
   - Documentar **lecciones aprendidas** del proyecto
   - Elaborar **informe de resultados de testing** con hallazgos y recomendaciones de mejora
   - Reportar hallazgos de seguridad (SAST, SCA, escaneo de imágenes) y documentar remediaciones o excepciones justificadas

2. **Pruebas finales e iteraciones**
   - Pruebas de integración end-to-end del sistema completo
   - Verificar que los quality gates bloqueen correctamente artefactos con vulnerabilidades críticas
   - Verificación de todos los puntos de la rúbrica

3. **Preparación para la entrega y defensa**
   - Crear presentación de venta (técnica o no técnica) para la defensa de 20 minutos
   - Preparar demo breve de funcionalidades clave (CI/CD, seguridad, observabilidad)
   - Ensayo de la defensa grupal (cámara encendida, micrófono abierto)
   - Preparar archivo `.zip` o `.rar` con todo el contenido (repositorio git, documentación, imágenes, etc.) — **máximo 40MB**
   - **Entrega final en gestion.ort.edu.uy antes de las 21:00 hs del 29/06/26**

---

## Rúbrica de evaluación (55 puntos)

| Criterio | Puntos |
|---|---|
| Implementación general (solución completa, innovadora, demuestra entendimiento de DevOps) | 7 |
| Gestión de proyecto (metodología consistente y progreso documentado) | 5 |
| Documentación técnica (completa, bien estructurada, en Markdown) | 5 |
| Containerización y despliegue (buenas prácticas + orquestación en nube) | 5 |
| Gestión de código (repositorios bien organizados, separación de componentes) | 5 |
| Estrategias de ramificación (trabajo colaborativo, PRs con revisión) | 4 |
| Implementación serverless (propósito claro e integrado) | 4 |
| Infraestructura como código (modular y parametrizada) | 4 |
| Análisis de código estático (informe con hallazgos y recomendaciones) | 4 |
| Testing y Calidad (tipo de testing integrado y quality gates) | 4 |
| Seguridad / DevSecOps (herramientas integradas al SDLC) | 4 |
| Observabilidad (métricas, logs, dashboard y alertas) | 4 |
| **Total** | **55** |

---

## Recomendaciones de trabajo

- **Seguimiento constante**: Mantener el tablero actualizado y documentar el estado en al menos **3 momentos** (inicio, mitad, cierre)
- **Documentación progresiva**: Documentar decisiones y configuraciones mientras se avanza
- **Enfoque de MVP**: Primero implementar versiones mínimas funcionales, luego mejorar
- **Seguridad desde el día 1**: No dejar los controles de seguridad (SCA, SAST, detección de secretos) para el final
- **Pruebas continuas**: No dejar todas las pruebas para el final
- **Revisar rúbrica**: Verificar periódicamente el cumplimiento de todos los puntos
- **PRs obligatorios**: Documentar mínimo **2 Pull Requests** con comentarios de revisión entre integrantes
