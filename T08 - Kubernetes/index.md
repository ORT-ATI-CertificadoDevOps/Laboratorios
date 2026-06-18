# T08 - Kubernetes

Kubernetes (K8s) es el estándar de facto para orquestar contenedores en producción. Permite declarar el estado deseado de una aplicación —cuántas réplicas, qué imagen, cómo exponer el servicio— y se encarga de mantener ese estado de forma automática, incluso ante fallas de nodos o pods.

En este módulo trabajamos con **Amazon EKS** (Elastic Kubernetes Service), el servicio gestionado de AWS que provee el control plane de K8s y permite agregar nodos de forma simple. Los labs están diseñados para correr en **AWS Academy Learner Labs**, usando el rol `LabRole` preconfigurado.

> **Importante:** Estos labs requieren una cuenta de AWS Academy. El rol `LabRole` ya tiene los permisos necesarios para EKS, EC2 y VPC. No es necesario crear credenciales ni roles IAM adicionales.

## Obligatorias

- [Parte 1 — Desplegar un cluster EKS](/T08%20-%20Kubernetes/Obligatorias/01-K8s/parte1-desplegando_un_cluster.md)
- [Parte 2 — Agregar worker nodes](/T08%20-%20Kubernetes/Obligatorias/01-K8s/parte2-desplegando_workers.md)
- [Parte 3 — Desplegar una aplicación](/T08%20-%20Kubernetes/Obligatorias/01-K8s/parte3-desplegando_una_app.md)
- [Parte 4 — Trabajar con Deployments](/T08%20-%20Kubernetes/Obligatorias/01-K8s/parte4-trabajando_con_deployments.md)
- [Parte 5 — Probes: liveness y readiness](/T08%20-%20Kubernetes/Obligatorias/01-K8s/parte5-trabajando_con_probes.md)

## Recomendadas
