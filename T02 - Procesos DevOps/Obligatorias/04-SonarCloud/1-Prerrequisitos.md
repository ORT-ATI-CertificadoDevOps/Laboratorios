## Preparación ambiente

### Prerrequisitos
No se necesitan habilidades específicas para este laboratorio.

Para realizar el laboratorio necesitamos lo siguiente:

- Tener cuenta en [GitHub](https://github.com/)
- Tener cuenta en [SonarQube Cloud (SonarCloud)](https://www.sonarsource.com/products/sonarcloud/signup-free/)
- Haber completado el lab **02-GitHub-Actions** de este topic — el análisis se dispara desde un workflow


### ¿Qué es Sonar?

**SonarQube** es una plataforma para el análisis estático del código fuente, con el objetivo de mejorar su calidad y reducir defectos potenciales. Detecta errores en fases tempranas del desarrollo, lo que permite ahorrar tiempo y costes.

Existe en dos modalidades: **SonarQube Server**, que se instala y opera en infraestructura propia, y **SonarQube Cloud** (antes SonarCloud), el servicio SaaS que se conecta directo al repositorio de GitHub. En este laboratorio usamos **SonarQube Cloud**.

La herramienta evalúa continuamente parámetros como la duplicación del código, la cobertura de pruebas unitarias o la complejidad ciclomática, aportando métricas que guían las decisiones técnicas y facilitan la mejora continua del código.

## Próximos pasos
Para el siguiente paso del laboratorio, diríjase a [2 - Generar nuestro primer analisis con SonarCloud](2-Generar_nuestro_primer_analisis_con_SonarCloud.md)