# Recomendadas — T04 Cloud

Estos temas no tienen un práctico guiado pero son relevantes para el curso. Se recomienda explorarlos de forma autónoma usando la documentación oficial de AWS.

---

## CloudWatch: Monitoreo y Alertas

Monitorear recursos AWS y crear alertas automáticas.

**Explorar:**
* Crear una **métrica de CPU** para una instancia EC2 y una alarma que notifique por email si supera el 80%
* Habilitar **Detailed Monitoring** en una instancia y comparar la granularidad de métricas (1 min vs 5 min)
* Crear un **Dashboard** con métricas de EC2, RDS y ELB en un solo panel

**Documentación:** [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/)

---

## Lambda: Serverless Functions

Ejecutar código sin aprovisionar ni gestionar servidores.

**Explorar:**
* Crear una función Lambda en Python que reciba un evento HTTP via **API Gateway** y devuelva un JSON
* Configurar un **trigger S3** que ejecute una Lambda cuando se suba un archivo a un bucket
* Usar **CloudWatch Logs** para ver los logs de ejecución de la función

**Documentación:** [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/)

---

## CloudFront: CDN y Distribución de Contenido

Distribuir contenido estático globalmente con baja latencia.

**Explorar:**
* Crear una distribución CloudFront apuntando al bucket S3 de la app Cafe (del lab 03-storage)
* Comparar la latencia de acceso directo al S3 vs CloudFront
* Configurar un **behavior** con cache invalidation para forzar actualización de contenido

**Documentación:** [CloudFront Developer Guide](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/)
