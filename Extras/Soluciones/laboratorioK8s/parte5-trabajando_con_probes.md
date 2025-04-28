## Solución al práctico 5

Dentro del bloque "containers" hay que agregar el bloque "readinessProbe" y "livenessProbe":
```yaml
          readinessProbe:
            httpGet:
              path: /
              port: 80
            initialDelaySeconds: 30
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /
              port: 80
            initialDelaySeconds: 15
            periodSeconds: 20
```

El YAML file completo quedaría así:


```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: 2048-app-checks
  name: 2048-checks-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: 2048-app-checks
  template:
    metadata:
      labels:
        app: 2048-app-checks
    spec:
      containers:
        - resources:
            limits: {}
            requests: {}
          image: evilroot/docker-2048
          name: docker-2048
          readinessProbe:
            httpGet:
              path: /
              port: 80
            initialDelaySeconds: 30
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /
              port: 80
            initialDelaySeconds: 15
            periodSeconds: 20
```