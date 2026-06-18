# T10 - Observabilidad

Observabilidad es la capacidad de entender el estado interno de un sistema a partir de sus salidas externas. Un sistema observable responde a la pregunta "¿qué está pasando y por qué?" sin necesidad de acceder directamente a los servidores.

Los tres pilares de la observabilidad son:

| Pilar | Qué responde | Herramienta en este módulo |
|-------|-------------|---------------------------|
| **Métricas** | ¿Cuánto? ¿Qué tan rápido? ¿Cuántas veces? | CloudWatch, Prometheus + Grafana |
| **Logs** | ¿Qué ocurrió exactamente? | CloudWatch Logs |
| **Trazas** | ¿Dónde se fue el tiempo en esta request? | AWS X-Ray |

La diferencia entre monitoring y observabilidad: el monitoring responde a alertas conocidas ("la CPU está al 90%"); la observabilidad permite investigar problemas desconocidos siguiendo las señales del sistema.

## Obligatorias

- [01-CloudWatch: Métricas, Logs y Alarmas](/T10%20-%20Observabilidad/Obligatorias/01-CloudWatch/1-CloudWatch.md)
- [02-Prometheus y Grafana: Stack de observabilidad en K8s](/T10%20-%20Observabilidad/Obligatorias/02-Prometheus-Grafana/1-Prometheus-Grafana.md)
- [03-Tracing: AWS X-Ray](/T10%20-%20Observabilidad/Obligatorias/03-Tracing/1-AWS-X-Ray.md)
- [04-Alerting: Alertmanager y notificaciones](/T10%20-%20Observabilidad/Obligatorias/04-Alerting/1-Alerting.md)

## Recomendadas

- [Exploración autónoma: ELK Stack, Datadog, SLO/SLI con Grafana](/T10%20-%20Observabilidad/Recomendadas/index.md)
