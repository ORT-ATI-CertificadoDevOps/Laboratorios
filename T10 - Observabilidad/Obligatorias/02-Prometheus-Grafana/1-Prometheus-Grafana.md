# Prometheus y Grafana — Stack de observabilidad en Kubernetes

CloudWatch está profundamente integrado con AWS pero no corre en entornos on-premise ni en otros clouds. **Prometheus** es el estándar open-source para recolección de métricas en Kubernetes; **Grafana** es el motor de visualización que se conecta a Prometheus (y decenas de otras fuentes) para crear dashboards.

```
Pods K8s → [métricas /metrics] → Prometheus (scraping) → Grafana (dashboards)
                                                        → Alertmanager (alertas)
```

## 2.1 Instalar el stack con Helm

La forma más simple de desplegar Prometheus + Grafana + Alertmanager en K8s es con el chart `kube-prometheus-stack`:

```bash
# Agregar el repositorio de Helm
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Crear namespace dedicado
kubectl create namespace monitoring

# Instalar el stack completo
helm install prometheus prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --set grafana.adminPassword=admin123 \
  --set prometheus.prometheusSpec.retention=7d
```

Este chart instala:
- **Prometheus** — recolección de métricas
- **Alertmanager** — gestión de alertas
- **Grafana** — visualización
- **kube-state-metrics** — métricas del estado del cluster (pods, deployments, etc.)
- **node-exporter** — métricas de nodos (CPU, memoria, disco)
- Reglas de alerta predefinidas para el cluster K8s

Verificar que todo esté corriendo:
```bash
kubectl get pods -n monitoring
```

## 2.2 Acceder a Grafana

```bash
# Port-forward para acceder localmente
kubectl port-forward -n monitoring svc/prometheus-grafana 3000:80
```

Abrir http://localhost:3000 en el navegador:
- **Usuario:** `admin`
- **Contraseña:** `admin123` (la configurada en el install)

El stack incluye dashboards predefinidos. En **Dashboards → Browse**, explorar:
- **Kubernetes / Cluster** — visión general del cluster
- **Kubernetes / Compute Resources / Namespace** — CPU y memoria por namespace
- **Kubernetes / Compute Resources / Pod** — métricas por pod

## 2.3 El modelo de datos de Prometheus

Prometheus almacena métricas como series de tiempo con etiquetas:

```
http_requests_total{method="GET", status="200", service="api"} 1234
http_requests_total{method="POST", status="500", service="api"} 12
```

Cada métrica tiene:
- **Nombre:** `http_requests_total`
- **Labels:** pares clave=valor que identifican la serie
- **Valor:** número flotante
- **Timestamp:** automático

### Tipos de métricas

| Tipo | Descripción | Ejemplo |
|------|-------------|---------|
| **Counter** | Solo sube, nunca baja | requests totales, errores |
| **Gauge** | Puede subir y bajar | memoria usada, conexiones activas |
| **Histogram** | Distribución de valores en buckets | latencia de requests |
| **Summary** | Percentiles calculados en el cliente | similar a histogram |

## 2.4 PromQL — Consultar métricas

PromQL es el lenguaje de consulta de Prometheus. En Grafana, ir a cualquier panel y hacer clic en **Edit** para ver y modificar las queries.

**Ejemplos:**

```promql
# CPU por pod en el namespace default
sum(rate(container_cpu_usage_seconds_total{namespace="default"}[5m])) by (pod)

# Memoria usada por namespace
sum(container_memory_working_set_bytes{namespace!=""}) by (namespace)

# Tasa de errores HTTP (ratio de 5xx sobre total)
rate(http_requests_total{status=~"5.."}[5m])
/
rate(http_requests_total[5m])

# Latencia p99 de requests
histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service))

# Pods en estado no-Running
kube_pod_status_phase{phase!="Running"} > 0
```

## 2.5 Exponer métricas de una aplicación propia

Para que Prometheus recolecte métricas de la aplicación, ésta debe exponer un endpoint `/metrics` en formato Prometheus.

**Node.js con prom-client:**

```javascript
const client = require('prom-client');

// Registrar métricas automáticas del proceso
const collectDefaultMetrics = client.collectDefaultMetrics;
collectDefaultMetrics();

// Métrica custom: contador de requests
const httpRequestsTotal = new client.Counter({
  name: 'http_requests_total',
  help: 'Total de requests HTTP',
  labelNames: ['method', 'path', 'status'],
});

// Métrica custom: histograma de latencia
const httpDuration = new client.Histogram({
  name: 'http_request_duration_seconds',
  help: 'Duración de requests HTTP en segundos',
  labelNames: ['method', 'path'],
  buckets: [0.01, 0.05, 0.1, 0.5, 1, 2, 5],
});

// Middleware Express para registrar métricas
app.use((req, res, next) => {
  const end = httpDuration.startTimer({ method: req.method, path: req.path });
  res.on('finish', () => {
    httpRequestsTotal.inc({ method: req.method, path: req.path, status: res.statusCode });
    end();
  });
  next();
});

// Endpoint de métricas para Prometheus
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', client.register.contentType);
  res.end(await client.register.metrics());
});
```

**Configurar el scraping en Prometheus (con ServiceMonitor):**

```yaml
# servicemonitor.yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: mi-app
  namespace: monitoring
  labels:
    release: prometheus  # debe coincidir con el label del stack
spec:
  selector:
    matchLabels:
      app: mi-app
  namespaceSelector:
    matchNames:
      - default
  endpoints:
    - port: http
      path: /metrics
      interval: 15s
```

```bash
kubectl apply -f servicemonitor.yaml
```

Prometheus detecta automáticamente el ServiceMonitor y empieza a hacer scraping del endpoint `/metrics` cada 15 segundos.

## 2.6 Crear un dashboard en Grafana

1. En Grafana, ir a **Dashboards → New dashboard → New panel**
2. En la query, escribir una expresión PromQL:
   ```promql
   rate(http_requests_total[5m])
   ```
3. En **Visualization**, seleccionar **Time series**
4. En **Panel options**, poner el título: "Requests por segundo"
5. Hacer clic en **Apply**
6. Repetir para agregar más paneles (latencia p99, tasa de errores, memoria)
7. Guardar el dashboard con **Ctrl+S**

Los dashboards se pueden exportar como JSON e importar en otros entornos — son configuración como código.

## 2.7 Resumen

| Componente | Rol | Puerto por defecto |
|------------|-----|-------------------|
| Prometheus | Scraping y almacenamiento de métricas | 9090 |
| Grafana | Dashboards y visualización | 3000 |
| Alertmanager | Gestión y enrutamiento de alertas | 9093 |
| node-exporter | Métricas de nodos K8s | 9100 |
| kube-state-metrics | Estado de objetos K8s | 8080 |

Continuar con [03 - Tracing con AWS X-Ray](../03-Tracing/1-AWS-X-Ray.md)
