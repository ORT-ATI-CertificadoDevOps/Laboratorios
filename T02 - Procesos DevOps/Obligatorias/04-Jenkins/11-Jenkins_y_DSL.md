# 11 - Jenkins + DSL

Vamos a aprender a utilizar DSL, en donde básicamente podremos definir todos los jobs trabajados anteriormente de forma de código, sin tener la necesidad de estar realizando las tareas directamente sobre la consola.

[Link documentación DSL](https://jenkinsci.github.io/job-dsl-plugin/)

## 11.1 - Instalar plugin de DSL

Ir a los plugins en la interfaz gráfica del Jenkins e el plugin de Job DSL.

## 11.2 - Generar nuevo freestyle project
 
Vamos a generar un nuevo freestyle el cual usaremos para empezar a trabajar con DSL e iremos verificando como se refleja lo escrito en DSL sobre el job generado, en este en especifico vamos a generar un nuevo job a partir de DSL.

- Generar un nuevo freestyle project llamada `job-dsl`.
- En la pestaña de `Build` elegir la opción de `Process Job DSLs`.
- Agregar el siguiente DSL Script:
```
job('job_dsl_created'){

}
```
- Ejecutar el job y verificar que se genero un nuevo job llamado `job_dsl_created`.
- Verificar en la documentación el comando `job` de DSL y el ejemplo sugerido por la misma.

## 11.3 - Cambiar description con DSL

- Modificar el código del freestyle project `job-dsl` para cambiar la description de `job_dsl_example`:
```
job('job_dsl_created'){

    description('This is my awesome job')
}
```
- Ejecutar para verificar que se realizo el cambio de description.

## 11.4 - Cambiar para tener parámetros con DSL

- Modificar el código del freestyle project `job-dsl` para cambiar tener parámetros en `job_dsl_example`:
```
job('job_dsl_created'){

    description('This is my awesome job')

    parameters {
        stringParam('Planet', defaultValue = 'world', description = 'This is the world')
        booleanParam('FLAG', true)
        choiceParam('OPTION', ['option 1 (default)', 'option 2', 'option 3'])
    }
}
```
- Ejecutar para verificar que se realizo la adición de los parámetros.

## 11.5 - Cambiar para usar SCM (Git) con DSL

- Modificar el código del freestyle project `job-dsl` para cambiar usar SCM en `job_dsl_example`:
```
job('job_dsl_created'){

    description('This is my awesome job')

    parameters {
        stringParam('Planet', defaultValue = 'world', description = 'This is the world')
        booleanParam('FLAG', true)
        choiceParam('OPTION', ['option 1 (default)', 'option 2', 'option 3'])
    }

    scm {
        git('https://github.com/jenkins-docs/simple-java-maven-app', 'master')
    }
}
```
- Ejecutar para verificar que se realizo el cambio en SCM con lo detallado anteriormente.

## 11.6 - Cambiar para usar triggers con DSL

- Modificar el código del freestyle project `job-dsl` para cambiar usar triggers en `job_dsl_example`:
```
job('job_dsl_created'){

    description('This is my awesome job')

    parameters {
        stringParam('Planet', defaultValue = 'world', description = 'This is the world')
        booleanParam('FLAG', true)
        choiceParam('OPTION', ['option 1 (default)', 'option 2', 'option 3'])
    }

    scm {
        git('https://github.com/jenkins-docs/simple-java-maven-app', 'master')
    }

    triggers {
        cron('H 5 * * 7')
    }
}
```
- Ejecutar para verificar que se realizo el cambio en triggers con lo detallado anteriormente.

## 11.7 - Cambiar para usar steps con DSL

- Modificar el código del freestyle project `job-dsl` para cambiar usar steps en `job_dsl_example`:
```
job('job_dsl_created'){

    description('This is my awesome job')

    parameters {
        stringParam('Planet', defaultValue = 'world', description = 'This is the world')
        booleanParam('FLAG', true)
        choiceParam('OPTION', ['option 1 (default)', 'option 2', 'option 3'])
    }

    scm {
        git('https://github.com/jenkins-docs/simple-java-maven-app', 'master')
    }

    triggers {
        cron('H 5 * * 7')
    }

    steps {
        shell("echo 'Hello World'")
    }
}
```
- Ejecutar para verificar que se realizo el cambio en steps con lo detallado anteriormente.

## 11.8 - Cambiar para usar mailer con DSL

- Modificar el código del freestyle project `job-dsl` para cambiar usar mailer en `job_dsl_example`:
```
job('job_dsl_created'){

    description('This is my awesome job')

    parameters {
        stringParam('Planet', defaultValue = 'world', description = 'This is the world')
        booleanParam('FLAG', true)
        choiceParam('OPTION', ['option 1 (default)', 'option 2', 'option 3'])
    }

    scm {
        git('https://github.com/jenkins-docs/simple-java-maven-app', 'master')
    }

    triggers {
        cron('H 5 * * 7')
    }

    steps {
        shell("echo 'Hello World'")
    }

    publishers {
        mailer('test@test.com', true, true)
    }
}
```
- Ejecutar para verificar que se realizo el cambio en Post-build Actions con lo detallado anteriormente.

## 11.9 - Recrear el Ansible job usando DSL

- Generar un nuevo freestyle project como el anterior de DSL y usar el siguiente código:
```
job('ansible-users-db-dsl'){

    description('Update the html table based on the input')

    parameters {
        choiceParam('AGE', ['21', '22', '23', '24', '25'])
    }

    steps {
        
        wrappers {
            colorizeOutput(colorMap = 'xterm')
        }
        ansiblePlaybook('/var/jenkins_home/ansible/people.yml') {
            inventoryPath('/var/jenkins_home/ansible/hosts')
            colorizedOutput(true)
            extraVars {
                extraVar("PEOPLE_AGE", '${AGE}', false)
            }
        }
    }
}
```
- Ejecutar para verificar que se realizo el cambio en el job ansible-users-db-sal y verificar su funcionamiento.

## Próximos pasos
Para el siguiente paso del laboratorio, diríjase a [12 - Jenkins y Pipeline](12-Jenkins_y_Pipeline_Jenkinsfile.md)