# Code commit and Code Build 


## 01: Pre-requisite Step - Create Staging and production services in ECS
- **Create ECS Task Definition**
  - Name: ecs-cicd-nginx 
  - Container Name: ecs-cicd-nginx  
  - **Importante: Tome nota del nombre de este contenedor, debe ser el mismo que proporcionamos en nuestro buildspec.yml para el nombre del contenedor**
  - Image: stacksimplify/nginxapp2:latest
- **Create Staging ECS Service**
  - Name: staging-ecs-cicd-nginx-svc
  - Number of Tasks: 1
- **Create Production ECS Service**
  - Name: prod-ecs-cicd-nginx-svc
  - Number of Tasks: 1  


## 02: Create CodeCommit Repository
- Crear CodeCommit repo con el nombre como **ecs-cicd-nginx**
- Clonar el repositorio localmente utilizando la opción de `HTTPS (GCR)`, verificar que van a tener que realizar algunas instalaciones de paquetes con Python.
- Para clonarlo una vez que lo crearon, ir a el repo, lo seleccionan y ahi tienen los `Connection steps`, la opción para clonarlo que tienen que usar dentro de `HTTPS (GCR)` es la de `Assuming a role`, ya que en estas cuentas contamos con el LabRole para poder hacer esto.
- El archivo `~/.aws/credentials` van a usar las credenciales `default` como siempre, pero van a tener que tener el archivo `~/.aws/config` 
```
[default]
region = us-east-1
output = json
[profile CodeAccess]
role_arn = arn:aws:iam::669574065319:role/LabRole
source_profile = default
role_session_name = voclabs/user1586855=Federico_Barcel__
```
- Copiar el siguiente codigo al repositorio, commitear y pushear los cambios, en la carpeta del practico los archivos:
  - Dockerfile 
  - index.html  

- Verificar en el repositorio de CodeCommit que los archivos esten subidos.

## 03: Create buildspec.yml for CodeBuild
- Cree un nuevo repositorio en Elastic Container Registry (ECR) con el nombre como **ecs-cicd-nginx** y tome nota del nombre completo del repositorio ECR.
- Crear archivo **buildspec.yml** en el repo con el siguiente contenido y remplazar los valores correspondientes:
   - Actualizar **REPOSITORY_URI** 
valor con el nombre completo del repositorio ECR
   - Actualizar **Container Name** en **printf '[{"name":"ecs-cicd-nginx"**
   - **Nota importante:** En la ECS Task Definition también cuando la estamos creando, asegúrese de dar el nombre del contenedor como **ecs-cicd-nginx**

### buildspec.yml

```
version: 0.2

phases:
  install:
    runtime-versions:
      docker: 18       
  pre_build:
    commands:
      - echo Logging in to Amazon ECR.....
      - aws --version
      - $(aws ecr get-login --region $AWS_DEFAULT_REGION --no-include-email)
      - REPOSITORY_URI=<YOUR_ACCOUNT_ID>.dkr.ecr.<YOUR_REGION>.amazonaws.com/ecs-cicd-nginx
      - IMAGE_TAG=$(echo $CODEBUILD_RESOLVED_SOURCE_VERSION | cut -c 1-7)
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - docker build -t $REPOSITORY_URI:$IMAGE_TAG .
  post_build:
    commands:
      - echo Build completed on `date`
      - echo Pushing the Docker images...
      - docker push $REPOSITORY_URI:$IMAGE_TAG
      - echo Writing image definitions file...
      - printf '[{"name":"ecs-cicd-nginx","imageUri":"%s"}]' $REPOSITORY_URI:$IMAGE_TAG > imagedefinitions.json
artifacts:
    files: imagedefinitions.json
```
-  Pushear el archivo al repositorio de CodeCommit.

**A partir de aca van a necesitar una cuenta que no sea de Laboratorio para poder utilizar CodePipeline y CodeBuild :'( , les dejo los siguientes pasos por tienen la posibilidad de acceder a otra cuenta y hacer un CICD con esto.

## 04: Create CodePipeline 
- Crear CodePipeline, van a tener que configurar los steps de:
  - Source stage: elegir el repositorio generadio anteriormente.
  - Build stage: generar una nueva instancia de build stage, con los valores por defecto.
  - Deploy stage: elegir alguno de los clusters que tenemos generados para desplegar con los valores correspondientes.
- Pruebe accediendo a la página html estática

## 05: Make changes to index.html file
- Realice cambios en index.html (Actualizar como V2)
- Confirme los cambios en el repositorio local de git y envíelos al repositorio de codeCommit
- Monitorear el codePipeline
- Pruebe accediendo a la página html estática

## 06: Create Manual Approval stage in CodePipeline
- Cree un topic SNS y confirme la suscripción de correo electrónico para enviar notificaciones
- Edite Pipeline y cree **Manual Approval Stage**


## 07: Create Deploy to Prod ECS Service stage in CodePipeline
- Edite el pipeline y cree **Deploy to prod ECS Service**



