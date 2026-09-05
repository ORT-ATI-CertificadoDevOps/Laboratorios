# Terraform Configuration Language Syntax

> **Tiempo estimado:** 20 minutos

La configuración de Terraform se escribe en HCL (HashiCorp Configuration Language), un lenguaje declarativo diseñado para describir infraestructura. En este laboratorio vas a aprender la sintaxis **rompiendo y arreglando código**: corregir errores, consultar atributos de recursos y reparar un archivo con todos los bloques de nivel superior.

### Puntos a tener en consideración
- **No se necesitan credenciales de AWS.** Los ejercicios usan los providers `random` y `local` (crean recursos en tu máquina) o solo ejecutan `validate`.
- `terraform fmt` detecta errores de **sintaxis**; `terraform validate` detecta errores de **esquema** (argumentos o referencias que no existen).
- Corregí los errores **leyendo el mensaje**, no adivinando.

---

## 01 - Lo mínimo que tenés que saber

```hcl
resource "aws_instance" "ec2demo" {      # block type + 2 labels (tipo, nombre)
  ami           = "ami-0953476d60561c955" # argument: valor que vos asignás
  instance_type = var.instance_type       # argument con valor de una expresión
}
# attribute: valor que expone el provider → aws_instance.ec2demo.public_ip
# meta-argument: argumento de Terraform que cambia el comportamiento → count, for_each, depends_on, lifecycle
```

Referencia: [Configuration Syntax](https://developer.hashicorp.com/terraform/language/syntax/configuration)

---

## 02 - Ejercicio 1: Sintaxis (5 min)

📁 `terraform-manifests/ejercicio-01-sintaxis/main.tf` — tiene **6 errores**.

```bash
cd terraform-manifests/ejercicio-01-sintaxis
terraform fmt        # errores de sintaxis
terraform init
terraform validate   # errores de esquema
```

Repetí hasta obtener `Success! The configuration is valid.` Anotá qué comando detectó cada error.

### 🔎 Investigá
- ¿Por qué `validate` necesita `init` y `fmt` no?
- ¿Cuándo se usa `=` (`tags = { }`) y cuándo no (`lifecycle { }`)? ¿Qué reglas debe cumplir un identificador? → secciones *Arguments and Blocks* e *Identifiers* de la [documentación de sintaxis](https://developer.hashicorp.com/terraform/language/syntax/configuration).

---

## 03 - Ejercicio 2: Atributos y meta-argumentos (8 min)

📁 `terraform-manifests/ejercicio-02-argumentos-atributos/main.tf`

```bash
cd terraform-manifests/ejercicio-02-argumentos-atributos
terraform init && terraform apply
terraform console
> local_file.saludo.content_md5
> exit
```

1. En el `plan`, ¿qué valores aparecen como `(known after apply)` y por qué?
2. Agregá un `output` que muestre el SHA256 del archivo. Buscá el nombre del atributo en la [documentación de `local_file`](https://registry.terraform.io/providers/hashicorp/local/latest/docs/resources/file) (*Attributes Reference*).
3. Agregá `count = 3` a `random_pet.nombre` y ejecutá `terraform validate`. Corregí las referencias para que el archivo use el **primer** nombre y el output muestre **los tres**. Aplicá y revisá `terraform state list`.

### 🔎 Investigá
- ¿Cómo se referencia una instancia con `count`? ¿Qué es `[*]`? → [References](https://developer.hashicorp.com/terraform/language/expressions/references) y [Splat Expressions](https://developer.hashicorp.com/terraform/language/expressions/splat).
- ¿Cómo sabe Terraform que `local_file.saludo` depende de `random_pet.nombre` sin `depends_on`?

```bash
terraform destroy && rm -rf .terraform* terraform.tfstate* salida/
```

---

## 04 - Ejercicio 3: Top-Level Blocks (5 min)

📁 `terraform-manifests/ejercicio-03-top-level-blocks/main.tf` — contiene los **8 tipos de bloques de nivel superior** y tiene **5 errores**.

```bash
cd terraform-manifests/ejercicio-03-top-level-blocks
terraform init -backend=false   # no se conecta al backend S3
terraform validate
```

1. Corregí los errores sin borrar bloques.
2. Para cada bloque, anotá su tipo y cuántos labels lleva (0, 1 o 2).

### 🔎 Investigá
- ¿Qué prefijo se usa para referenciar cada tipo de bloque (`var.`, `local.`, `data.`, `module.`)?
- `validate` aceptó el bucket `terraform-DevOpsLab`, pero AWS lo rechazaría. ¿Por qué? ¿Qué no puede verificar `validate`?

Cada uno de estos bloques se ve en detalle en los laboratorios siguientes.

---

## Referencias
- [Configuration Syntax](https://developer.hashicorp.com/terraform/language/syntax/configuration)
- [Meta-Arguments](https://developer.hashicorp.com/terraform/language/meta-arguments)
- [References to Named Values](https://developer.hashicorp.com/terraform/language/expressions/references)

---

Continuar con [02-01 — Terraform Block](../../02-TerraformFundamentalBlocks/02-01-Block/README.md)
