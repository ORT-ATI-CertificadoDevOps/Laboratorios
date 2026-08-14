# T06 DevSecOps — Recomendadas

Temas para exploración autónoma que complementan el módulo obligatorio.

## AWS GuardDuty

GuardDuty es un servicio de detección de amenazas que analiza continuamente los logs de CloudTrail, VPC Flow Logs y DNS logs para identificar comportamiento anómalo: acceso a instancias desde IPs de listas negras, escalada de privilegios IAM, exfiltración de datos desde S3.

**Para explorar:**
- Activar GuardDuty en la consola AWS (primeros 30 días gratis)
- Revisar los findings de ejemplo que AWS provee
- Configurar una alarma de CloudWatch para findings de severidad High

## AWS Security Hub

Security Hub agrega hallazgos de GuardDuty, Inspector, Macie y otras herramientas en un único panel. Evalúa el entorno contra benchmarks de seguridad como CIS AWS Foundations y AWS Foundational Security Best Practices.

**Para explorar:**
- Habilitar Security Hub y revisar el Security Score del entorno del curso
- Identificar los findings más críticos en el entorno de labs

## HashiCorp Vault

Vault es la alternativa open-source a AWS Secrets Manager, con soporte multi-cloud y capacidades avanzadas: dynamic secrets (genera credenciales temporales on-demand), PKI management, cifrado como servicio.

**Para explorar:**
- Instalar Vault en modo dev: `vault server -dev`
- Crear y leer un secreto KV
- Configurar un dynamic secret para PostgreSQL

## Policy as Code con OPA

OPA (Open Policy Agent) permite definir políticas como código Rego y evaluarlas en el pipeline. Se puede usar para rechazar imágenes Docker que no vengan de un registry aprobado, o para validar que los recursos Terraform cumplan con políticas organizacionales.

**Para explorar:**
- Instalar OPA CLI: `brew install opa`
- Escribir una política simple que valide una configuración JSON
- Integrar OPA con Conftest para validar archivos Terraform o YAML de Kubernetes
