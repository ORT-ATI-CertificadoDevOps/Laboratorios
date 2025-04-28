## Ejercicio práctico: Despliegue de ArgoCD
![ArgoCD Logo](https://res.cloudinary.com/practicaldev/image/fetch/s--KBtH4PIk--/c_limit%2Cf_auto%2Cfl_progressive%2Cq_auto%2Cw_880/https://cdn-images-1.medium.com/max/1024/1%2ALydFAwy_HJjw8lGCsi1Iqg.png)
### Objetivo
Desplegar ArgoCD en nuestro cluster de Kubernetes mediante Terraform.

### Pasos a seguir

1. **Preparación**
   - Instalar ArgoCD sobre un clúster de Kubernetes mediante terraform utilizando el provider de helm.

2. **Estructura del repositorio**
   - Crear un repositorio de Git con la siguiente estructura:

   ```plaintext
   ├── infastructure
       ├── base
       │   ├── _providers.tf
       │   ├── _versions.tf
       │   ├── _variables.tf
       │   ├── main.tf (pueden reusar de ejercicios pasados vpc/subnets/firewall/eks)
       │   ├── argocd.tf
       │   └── values
       |       └── argocd.tf
       ├── aws
           └── us-east-1
               ├── _backend.tf
               ├── _terraform.tfvars
               ├── _providers.tf (link simbolico)
               ├── _versions.tf (link simbolico)
               ├── _variables.tf (link simbolico)
               ├── main.tf (link simbolico)
               └── argocd.tf (link simbolico)
   ```

3. **Archivo argocd.tf**

   ```
    locals {
      argocd_name_sufix    = "argocd"
      argocd_namespace     = "tools"
      argo_public_name     = "${var.argo_public_hostname}.${var.public_domain}"
      username             = "AQUI_GITHUB_USERNAME"
      clientId             = sensitive(data.aws_secretsmanager_secret_version.argocd_client_id.secret_string)
      clientSecret         = sensitive(data.aws_secretsmanager_secret_version.argocd_client_secret.secret_string)
      githubAppPrivateKey  = sensitive(data.aws_secretsmanager_secret_version.argocd_gh_app_private_key.secret_string)
    }

    data "aws_secretsmanager_secret" "argocd_client_id" {
      name = "${var.environment}/${local.argocd_name_sufix}/clientId"
    }
    data "aws_secretsmanager_secret" "argocd_client_secret" {
      name = "${var.environment}/${local.argocd_name_sufix}/clientSecret"
    }
    data "aws_secretsmanager_secret" "argocd_gh_app_private_key" {
      name = "${var.environment}/${local.argocd_name_sufix}/githubAppPrivateKey"
    }
    data "aws_secretsmanager_secret_version" "argocd_client_id" {
      secret_id = data.aws_secretsmanager_secret.argocd_client_id.name
    }
    data "aws_secretsmanager_secret_version" "argocd_client_secret" {
      secret_id = data.aws_secretsmanager_secret.argocd_client_secret.name
    }
    data "aws_secretsmanager_secret_version" "argocd_gh_app_private_key" {
      secret_id = data.aws_secretsmanager_secret.argocd_gh_app_private_key.name
    }
    resource "helm_release" "argo_cd" {
      name             = local.argocd_name_sufix
      chart            = local.argocd_name_sufix
      namespace        = local.argocd_namespace
      version          = "5.36.2"
      repository       = "https://argoproj.github.io/argo-helm"
      create_namespace = false
      values = [
        templatefile("${path.module}/values/${local.argocd_name_sufix}.yaml", {
          targetRevision      = "main"
          envShort            = var.environment
          username            = local.username
          argocdHostList      = jsonencode(["${local.argo_public_name}"])
          argocdHost          = local.argo_public_name
          argocdPaths         = jsonencode(["/"])
          healthCheckPath     = "/healthz"
          ingressEnabled      = true
          ingressClass        = "alb"
          ingressType         = "ip"
          ingressScheme       = "internet-facing"
          clientId            = local.clientId
          clientSecret        = local.clientSecret
        })
      ]

      set {
        name  = "fullnameOverride"
        value = local.argocd_name_sufix
      }
    }
   ```

4. **Archivo values\argocd.yaml**

   ```yaml
    server:
      ingress:
        enabled: true
        https: true
        ingressClassName: ${ingressClass}
        annotations:
          alb.ingress.kubernetes.io/target-type: ${ingressType}
          alb.ingress.kubernetes.io/scheme: ${ingressScheme}
          alb.ingress.kubernetes.io/listen-ports: '[{"HTTPS":443}]'
          alb.ingress.kubernetes.io/healthcheck-path: ${healthCheckPath}
          alb.ingress.kubernetes.io/backend-protocol: HTTPS
          alb.ingress.kubernetes.io/security-groups: ${sg_cloudflare}
        hosts: ${argocdHostList}
        paths: ${argocdPaths}
        tls:
          - hosts: ${argocdHostList}
      ingressGrpc:
        enabled: true
        isAWSALB: true
        awsALB:
          serviceType: ClusterIP
      config:
        url: https://${argocdHost}
        dex.config: |
          connectors:
            - type: github
              id: github
              name: GitHub
              config:
                clientID: ${clientId}
                clientSecret: ${clientSecret}
                orgs:
                - name: ${username}
   ```