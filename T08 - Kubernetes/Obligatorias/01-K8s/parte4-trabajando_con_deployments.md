## Trabajando con Deployments

### 2048 App - Parte 1

Crear nuevamente la aplicación ***2048*** usando el comando `kubectl run pod-2048 --image=evilroot/docker-2048 -l app=app-2048`

Listar y describir los recursos creados:

```bash
## Obtener todos los recursos
$ kubectl get all

## Listar solo los objetos del tipo POD
$ kubectl get pods

## Obtener información detallada de un objeto
$ kubectl describe pod app-2048

## Probar eliminar uno de los PODs
$ kubectl delete pod nombre_de_un_pod

## Listar nuevamente los PODs
$ kubectl get pods
```
**Qué pasó con el POD?**

### 2048 App - Parte 2

Crear un YAML con el código siguiente:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: 2048-app
  name: 2048-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: 2048-app
  strategy: {}
  template:
    metadata:
      labels:
        app: 2048-app
    spec:
      containers:
      - image: evilroot/docker-2048
        name: docker-2048
```

Para crear el objeto, usar el comando `kubectl create -f nombre-archivo.yaml`

```bash
$ kubectl create -f deployment.yaml                                                                                             
deployment.apps/2048-app created
```

Listar y describir los recursos creados:

```bash

## Obtener todos los recursos
$ kubectl get all

## Listar solo los objetos del tipo POD
$ kubectl get pods -o wide

## Obtener información detallada de un objeto
$ kubectl describe pod nombre_del_pod_a_describir

## Observar el objeto ReplicaSet. Que información nos brinda?
$ kubectl get replicaset

## Probar eliminar uno de los PODs
$ kubectl delete pod nombre_de_un_pod

## Obtener información detallada de un objeto
$ kubectl describe pod nombre_del_pod_a_describir
```

#### Publicar la App

Crear un archivo YAML con el siguiente código.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: svc-2048
spec:
  type: LoadBalancer
  selector:
    app: 2048-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
```

Tareas:

* Crear de la misma forma, el Service con el archivo YAML
* Listar los objetos
* Describir el objeto Service
* Responder:
  * De qué forma relaciona el Service con los PODs
  * Cual es la URL de la aplicación
  * Qué otro tipo de Service podemos crear
* Investigar de qué forma podemos aumentar de dos réplicas a tres