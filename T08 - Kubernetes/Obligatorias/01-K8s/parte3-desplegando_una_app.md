## Desplegando Aplicaciones

### 2048 App - Modo imperativo

Vamos a desplegar la aplicación 2048, de forma imperativa, esto es, mediante comandos con la herramienta ***Kubectl***.

```bash
## Desplegar un Pod de nombre pod-2048 usando la imagen evilroot/docker-2048 y pasándole una etiqueta app=pod-2048
$ kubectl run pod-2048 --image=evilroot/docker-2048 -l app=pod-2048

## Exponer el pod usando un Service del tipo LoadBalancer. De esta forma, se crea un ALB directamente en AWS.
$ kubectl expose pod pod-2048 --port=80  --name=svc-2048 --type=LoadBalancer

## Podemos validar que se haya creado el service y obtener la url del balanceador
$ kubectl get svc

## Listar todos los objetos
$ kubectl get all
NAME      READY   STATUS    RESTARTS   AGE
pod/pod-2048   1/1     Running   0          41m

NAME                 TYPE           CLUSTER-IP      EXTERNAL-IP                                                              PORT(S)        AGE
service/kubernetes   ClusterIP      10.100.0.1      <none>                                                                   443/TCP        85m
service/svc-2048     LoadBalancer   10.100.87.220   a76d4512a05064cd09e2dfd71ece33d4-334853698.us-east-1.elb.amazonaws.com   80:32563/TCP   4m38s

## Eliminar el Service creado y el POD
$ kubectl delete svc svc-2048
$ kubectl delete pod pod-2048
```

### 2048 App - Modo declarativo

El modo declarativo implica crear archivos YAML con la descripción de los objetos que queremos crear. Siguiendo la línea del POD, vamos a crear un yaml para declarar un objeto del tipo POD. Para esto crear un archivo de nombre pod-2048.yaml (por ejemplo), con el siguiente contenido:

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: pod-2048
  name: pod-2048
spec:
  containers:
  - image: evilroot/docker-2048
    name: pod-2048
  dnsPolicy: ClusterFirst
  restartPolicy: Always
```
Luego de tener el archivo, podemos crearlo:

```bash
kubectl create -f pod-2048.yaml
```
Podemos exponer el POD de la misma forma que hicimos anteriormente, con el comando: `$ kubectl expose pod pod-2048 --port=80 --name=svc-2048`

