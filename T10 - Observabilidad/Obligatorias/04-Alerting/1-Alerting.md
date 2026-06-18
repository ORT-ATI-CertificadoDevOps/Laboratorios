# Alerting — Alertmanager y Notificaciones

Las métricas y los dashboards son útiles cuando alguien los mira. Las alertas cierran el loop: cuando algo sale mal, el sistema notifica a la persona correcta, por el canal correcto, con suficiente contexto para actuar.

**Alertmanager** es el componente del stack Prometheus que recibe alertas de Prometheus, las agrupa, las deduplica y las enruta a los destinos configurados (Slack, email, PagerDuty, etc.).

```
Prometheus → [regla de alerta supera umbral] → Alertmanager → Slack / email / PagerDuty
```

## 4.1 Reglas de alerta en Prometheus

Las reglas de alerta se definen en archivos YAML y se cargan en Prometheus. Con `kube-prometheus-stack`, se pueden agregar como recursos de Kubernetes:

```yaml
# alertas-mi-app.yaml
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  name: alertas-mi-app
  namespace: monitoring
  labels:
    release: prometheus
spec:
  groups:
    - name: mi-app.reglas
      interval: 30s
      rules:

        # Alerta: tasa de errores > 5% por más de 5 minutos
        - alert: AltaTasaDeErrores
          expr: |
            rate(http_requests_total{status=~"5.."}[5m])
            /
            rate(http_requests_total[5m])
            > 0.05
          for: 5m
          labels:
            severity: warning
            team: backend
          annotations:
            summary: "Alta tasa de errores en {{ $labels.service }}"
            description: "La tasa de errores es {{ $value | humanizePercentage }} en los últimos 5 minutos."
            runbook_url: "https://wiki.empresa.com/runbooks/alta-tasa-errores"

        # Alerta: latencia p99 > 2 segundos
        - alert: AltaLatencia
          expr: |
            histogram_quantile(0.99,
              sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service)
            ) > 2
          for: 10m
          labels:
            severity: critical
            team: backend
          annotations:
            summary: "Latencia p99 crítica en {{ $labels.service }}"
            description: "El p99 de latencia es {{ $value }}s, superando el umbral de 2s."

        # Alerta: pod en CrashLoopBackOff
        - alert: PodEnCrashLoop
          expr: |
            rate(kube_pod_container_status_restarts_total[15m]) * 60 * 15 > 3
          for: 0m
          labels:
            severity: critical
            team: plataforma
          annotations:
            summary: "Pod {{ $labels.pod }} reiniciando constantemente"
            description: "El pod {{ $labels.pod }} en namespace {{ $labels.namespace }} tuvo más de 3 reinicios en los últimos 15 minutos."

        # Alerta: nodo con más del 90% de CPU
        - alert: NodoCPUCritico
          expr: |
            100 - (avg(irate(node_cpu_seconds_total{mode="idle"}[5m])) by (node) * 100) > 90
          for: 10m
          labels:
            severity: warning
            team: plataforma
          annotations:
            summary: "CPU crítico en nodo {{ $labels.node }}"
            description: "El nodo {{ $labels.node }} está usando {{ $value | humanize }}% de CPU."
```

```bash
kubectl apply -f alertas-mi-app.yaml
```

Las alertas aparecen en la UI de Prometheus en **Alerts** y en Alertmanager.

## 4.2 Configurar Alertmanager — Slack

Actualizar la configuración de Alertmanager con un values file para Helm:

```yaml
# alertmanager-values.yaml
alertmanager:
  config:
    global:
      slack_api_url: 'https://hooks.slack.com/services/T.../B.../xxx'

    route:
      group_by: ['alertname', 'team']
      group_wait: 30s        # espera 30s antes de enviar para agrupar alertas
      group_interval: 5m     # reenvía si hay nuevas alertas en el grupo
      repeat_interval: 4h    # reenvía si la alerta sigue activa
      receiver: 'slack-default'

      routes:
        # Alertas críticas van a un canal diferente
        - match:
            severity: critical
          receiver: 'slack-critico'

        # Alertas del equipo de plataforma
        - match:
            team: plataforma
          receiver: 'slack-plataforma'

    receivers:
      - name: 'slack-default'
        slack_configs:
          - channel: '#alertas-dev'
            title: '{{ .GroupLabels.alertname }}'
            text: |
              {{ range .Alerts }}
              *Resumen:* {{ .Annotations.summary }}
              *Descripción:* {{ .Annotations.description }}
              {{ if .Annotations.runbook_url }}*Runbook:* {{ .Annotations.runbook_url }}{{ end }}
              {{ end }}
            color: '{{ if eq .Status "firing" }}danger{{ else }}good{{ end }}'

      - name: 'slack-critico'
        slack_configs:
          - channel: '#alertas-criticas'
            title: ':rotating_light: CRÍTICO: {{ .GroupLabels.alertname }}'
            text: |
              {{ range .Alerts }}
              {{ .Annotations.description }}
              {{ end }}

      - name: 'slack-plataforma'
        slack_configs:
          - channel: '#plataforma-alertas'
            title: '{{ .GroupLabels.alertname }}'
            text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
```

```bash
helm upgrade prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  -f alertmanager-values.yaml
```

### Crear el webhook de Slack

1. En Slack, ir a **Apps → Incoming Webhooks → Add to Slack**
2. Seleccionar el canal donde llegan las alertas
3. Copiar la Webhook URL y usarla en `slack_api_url`

## 4.3 Configurar notificaciones por email

```yaml
receivers:
  - name: 'email-oncall'
    email_configs:
      - to: 'oncall@empresa.com'
        from: 'alertas@empresa.com'
        smarthost: 'smtp.gmail.com:587'
        auth_username: 'alertas@empresa.com'
        auth_password: '{{ .ExternalLabels.SMTP_PASSWORD }}'
        send_resolved: true
        headers:
          subject: '[{{ .Status | toUpper }}] {{ .GroupLabels.alertname }}'
        html: |
          <h2>{{ .GroupLabels.alertname }}</h2>
          {{ range .Alerts }}
          <p><b>{{ .Annotations.summary }}</b></p>
          <p>{{ .Annotations.description }}</p>
          {{ end }}
```

## 4.4 Silenciar alertas (maintenance window)

Cuando se hace mantenimiento planeado, silenciar alertas evita ruido innecesario:

En la UI de Alertmanager (port-forward al puerto 9093):
```bash
kubectl port-forward -n monitoring svc/prometheus-alertmanager 9093:9093
```

Ir a http://localhost:9093 → **Silences → New Silence**:
- **Matchers:** `alertname=~".*"` (todas las alertas) o `team=plataforma`
- **Duration:** duración de la ventana de mantenimiento
- **Comment:** motivo del silencio

También desde la CLI:
```bash
curl -X POST http://localhost:9093/api/v2/silences \
  -H 'Content-Type: application/json' \
  -d '{
    "matchers": [{"name": "alertname", "value": ".*", "isRegex": true}],
    "startsAt": "2024-01-15T02:00:00Z",
    "endsAt": "2024-01-15T04:00:00Z",
    "comment": "Mantenimiento de base de datos"
  }'
```

## 4.5 Buenas prácticas de alerting

| Práctica | Por qué |
|----------|---------|
| Alertar sobre síntomas, no causas | "Latencia alta" es accionable; "CPU alto" a veces no lo es |
| Incluir `runbook_url` en cada alerta | El oncall necesita saber qué hacer, no solo que hay un problema |
| Usar `for:` para evitar flapping | Una alerta que dura 1 minuto y desaparece genera ruido |
| Separar severidades (warning vs critical) | No todas las alertas requieren despertar a alguien a las 3am |
| Revisar y depurar alertas regularmente | Las alertas que nadie atiende se vuelven ruido de fondo |
| `group_wait` y `group_interval` | Agrupar alertas relacionadas evita inundar el canal de Slack |

## 4.6 Resumen del módulo T10

```
CloudWatch    → observabilidad nativa AWS (métricas, logs, alarmas, dashboards)
Prometheus    → recolección de métricas en K8s (scraping, PromQL, storage)
Grafana       → visualización y dashboards (multi-source, alerting integrado)
X-Ray         → tracing distribuido (qué tardó cuánto en cada request)
Alertmanager  → enrutamiento y deduplicación de alertas (Slack, email, PagerDuty)
```

| Herramienta | Pilar | Entorno ideal |
|-------------|-------|--------------|
| CloudWatch | Métricas + Logs + Alertas | AWS-native |
| Prometheus + Grafana | Métricas | K8s / multi-cloud |
| X-Ray | Trazas | AWS Lambda / ECS |
| Alertmanager | Alertas | Stack Prometheus |
