# 12 - Jenkins + Pipeline (Jenkinsfile)

Vamos a trabajar con archivos Jenkinsfile para empezar a manejar nuestros propios pipelines con Jeknkins.

Se deja la documentación por si hay necesidad de consultarla. [Link documentación Jenkinsfile](https://www.jenkins.io/doc/book/pipeline/jenkinsfile/).

## 12.1 Instalar plugin de JenkinsFile

Instalar el plugin `Pipeline` como se han instalados los demás plugins anteriromente.

## 12.2 Crear nuestro primer pipeline

Vamos a generar nuestro primer pipeline utilizando jenkinsfile.

- Generar un nuevo `Pipeline` project y agregar el siguiente código en la sección de `Pipeline` y dejar como `Pipeline definition`:
```
pipeline {
    agent any

    stages {
        stage('Build'){
            steps {
                echo 'Building...'
            }
        }
        stage('Test'){
            steps {
                echo 'Testing...'
            }
        }
        stage('Deploy'){
            steps {
                echo 'Deploying...'
            }
        }
    }
}
```
- Guardar y ejecutar para ver el resultado de nuestro primer pipeline, observar que se visualizan tres pasos a ejecutar.

## 12.2 Agregar multi-steps a nuestro pipeline

Vamos a modificar el código anterior con el siguiente y remplazarlo en el `Pipeline` definido, verificar que en este código se van a ejecutar varios steps dentro de un mismo stage, esto se puede realizar en todos los demás stages que existan en nuestro pipeline con diferentes operaciones:
```
pipeline {
    agent any
    stages {
        stage('Build'){
            steps {
                sh 'echo "My first pipeline"'
                sh '''
                    echo "By the way, I can do more stuff in here :)"
                    ls -lah
                    pwd
                    top
                '''
            }
        }
    }
}
```
- Ejecutar el pipeline y verificar como fueron ejecutados todos los steps pertenecientes el stage de Build.

## 12.3 Manejo de Retry

Hacer cambios en el `Pipeline` nuevamente, con el siguiente código:

```
pipeline {
    agent any
    stages {
        stage('Timeout'){
            steps {
                retry(3){
                    sh 'I am not going to work :c'
                }
            }
        }
    }
}
```
- Ejecutar el siguiente código, esto va a fallar porque el cáracter I no es válido en linux va a fallar, pero se ejecutara tres veces por el retry.

## 12.4 Manejo de Timeout

Hacer cambios en el `Pipeline` nuevamente, con el siguiente código:

```
pipeline {
    agent any
    stages {
        stage('Deploy'){
            steps {
                retry(3){
                    sh 'echo Hello'
                }

                timeout(time: 3, unit: 'SECONDS'){
                    sh 'sleep 5'
                }
            }
        }
    }
}
```
- Ejecutar el siguiente código, verificar como el resultado de que el pipeline termina por timeout.

## 12.5 Manejo de Environment variables

Hacer cambios en el `Pipeline` nuevamente, con el siguiente código:

```
pipeline {
    agent any

    environment {
        NAME = 'Federico'
        LASTNAME = 'Barcelo'
    }

    stages {
        stage('Build'){
            steps {
                sh 'echo $NAME $LASTNAME'
            }
        }
    }
}
```
- Ejecutar el siguiente código, verificar como el resultado del pipeline con las environment variables.

## 12.6 Manejo de Credentials

Hacer cambios en el `Pipeline` nuevamente, con el siguiente código:

```
pipeline {
    agent any

    environment {
        secret = credentials('SECRET-TEXT')
    }

    stages {
        stage('Example stage 1'){
            steps {
                sh 'echo $secret'
            }
        }
    }
}
```
- Alojar una nueva credencial por interfaz gráfica que se llame `SECRET-TEXT` con algún valor de su preferencia.
- Ejecutar el siguiente código, verificar como el resultado del pipeline se visualiza el echo de la credential alojada en el Jenkins.

## 12.7 Post actions

Hacer cambios en el `Pipeline` nuevamente, con el siguiente código:

```
pipeline {
    agent any

    stages {
        stage('Test'){
            steps {
                sh 'echo "Fail!"; exit 1'
            }
        }
    }
    post {
        always {
            echo 'I will always get executed :D'
        }
        success {
            echo 'I will only get executed if this success'
        }
        failure {
            echo 'I will only get executed if this fails'
        }
        unstable {
            echo 'I will only get executed if this unstable'
        }
    }
}
```
- Ir modificando la linea del sh para ver como se devuelven los mensajes en el post.
- Ejecutar el siguiente código, verificar como el resultado del pipeline las post actions.