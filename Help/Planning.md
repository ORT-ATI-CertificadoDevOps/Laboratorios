# Plan de Trabajo - Obligatorio

## Semana 1: Planificación y configuración inicial

1. **Planificación del proyecto**
   - Estudio detallado de los requerimientos del obligatorio
   - Creación del tablero Kanban (documentar primer estado con screenshot)
   - Decidir roles y responsabilidades dentro del equipo
   - Registrar equipo en el listado oficial

2. **Investigación y selección de herramientas**
   - Herramienta de gestión de repositorios Git (GitHub, GitLab, Bitbucket)
   - Herramienta de CI/CD (GitHub Actions, GitLab CI/CD, Jenkins, CircleCI)
   - Herramienta para análisis de código estático (SonarQube, ESLint)
   - Herramienta para testing adicional (Postman, JMeter, Selenium)

3. **Configuración de repositorios**
   - Clonar el repositorio de `voting-app`
   - Crear estructura de repositorios para el proyecto (app, infraestructura)
   - Definir estrategia de ramificación para código de aplicación (justificar Git Flow o Trunk Based)
   - Definir estrategia Feature Branch para código de infraestructura

4. **Exploración de AWS**
   - Familiarización con la consola de AWS
   - Exploración de servicios básicos que se utilizarán (EC2, VPC, S3)
   - Creación de cuentas de servicio y configuración de permisos

## Semana 2: Infraestructura como código

1. **Desarrollo de infraestructura como código**
   - Configuración inicial de Terraform
   - Definir estructura modular para la infraestructura
   - Crear variables parametrizadas por ambiente (Dev, Test, Prod)
   - Implementar módulos de red (VPC, subnets, security groups)
   - Definir estrategia para manejo seguro de secretos

2. **Configuración inicial de CI**
   - Configurar pipeline básico para validación de Terraform
   - Implementar proceso de Pull Request y revisión de código
   - Configurar despliegue automatizado de infraestructura base

3. **Documentación**
   - Actualización del tablero Kanban
   - Generar README.md y comenzar a documentar

## Semana 3: Containerización y CI

1. **Containerización de aplicaciones**
   - Análisis de la arquitectura de voting-app
   - Revisar y actualizar los Dockerfiles existentes si es necesario
   - Pruebas de construcción de imágenes localmente
   - Implementación de docker-compose para pruebas de integración local

2. **Configuración de CI para contenedores**
   - Configurar pipeline para construcción de imágenes Docker
   - Integrar análisis de código estático
   - Implementar primeras pruebas automatizadas
   - Configurar registro de contenedores (ECR o equivalente)

3. **Implementación de servicios serverless**
   - Diseñar e implementar la funcionalidad serverless elegida (Lambda o API Gateway)
   - Integrar con el resto de la arquitectura
   - Documentar propósito y funcionamiento

## Semana 4: Orquestación y CD

1. **Configuración de orquestación de contenedores**
   - Despliegue de ECS o EKS según decisión del equipo
   - Configuración de servicios y tareas
   - Implementación de balanceo de carga y escalado

2. **Configuración de entornos múltiples**
   - Despliegue de infraestructura para ambiente Dev
   - Configuración de variables de entorno específicas
   - Implementar pipeline de CD para ambiente Dev
   - Definir quality gates entre ambientes

3. **Testing avanzado**
   - Implementar pruebas funcionales o de carga/rendimiento
   - Configurar ejecución automática en el pipeline
   - Comenzar recolección de métricas para el informe

## Semana 5: Producción y observabilidad

1. **Configuración de ambientes Test y Prod**
   - Despliegue de infraestructura para Test y Prod
   - Implementar pipelines completos para todos los ambientes
   - Configurar aprobaciones y controles de seguridad
   - Verificar configuraciones de alta disponibilidad

2. **Implementación de observabilidad**
   - Configuración de logging centralizado
   - Implementar dashboard de métricas críticas
   - Configurar al menos 2 alertas para condiciones relevantes
   - Pruebas de recuperación ante fallos

3. **Actualización del tablero Kanban** (documentar tercer estado)

## Semana 6: Documentación y entrega final

1. **Documentación técnica**
   - Finalizar README.md completo con instrucciones
   - Generar diagramas de arquitectura
   - Documentar flujos CI/CD y estrategia de Git
   - Elaborar informe de resultados de testing con recomendaciones

2. **Pruebas finales e iteraciones**
   - Pruebas de integración end-to-end
   - Optimización de configuraciones basadas en resultados
   - Verificación de todos los requerimientos del obligatorio

3. **Preparación para la entrega y defensa**
   - Crear presentación enfocada en valor de negocio (presentación de venta)
   - Preparar demo breve de funcionalidades clave
   - Ensayo de la defensa
   - Preparación del archivo de entrega (.zip o .rar)
   - Entrega final


### Recomendaciones de trabajo
- **Seguimiento constante**: Mantener el tablero Kanban actualizado y realizar reuniones regulares
- **Documentación progresiva**: Documentar decisiones y configuraciones mientras se avanza
- **Enfoque de MVP**: Primero implementar versiones mínimas funcionales, luego mejorar
- **Pruebas continuas**: No dejar todas las pruebas para el final
- **Revisar rúbrica**: Verificar periódicamente cumplimiento de todos los puntos de la rúbrica
