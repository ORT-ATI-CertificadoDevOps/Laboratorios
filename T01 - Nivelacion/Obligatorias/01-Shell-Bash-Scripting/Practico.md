## Shell-Bash

### Puntos a tener en consideración
- Se deja un script de ejemplo en la carpeta del práctico, con la estructura necesaria para ser ejecutado como script. Verificar que los archivos que son scripts llevan la extensión `.sh`.
- Todos los scripts deben tener permisos de ejecución para poder ser invocados. Para dar permisos y ejecutar un script:
  ```bash
  chmod +x mi_script.sh   # dar permiso de ejecución
  ls -l mi_script.sh      # verificar: debe aparecer -rwxr-xr-x
  ./mi_script.sh          # ejecutar
  ```
- A continuación una imagen para verificar que los permisos se encuentran de manera correcta y el script fue ejecutado de manera exitosa:

<img src="/Extras/Imagenes/laboratorioNivelacion/Bash/scriptExample.png" title="static">

- Si pudimos realizar lo anterior sin problema, podremos empezar a realizar el práctico de Shell-Bash-Scripting.
- Las variables dentro de Bash se definen de la siguiente manera:
  - `VARIABLE="valor"`
  - EJ: `name="Federico"`
- También se pueden alojar salidas de comandos dentro de las variables:
  - `VARIABLE=$(COMANDO)`
  - EJ: `test=$(ls /)`
- Se deja esta [página](https://ss64.com/bash/) en donde pueden encontrar en detalle una gran variedad de comandos de bash.

> **Nota:** Como siempre, si tienen dudas o se encuentran trancados no tengan miedo en consultar/preguntar. La idea de los laboratorios/prácticos es hacer lo más hands-on posible de manera grupal/independiente.

---

## Conceptos previos

Antes de arrancar con los ejercicios, hay algunos conceptos clave que van a aparecer a lo largo del práctico.

### El shebang `#!/bin/bash`

La primera línea de todo script bash es el **shebang**. Le indica al sistema operativo qué intérprete usar para ejecutar el archivo:

```bash
#!/bin/bash            # usa bash en la ruta estándar
#!/usr/bin/env bash    # forma más portable: busca bash en el PATH
```

Sin esta línea, el sistema no sabe cómo ejecutar el script.

### Buenas prácticas para scripts robustos

Para scripts que van a correr en entornos reales (servidores, pipelines de CI/CD), conviene agregar estas opciones al inicio:

```bash
#!/usr/bin/env bash
set -e    # terminar si cualquier comando falla
set -u    # error si se usa una variable no definida
set -x    # imprimir cada comando antes de ejecutarlo (útil para debug)

# Combinación recomendada para scripts de producción:
set -euo pipefail
```

### Comillas en variables

Siempre usar **comillas dobles** alrededor de las variables. Sin ellas, los valores con espacios generan bugs difíciles de detectar:

```bash
RUTA="/mi directorio/con espacios"

# ❌ Peligroso: bash lo interpreta como dos argumentos separados
if [ -d $RUTA ]; then ...

# ✅ Correcto
if [ -d "$RUTA" ]; then ...
echo "Hola $NOMBRE"    # interpola la variable
echo 'Hola $NOMBRE'    # comillas simples: imprime literal $NOMBRE
```

### Redirección y pipes

Uno de los conceptos más potentes de bash:

```bash
# Pipes: encadenar la salida de un comando como entrada de otro
ls -l | grep ".sh"
cat /etc/passwd | grep root

# Redirección de salida
echo "mensaje" > archivo.txt     # sobreescribe el archivo
echo "mensaje" >> archivo.txt    # agrega al final (append)

# Redirección de errores
comando 2> errores.txt           # redirigir stderr a archivo
comando > salida.txt 2>&1        # redirigir stdout y stderr juntos
comando 2> /dev/null             # descartar errores

# Operadores lógicos con comandos
comando && echo "Éxito"          # ejecutar si el anterior tuvo éxito (exit 0)
comando || echo "Falló"          # ejecutar si el anterior falló (exit != 0)
```

### Códigos de salida y `$?`

Todo comando en bash devuelve un código de salida: `0` significa éxito, cualquier otro valor significa error. Esto es fundamental en scripting y en CI/CD:

```bash
ls /ruta/existente
echo $?    # 0

ls /ruta/que/no/existe
echo $?    # 2 (distinto de 0 = error)

# Salir de un script con un código específico
exit 0     # éxito
exit 1     # error genérico
```

### Flags de test para condicionales

Los más usados para validar rutas y archivos:

```bash
-e  # existe (cualquier tipo)
-f  # es archivo regular
-d  # es directorio
-x  # tiene permisos de ejecución
-r  # tiene permisos de lectura
-s  # existe y no está vacío
```

---

## Ejercicio 1
- Generar un archivo `ejercicio1.sh` que imprima por pantalla `"Hola NOMBRE, este es tu primer script"`.
- Reemplazar NOMBRE con tu propio nombre. Por ejemplo, si te llamás Ana, debería mostrar en pantalla `"Hola Ana, este es tu primer script"`.
- Resultado esperado (ejemplo):

<img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio1.png" title="static">

---

## Ejercicio 2
- Generar un archivo `ejercicio2.sh` para que ahora incluya el NOMBRE como una variable.
- El resultado en pantalla debe ser el mismo que el del ejercicio anterior, pero esta vez el nombre debe estar almacenado en una variable dentro del script.
- Resultado esperado (ejemplo):

<img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio1.png" title="static">

---

## Ejercicio 3
- Generar un archivo `ejercicio3.sh` que guarde en una variable `EQUIPO` el comando `hostname` y luego imprima por pantalla el siguiente mensaje: `"Este script se encuentra ejecutándose en el equipo X"`, en donde X es el valor obtenido por el comando `hostname` alojado en la variable `EQUIPO`.
- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio3.png" title="static">

---

## Ejercicio 4
- Generar un archivo `ejercicio4.sh` que valide si una determinada RUTA/PATH existe.
- Es recomendable alojar la RUTA/PATH como una variable.
- Si la RUTA **EXISTE**, se debe imprimir por pantalla `"La ruta: RUTA existe!"`
  - Adicionalmente, si se **TIENEN** permisos de ejecución sobre esa ruta, se debe imprimir por pantalla `"Tengo permisos de ejecución sobre la ruta: RUTA"`
  - Si la ruta existe pero **NO SE TIENEN** permisos de ejecución, no es necesario imprimir nada extra.
- Si la RUTA **NO EXISTE**, se debe imprimir por pantalla `"La ruta: RUTA no existe"`

> **Nota:** Apoyarse en los flags de test de la sección de conceptos previos (`-e`, `-x`). La estructura de un `if` en bash es:
> ```bash
> if [ condición ]; then
>     # comandos si se cumple
> elif [ otra_condición ]; then
>     # comandos si se cumple la segunda
> else
>     # comandos si ninguna se cumplió
> fi
> ```

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio4.png" title="static">

---

## Ejercicio 5
- Generar un archivo `ejercicio5.sh` que muestre por pantalla el nombre de 5 animales, apareciendo cada uno en una línea diferente.
- Se puede resolver usando múltiples `echo` o utilizando un array junto con un loop `for` (¡se recomienda intentar con el loop!).

> **Nota:** La sintaxis de un array y loop `for` en bash es:
> ```bash
> ANIMALES=("perro" "gato" "pez" "loro" "tortuga")
> for ANIMAL in "${ANIMALES[@]}"; do
>     echo "$ANIMAL"
> done
> ```

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio5.png" title="static">

---

## Ejercicio 6
- Generar un archivo `ejercicio6.sh` que le pida al usuario una RUTA y verifique si la misma es un directorio, un archivo regular o otro tipo de archivo.
- En caso de ser un **DIRECTORIO**, se deberá mostrar por pantalla `"La ruta: RUTA recibida es un directorio"`.
- En caso de ser un **ARCHIVO REGULAR**, se deberá mostrar por pantalla `"La ruta: RUTA recibida es un archivo regular"`.
- En caso de ser **OTRO TIPO DE ARCHIVO**, se deberá mostrar por pantalla `"La ruta: RUTA es otro tipo de archivo"`.
- Además es necesario ejecutar un `ls -l` sobre la RUTA solicitada.
- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio6(1).png" title="static">
- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio6(2).png" title="static">

---

## Ejercicio 7
- Generar un archivo `ejercicio7.sh` a partir del archivo `ejercicio6.sh`, que en lugar de recibir la ruta por pantalla, la reciba como parámetro.

> **Nota:** Los parámetros posicionales en bash se acceden con `$1`, `$2`, etc. El total de parámetros recibidos se obtiene con `$#`.

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio7.png" title="static">

---

## Ejercicio 8
- A partir del `ejercicio7.sh`, agregar una validación al inicio del script que verifique si se recibió el parámetro `$1`.
- Si **NO** se recibió ningún parámetro, el script debe imprimir por pantalla `"Error: debe ingresar una ruta como parámetro"` y terminar la ejecución con `exit 1`.
- Si **SÍ** se recibió el parámetro, el script debe continuar con la lógica anterior.

> **Nota:** Recordar que `exit 1` indica que el script terminó con error. El sistema (o pipeline de CI/CD) puede usar este código para saber si el script falló.

- Guardar el resultado como `ejercicio8.sh`.

---

## Ejercicio 9
- Generar un archivo `ejercicio9.sh` que mediante una función muestre por pantalla el número total de archivos existentes en el directorio donde se encuentra el archivo `ejercicio9.sh`.
- Definir la función como: `function contar_archivos()`

> **Nota:** Para capturar la salida de una función y usarla como valor, se puede hacer así:
> ```bash
> function contar_archivos() {
>     local total
>     total=$(ls . | wc -l)
>     echo "$total"    # retornar via stdout
> }
>
> RESULTADO=$(contar_archivos)
> echo "Total de archivos: $RESULTADO"
> ```

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio8.png" title="static">

---

## Ejercicio 10
- Generar un archivo `ejercicio10.sh` a partir del archivo `ejercicio9.sh`.
- Realizar los cambios necesarios para que la función `contar_archivos()` pueda recibir directorios como parámetros.
- Una vez realizadas las modificaciones, utilizar la función sobre los directorios:
  - `/etc`
  - `/var`
  - `/usr/bin`

> **Nota:** Dentro de una función, `$1` es el primer parámetro recibido por esa función (no el del script). Usar `local` para declarar variables locales a la función:
> ```bash
> function contar_archivos() {
>     local directorio="$1"
>     ls "$directorio" | wc -l
> }
> ```

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio9.png" title="static">

---

## Ejercicio 11
- Generar un archivo `ejercicio11.sh` que muestre un número random por pantalla `"El número random fue: xxxxx"` (utilizar el comando `$RANDOM` para obtener el número aleatorio).
- Cada vez que el `.sh` se ejecute, debe guardarse el mensaje desplegado con el número aleatorio obtenido en un archivo llamado `ejercicio11.txt`, alojado en `/tmp/`.

> **Nota:** Para agregar contenido al final de un archivo sin sobreescribirlo, usar `>>` (append):
> ```bash
> echo "mensaje" >> /tmp/ejercicio11.txt
> ```
> Se utiliza `/tmp/` ya que es accesible sin permisos de superusuario en todos los sistemas.

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio10(1).png" title="static">
- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Bash/Ejercicio10(2).png" title="static">

---

## Ejercicio 12 — Procesamiento de archivos con loop
- Generar un archivo `ejercicio12.sh` que lea el archivo `/etc/passwd` línea por línea e imprima solo el **nombre de usuario** (primer campo, separado por `:`) de cada línea.
- Guardar la salida en `/tmp/usuarios.txt` usando redirección.
- Verificar el resultado con `cat /tmp/usuarios.txt`.

> **Nota:** Para leer un archivo línea por línea se puede usar un loop `while read`:
> ```bash
> while IFS= read -r linea; do
>     echo "$linea"
> done < archivo.txt
> ```
> Para obtener el primer campo separado por `:` se puede usar `cut`:
> ```bash
> echo "root:x:0:0" | cut -d: -f1    # imprime: root
> ```

---

## Ejercicio 13 — Script con validación robusta y códigos de salida
- Generar un archivo `ejercicio13.sh` que reciba **dos argumentos**: una ruta y una extensión (ej: `./ejercicio13.sh /etc conf`).
- El script debe:
  - Validar que se recibieron exactamente 2 argumentos. Si no, imprimir `"Error: uso: ./ejercicio13.sh <directorio> <extensión>"` y terminar con `exit 1`.
  - Validar que la ruta recibida existe y es un directorio. Si no, imprimir un mensaje de error y terminar con `exit 1`.
  - Contar cuántos archivos con esa extensión hay en el directorio e imprimir: `"Se encontraron X archivos .extensión en /ruta"`.
  - Terminar con `exit 0` si todo fue exitoso.

> **Nota:** Para contar archivos por extensión se puede usar `find`:
> ```bash
> find "$DIRECTORIO" -maxdepth 1 -name "*.$EXTENSION" | wc -l
> ```

---

## Ejercicio 14 — Script de backup con fecha y log
- Generar un archivo `ejercicio14.sh` que reciba un directorio como parámetro y cree un backup comprimido del mismo con la fecha actual en el nombre del archivo.
- El backup debe generarse en `/tmp/`.
- Cada ejecución debe registrar en `/tmp/backup.log`: la fecha y hora, el directorio de origen y el nombre del archivo generado.
- Ejemplo de uso y salida esperada:
  ```bash
  ./ejercicio14.sh /home/usuario/documentos
  # Genera: /tmp/documentos_2025-06-10.tar.gz
  # Registra en /tmp/backup.log:
  # [2025-06-10 14:32:01] Backup de /home/usuario/documentos -> /tmp/documentos_2025-06-10.tar.gz
  ```

> **Nota:** Comandos útiles para este ejercicio:
> ```bash
> date "+%Y-%m-%d"               # fecha actual: 2025-06-10
> date "+%Y-%m-%d %H:%M:%S"     # fecha y hora: 2025-06-10 14:32:01
> basename /home/usuario/docs    # obtener último segmento: docs
> tar -czf archivo.tar.gz /ruta  # comprimir directorio
> ```

---

## Bonus commands!

- `chmod` — permisos de ejecución
  ```bash
  chmod +x script.sh          # dar permiso de ejecución
  chmod 755 script.sh         # rwxr-xr-x (owner: rwx, group+others: rx)
  ls -l script.sh             # verificar permisos
  ```

- `set` — opciones de seguridad y debug
  ```bash
  set -e                      # terminar si cualquier comando falla
  set -u                      # error si se usa variable no definida
  set -x                      # modo debug: imprimir cada comando
  set -o pipefail             # si cualquiera de los comandos del pipe falla, todo el pipe falla
  set -euo pipefail           # combinación recomendada para producción 
  ```

- `git config`
  ```bash
  git config --global user.name "[NOMBRE]"
  git config --global user.email "[email@domain]"
  ```

- `git add`
  ```bash
  git add file1 folder1/
  git add *
  ```

- `$?` — código de salida
  ```bash
  echo $?                     # 0 = éxito, distinto de 0 = error
  comando && echo "OK"        # ejecutar si anterior tuvo éxito
  comando || echo "Falló"     # ejecutar si anterior falló
  exit 0                      # salir con éxito
  exit 1                      # salir con error
  ```

- Redirección
  ```bash
  echo "msg" > archivo.txt    # sobreescribir
  echo "msg" >> archivo.txt   # append
  comando 2> errores.txt      # redirigir stderr
  comando 2> /dev/null        # descartar errores
  ls | grep ".sh"             # pipe entre comandos
  ```

- `while` loop
  ```bash
  CONTADOR=1
  while [ $CONTADOR -le 5 ]; do
      echo "Iteración $CONTADOR"
      CONTADOR=$((CONTADOR + 1))
  done

  while IFS= read -r linea; do
      echo "$linea"
  done < archivo.txt
  ```

- Aritmética
  ```bash
  echo $((3 * 4 + 2))        # 14
  TOTAL=$((A + B))
  echo ${#VARIABLE}           # longitud de un string
  ```
