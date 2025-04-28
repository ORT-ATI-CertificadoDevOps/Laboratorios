## Ejercicio práctico: Despliegue de aplicaciones con Argo CD y "app of apps" basado en Helm
![ArgoCD Logo](https://res.cloudinary.com/practicaldev/image/fetch/s--KBtH4PIk--/c_limit%2Cf_auto%2Cfl_progressive%2Cq_auto%2Cw_880/https://cdn-images-1.medium.com/max/1024/1%2ALydFAwy_HJjw8lGCsi1Iqg.png)
### Objetivo
Desplegar múltiples aplicaciones utilizando Argo CD y el modelo de "app of apps" basado en Helm.

### Pasos a seguir

1. **Preparación**
   - Instalar Argo CD en el clúster de Kubernetes ([Desplegando_ArgoCD](1-Desplegando_ArgoCD.md))
   - Configurar el acceso a los repositorios de Helm donde se encuentran las aplicaciones a desplegar.

2. **Estructura del repositorio**
   - Sobre el repositorio que creamos en el primer ejercicio, vamos a agregar los siguientes directorios:

   ```plaintext
   ├── app-of-apps
   │   └── values.yaml
   └── apps
       ├── app1
       │   ├── Chart.yaml
       │   ├── templates
       │   └── values.yaml
       ├── app2
       │   ├── Chart.yaml
       │   ├── templates
       │   └── values.yaml
       └── app3
           ├── Chart.yaml
           ├── templates
           └── values.yaml
   ```

3. **Archivo values.yaml de la "app of apps"**
   - En el directorio `app-of-apps`, crea un archivo `values.yaml` con la configuración de las aplicaciones a desplegar. Por ejemplo:

   ```yaml
   apps:
     - name: app1
       repo: <URL_REPO_APP1>
       path: apps/app1
       values: apps/app1/values.yaml
     - name: app2
       repo: <URL_REPO_APP2>
       path: apps/app2
       values: apps/app2/values.yaml
     - name: app3
       repo: <URL_REPO_APP3>
       path: apps/app3
       values: apps/app3/values.yaml
   ```

4. **Despliegue de la "app of apps"**
   - Crea un archivo YAML de Argo CD (`argocd-app.yaml`) para desplegar la "app of apps". Por ejemplo:

   ```yaml
   apiVersion: argoproj.io/v1alpha1
   kind: Application
   metadata:
     name: app-of-apps
     namespace: argocd
   spec:
     destination:
       namespace: <TARGET_NAMESPACE>
       server: https://kubernetes.default.svc
     project: default
     source:
       repoURL: <URL_REPO_APP_OF_APPS>
       path: app-of-apps
       targetRevision: HEAD
       helm:
         valueFiles:
           - values.yaml
     syncPolicy:
       automated:
         prune: true
   ```

   - Aplica el archivo YAML de Argo CD para desplegar la "app of apps":

   ```plaintext
   kubectl apply -f argocd-app.yaml
   ```

5. **Monitoreo y actualización**
   - Argo CD se encargará de sincronizar y desplegar automáticamente las aplicaciones individuales basadas en Helm especificadas en la "app of apps".
   - Para realizar actualizaciones en las aplicaciones individuales, simplemente actualiza los archivos `values.yaml` de cada aplicación y realiza un commit en el repositorio correspondiente. Argo CD detectará los cambios y aplicará las actualizaciones automáticamente.

Recuerda reemplazar las etiquetas `<URL_REPO_...>` y `<TARGET_NAMESPACE>` con las URL de los repositorios