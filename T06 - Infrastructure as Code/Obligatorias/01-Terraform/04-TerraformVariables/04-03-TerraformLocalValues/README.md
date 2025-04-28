# Terraform Local Values

## 01 - Introducción
- Entender el principio de DRY
- ¿Qué es un local value en Terraform?
- ¿Cuando se usa un local value?
- ¿Cual es el problema que local values esta solucionando?

```

¿Qué es el principio DRY?

No te repitas (Don't repeat yourself)

¿Qué es el local value en Terraform?

El local block define una o más variables locales dentro de un módulo.
Un local value asigna un nombre a una expresión de Terraform, lo que permite que se use varias veces dentro de un módulo sin repetirlo.

¿Cuándo utilizar local values?

Los local values pueden ser útiles para evitar repetir los mismos valores o expresiones varias veces en una configuración.
Si se usan en exceso, también pueden dificultar la lectura de una configuración para futuros mantenedores al ocultar los valores reales utilizados.
Utilice los local values solo con moderación, en situaciones en las que se utiliza un único valor o resultado en muchos lugares y es probable que ese valor cambie en el futuro. La capacidad de cambiar fácilmente el valor en un lugar central es la ventaja clave de los local values.

¿Cuál es el problema que se esta solucionando?

Actualmente, Terraform no permite la sustitución de variables dentro de las variables. La forma de Terraform de hacer esto es mediante el uso de valores locales o locales donde de alguna manera puede mantener su código DRY.

Otro caso de uso (al menos para mí, Federico) para los locales es acortar las referencias en proyectos de Terraform ascendentes como se ve a continuación. Esto hará que sus plantillas / módulos de Terraform sean más legibles.

```

## 02 - Create / Review Terraform configuration files

Revisar los siguientes archivos:

- c1-versions.tf
- c2-variables.tf
- c3-s3-bucket.tf


## 03 - Test the Terraform configuration using commands
```
# Initialize Terraform
terraform init

# Validate Terraform configuration files
terraform validate

# Format Terraform configuration files
terraform fmt

# Review the terraform plan
terraform plan 

# Create Resources (Optional)
terraform apply -auto-approve
```

## Referencias
- [Terraform Local values](https://www.terraform.io/docs/language/values/locals.html)