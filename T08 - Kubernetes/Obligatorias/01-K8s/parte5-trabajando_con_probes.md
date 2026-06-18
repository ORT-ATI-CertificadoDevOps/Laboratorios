## Kubernetes - Parte 5

### Chequeos de estado: liveness y readiness probes

Kubernetes usa probes para saber cuándo un pod está listo para recibir tráfico y cuándo debe reiniciarse.

| Probe | ¿Qué determina? | Si falla... |
|-------|----------------|-------------|
| **readinessProbe** | La app está lista para recibir requests | El pod se saca del balanceo (no se reinicia) |
| **livenessProbe** | La app sigue viva y funcionando | Kubernetes reinicia el contenedor |

### Tarea

Partir del Deployment `2048-app` creado en la parte 4 y modificarlo para agregar ambos tipos de probe usando `httpGet` contra el path `/` en el puerto `80`.

Parámetros sugeridos:

- **readinessProbe:** `initialDelaySeconds: 30`, `periodSeconds: 10`
- **livenessProbe:** `initialDelaySeconds: 15`, `periodSeconds: 20`

Una vez aplicado el YAML:

```bash
## Aplicar el deployment actualizado
kubectl apply -f deployment.yaml

## Verificar que el pod arranca correctamente
kubectl get pods -w

## Describir el pod para ver el estado de los probes
kubectl describe pod <nombre-del-pod>
```

En la salida de `describe` debería aparecer algo como:

```
Liveness:   http-get http://:80/ delay=15s timeout=1s period=20s
Readiness:  http-get http://:80/ delay=30s timeout=1s period=10s
```

Responder:
- ¿Qué pasa si se configura un `initialDelaySeconds` muy bajo para la readinessProbe?
- ¿En qué situaciones conviene usar liveness pero no readiness?

### Alerta de Spoiler

En caso de trancarse, se puede consultar [aquí](/Extras/Soluciones/laboratorioK8s/parte5-trabajando_con_probes.md).
