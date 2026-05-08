# T11 Observabilidad — Recomendadas

## ELK Stack / OpenSearch

ELK (Elasticsearch + Logstash + Kibana) o su variante open-source OpenSearch es el stack más usado para análisis de logs a escala. Mientras CloudWatch Logs Insights cubre casos de uso básicos, ELK permite búsquedas full-text sobre terabytes de logs, dashboards de análisis de seguridad, y correlación de eventos entre múltiples fuentes.

**Para explorar:**
- Desplegar OpenSearch con Helm en el cluster K8s del laboratorio
- Configurar Fluent Bit como log shipper (ya incluido en muchos clusters K8s)
- Crear un index pattern en OpenSearch Dashboards
- Escribir una query KQL para filtrar errores por aplicación

## Datadog

Datadog es la plataforma SaaS de observabilidad más usada en producción. Unifica métricas, logs, trazas, dashboards, alertas y más en una sola plataforma con 600+ integraciones. Tiene una trial de 14 días sin tarjeta de crédito.

**Para explorar:**
- Crear una cuenta trial en datadog.com
- Instalar el Datadog Agent en una instancia EC2 o como DaemonSet en K8s
- Explorar el APM (Application Performance Monitoring) conectado a una app Node.js
- Configurar un monitor (alerta) con notificación a email

## SLO/SLI con Grafana

**SLI** (Service Level Indicator) es la métrica que mide la confiabilidad: tasa de errores, latencia p99, disponibilidad. **SLO** (Service Level Objective) es el objetivo: "el 99.9% de los requests deben completarse en menos de 500ms".

Grafana tiene soporte nativo para SLOs desde la versión 10.

**Para explorar:**
- Definir un SLI para la API del laboratorio: `1 - (errores / total_requests)`
- Configurar un SLO del 99.5% en Grafana con ventana de 30 días
- Calcular el error budget: cuántos minutos de downtime quedan en el mes
- Crear una alerta cuando el error budget cae por debajo del 20%

## OpenTelemetry

OpenTelemetry (OTel) es el estándar open-source para instrumentación de observabilidad — define las APIs para generar métricas, logs y trazas de forma agnóstica al backend. En vez de integrar directamente con X-Ray o Datadog, la app se instrumenta con OTel y se elige el backend en configuración.

**Para explorar:**
- Instrumentar una app Node.js con `@opentelemetry/sdk-node`
- Exportar trazas a AWS X-Ray usando el OTLP exporter
- Comparar con la instrumentación directa del lab de X-Ray
