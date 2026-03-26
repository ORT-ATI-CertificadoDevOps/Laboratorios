## Git

### Puntos a tener en consideración
- Se deja la [documentación](https://git-scm.com/docs) oficial de git por si es necesario consultar algún comando en específico.
- También se deja disponible la [página](https://ss64.com/bash/) de bash utilizada en el laboratorio anterior por si tienen necesidad de utilizar algún comando extra.
- Tener en cuenta que hay que ir realizando cada comando de a uno, no ejecuten comandos por ejecutar. Por más de estar realizando pruebas, necesitamos lograr entender qué es lo que estamos realizando, así que si se pierden un poco, consulten/pregunten **TODAS** las veces que sean necesarias, para eso estamos los docentes.

---

## Ejercicio 1 — Configuración y creación del repositorio

### 1.1 — Configuración inicial de Git

Antes de usar Git por primera vez, configurá tu identidad. Esta información queda registrada en cada commit que hagas.

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "nombre@dominio.com"
git config --global color.ui auto
git config --global init.defaultBranch main
```

> Podés verificar que todo quedó bien con `git config --list`.

### 1.2 — Autenticación SSH con GitHub

GitHub ya no permite autenticarse con usuario y contraseña por HTTPS. La forma recomendada es SSH.

**Generar una clave SSH:**

```bash
ssh-keygen -t ed25519 -C "tu@email.com"
# Presioná Enter para aceptar la ubicación por defecto
```

**Copiar la clave pública:**

```bash
cat ~/.ssh/id_ed25519.pub
# Copiá todo el contenido que se muestra
```

**Agregar la clave a GitHub:**

1. Ir a **GitHub → Settings → SSH and GPG keys → New SSH key**
2. Pegar el contenido copiado y guardar

**Verificar la conexión:**

```bash
ssh -T git@github.com
# Esperado: "Hi <usuario>! You've successfully authenticated..."
```

A partir de ahora, cuando clonen o agreguen un remote, usen la URL SSH (`git@github.com:usuario/repo.git`) en lugar de HTTPS.

### 1.3 — Crear el repositorio

- Generar un directorio llamado `practicoGit`, no importa en dónde lo ubiquen.
- Inicializar un nuevo repositorio, pueden realizarlo de dos maneras:
  - **Opción 1)** Se posicionan sobre el nuevo directorio generado y ejecutan `git init`, luego van a tener que subir el repositorio generado localmente a su cuenta de GitHub.
  - **Opción 2)** Generan el repositorio desde su cuenta personal de GitHub y luego lo clonan sobre la carpeta generada anteriormente.
- No importa por cuál opción fueron, para seguir deben verificar:
  - Tener el repositorio clonado localmente.
  - Visualizar el repositorio en su cuenta de GitHub.

> **Nota:** Al usar SSH, el remote se agrega así:
> ```bash
> git remote add origin git@github.com:TU_USUARIO/practicoGit.git
> git push -u origin main
> ```

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Git/Ejercicio1(1).png" title="static">
- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Git/Ejercicio1(2).png" title="static">

---

## Ejercicio 2 — Agregar archivos, staging y commits

### 2.1 — Crear la estructura de carpetas y el .gitignore

Antes de agregar archivos, es buena práctica crear un `.gitignore` para evitar versionar archivos que no deberían subirse al repositorio (dependencias, secrets, archivos de sistema).

```bash
cat > .gitignore << 'EOF'
# Variables de entorno (¡nunca versionar secrets!)
.env
.env.local

# Archivos de sistema
.DS_Store
Thumbs.db

# Logs
*.log
EOF
```

Ahora crear la estructura de carpetas:

- Agregar las siguientes dos carpetas sobre nuestro repositorio generado anteriormente:
  - `archivosVarios`
  - `imagenes`
- Alojar sobre la carpeta `imagenes`, 3 imágenes de su elección.
- Alojar sobre la carpeta `archivosVarios`, 2 archivos de texto con algo dentro y 1 archivo de su elección.

### 2.2 — Staging, commits y push

- Antes de agregar archivos al stage, revisá qué cambió:

```bash
git status           # archivos modificados/nuevos
git diff             # ver exactamente qué líneas cambiaron
```

- Agregar sobre nuestra zona de stage los archivos correspondientes a la carpeta `imagenes` y hacer un commit con ellos utilizando un mensaje descriptivo.

> **Nota:** Ver comandos `git add` y `git commit` para este paso.

> **Buena práctica — mensajes de commit:** Usar el estándar [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/) para escribir mensajes claros:
> ```bash
> # ❌ Malo
> git commit -m "cambios"
> git commit -m "agregué cosas"
>
> # ✅ Bueno
> git commit -m "feat: agregar imágenes iniciales al repositorio"
> git commit -m "feat: agregar archivos de texto en archivosVarios"
> git commit -m "chore: agregar .gitignore"
> ```

- Verificar que lo anterior fue realizado correctamente con `git status` y `git log`:

```bash
git log --oneline    # ver historial compacto
git log --oneline --graph    # ver historial en formato árbol
```

- Si lo anterior fue visualizado correctamente, subir el commit al repositorio centralizado.
- Verificar en la web de GitHub que el cambio que hicieron localmente se encuentra allí.
- Repetir los pasos de agregar archivos en la zona de stage para la carpeta `archivosVarios` y realizar los mismos pasos hasta que quede todo subido al repositorio centralizado.

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Git/Ejercicio2.png" title="static">

---

## Ejercicio 3 — Ramas (branches)

- Generar 2 nuevas ramas a partir de la rama `main`:
  - `develop`
  - `staging`

```bash
# Forma moderna (recomendada desde Git 2.23)
git switch -c develop
git switch main
git switch -c staging
git switch main

# Forma clásica (también válida)
git checkout -b develop
```

- Subir los cambios al repositorio centralizado y visualizar dichas ramas allí:

```bash
git push origin develop
git push origin staging

# Ver todas las ramas (locales y remotas)
git branch -a
```

> **Nota:** Apoyarse también del comando `git branch` para listar ramas y verificar en cuál están posicionados (la rama actual tiene un `*` al lado).

- Ej de la solución: <img src="/Extras/Imagenes/laboratorioNivelacion/Git/Ejercicio3.png" title="static">

---

## Ejercicio 4 — Conflictos de merge

- Posicionarse sobre la rama `develop`:

```bash
git switch develop
git branch    # verificar que estamos en develop
```

- Generar un nuevo archivo de texto en la carpeta `archivosVarios` de nombre `prueba.txt` y agregar en la primera línea el texto `"Prueba1"`.
- Agregar dicho archivo a la zona de stage, luego adicionarlo en la rama y al repositorio centralizado.

- Posicionarse sobre la rama `staging`:

```bash
git switch staging
git branch    # verificar que estamos en staging
```

- Generar un nuevo archivo de texto en la carpeta `archivosVarios` de nombre `prueba.txt` y agregar en la primera línea el texto `"Prueba1234"`.
- Agregar dicho archivo a la zona de stage, luego adicionarlo en la rama y al repositorio centralizado.

- Posicionado sobre la rama `staging`, ejecutar:

```bash
git merge develop
```

Nos vamos a traer el contenido de la rama `develop` hacia la rama `staging`.

- Si todo ocurrió de manera correcta, estaremos visualizando el siguiente conflicto: <img src="/Extras/Imagenes/laboratorioNivelacion/Git/Ejercicio4(1).png" title="static">

- Esto ocurre porque se estuvo trabajando sobre el mismo archivo en ambas ramas. Git marca el conflicto así dentro del archivo:

```
<<<<<<< HEAD
Prueba1234
=======
Prueba1
>>>>>>> develop
```

  - Todo lo que está entre `<<<<<<< HEAD` y `=======` es el contenido de la rama en la que estamos (`staging`).
  - Todo lo que está entre `=======` y `>>>>>>>` es el contenido de la rama que estamos mergeando (`develop`).

- Ver las opciones disponibles y darle a **"Accept both changes"** y guardar el archivo.
- Ver qué comandos debemos utilizar para terminar de arreglar el conflicto y que quede subido al repositorio centralizado (apoyarse de `git status`, `git commit` y `git push`).

---

## Ejercicio 5 — git stash: guardar trabajo temporalmente

Este ejercicio simula un escenario muy común: estás trabajando en un cambio y te piden resolver algo urgente en otra rama, sin poder hacer un commit todavía.

- Posicionarse sobre la rama `develop` y crear un archivo nuevo sin commitearlo:

```bash
git switch develop
echo "trabajo en progreso" > archivosVarios/wip.txt
git add archivosVarios/wip.txt
```

- Guardar el trabajo temporalmente con `stash`:

```bash
git stash push -m "WIP: archivo en progreso"

# Verificar que el working tree quedó limpio
git status

# Ver la lista de stashes guardados
git stash list
```

- Cambiar a `main` para simular el fix urgente:

```bash
git switch main
echo "fix urgente" >> README.md
git add README.md
git commit -m "fix: corregir problema urgente en main"
git push
```

- Volver a `develop` y recuperar el trabajo guardado:

```bash
git switch develop
git stash pop

# Verificar que el archivo volvió
git status
```

> **Comandos útiles de stash:**
> ```bash
> git stash list              # listar todos los stashes
> git stash show stash@{0}    # ver contenido de un stash
> git stash apply             # recuperar sin borrar de la lista
> git stash drop stash@{0}    # borrar un stash sin aplicarlo
> ```

---

## Ejercicio 6 — git tag: etiquetar un release

Los tags se usan para marcar versiones específicas del proyecto. Son fundamentales en pipelines de CI/CD para identificar qué versión fue desplegada.

- Posicionarse sobre `main` y asegurarse de tener los últimos cambios:

```bash
git switch main
git pull
```

- Crear un tag anotado para marcar la primera versión:

```bash
git tag -a v1.0.0 -m "Release v1.0.0: primera versión estable"
```

- Ver el tag creado y su detalle:

```bash
git tag                  # listar todos los tags
git show v1.0.0          # ver detalle del tag
```

- Subir el tag al repositorio centralizado:

```bash
git push origin v1.0.0

# O subir todos los tags de una sola vez
git push origin --tags
```

- Verificar en la web de GitHub que el tag aparece en la sección **Releases / Tags**.

---

## Ejercicio 7 — Deshacer cambios

### 7.1 — Corregir el último commit

Si te olvidaste un archivo o el mensaje tiene un error, podés corregir el último commit **antes de pushearlo**:

```bash
# Cambiar el mensaje del último commit
git commit --amend -m "feat: mensaje corregido"

# Agregar un archivo olvidado sin cambiar el mensaje
git add archivo_olvidado.txt
git commit --amend --no-edit
```

> ⚠️ Solo usá `--amend` si el commit **todavía no fue pusheado** al remoto.

### 7.2 — Descartar cambios no commiteados

```bash
# Descartar cambios de un archivo específico (forma moderna)
git restore nombre_archivo

# Sacar un archivo del staging sin perder los cambios
git restore --staged nombre_archivo
```

### 7.3 — Revertir un commit ya pusheado

```bash
# revert: crea un nuevo commit que deshace el anterior (SEGURO, no reescribe historia)
git revert HEAD

# Verificar el resultado
git log --oneline
git push
```

> **Diferencia clave entre `revert` y `reset`:**
>
> | Comando | Reescribe historia | Seguro en repos compartidos |
> |---|---|---|
> | `git revert` | No | ✅ Sí |
> | `git reset --soft/mixed` | Sí | ⚠️ Solo en rama local |
> | `git reset --hard` | Sí (destructivo) | ❌ No |
>
> En repositorios compartidos, siempre preferí `git revert`.

---

## Bonus commands!

- `git config`
  ```bash
  git config --global user.name "[NOMBRE]"
  git config --global user.email "[email@domain]"
  git config --global init.defaultBranch main
  git config --list
  ```

- `git add`
  ```bash
  git add file1 folder1/
  git add *
  git add .    # agregar todo el directorio actual
  ```

- `git status`
  ```bash
  git status           # estado del working tree
  git status -s        # formato corto
  ```

- `git diff`
  ```bash
  git diff                        # cambios no staged
  git diff --staged               # cambios en staging area
  git diff [branch1] [branch2]    # comparar dos ramas
  ```

- `git log`
  ```bash
  git log --oneline               # resumen compacto
  git log --oneline --graph       # árbol visual de ramas
  git log --oneline --graph --all # incluye ramas remotas
  git log --author="Nombre"       # filtrar por autor
  git log -p archivo              # cambios por archivo
  ```

- `git switch` / `git restore` (comandos modernos desde Git 2.23)
  ```bash
  git switch nombre_rama          # cambiar de rama
  git switch -c nueva_rama        # crear y cambiar de rama
  git restore archivo             # descartar cambios en working tree
  git restore --staged archivo    # sacar del staging
  ```

- `git reset`
  ```bash
  git reset [file]                # sacar archivo del staging
  git reset --soft HEAD~1         # deshacer commit, mantener cambios staged
  git reset --hard HEAD~1         # deshacer commit Y cambios (destructivo)
  ```

- `git revert`
  ```bash
  git revert HEAD                 # revertir último commit (seguro)
  git revert abc1234              # revertir un commit específico
  ```

- `git stash`
  ```bash
  git stash                       # guardar trabajo temporalmente
  git stash push -m "descripción" # guardar con nombre
  git stash list                  # listar stashes
  git stash pop                   # recuperar y borrar
  git stash apply                 # recuperar sin borrar
  git stash drop                  # borrar sin recuperar
  ```

- `git tag`
  ```bash
  git tag                         # listar tags
  git tag -a v1.0.0 -m "msg"     # crear tag anotado
  git push origin v1.0.0          # subir tag al remoto
  git push origin --tags          # subir todos los tags
  ```

- `git fetch`
  ```bash
  git fetch origin                # descargar cambios sin mergear
  git pull --rebase               # fetch + rebase (historial más limpio)
  ```

- `git shortlog`
- `git whatchanged`
- archivo _.gitignore_

---

## Ejercicio Integrador — Fase 1: Tu Portfolio DevOps

A lo largo del curso vas a construir y publicar tu propio **portfolio/CV online**. Este es el hilo conductor del ciclo: lo dockerizarás, desplegarás automáticamente con GitHub Actions y analizarás su calidad con SonarCloud.

| Fase | Lab | Qué vas a hacer |
|------|-----|----------------|
| **1 — esta fase** | T01 Git | Clonar el template, personalizar, primeros commits |
| 2 | T02 Docker | Construir y correr el portfolio dentro de nginx |
| 3 | T02 GitHub Actions | Deploy automático a GitHub Pages |
| 4 | T02 SonarCloud | Análisis de calidad del código |

El equipo docente provee un template base listo para usar en:
`git@github.com:ORT-ATI-CertificadoDevOps/portfolio-template.git`

### Tarea: crear tu repositorio y subir el template

**1. Creá tu repositorio en GitHub**

- Nombre recomendado: `portfolio-devops`
- Visibilidad: **Public** (necesario para GitHub Pages en fases siguientes)
- Inicializalo con un `README.md`

**2. Cloná el template y apuntalo a tu repositorio**

```bash
# Cloná el template del portfolio
git clone git@github.com:ORT-ATI-CertificadoDevOps/portfolio-template.git
cd portfolio-template

# Reemplazá el remote origin por tu repositorio personal
git remote remove origin
git remote add origin git@github.com:TU_USUARIO/portfolio-devops.git
git push -u origin main
```

**3. Personalizá el portfolio**

Abrí `index.html` en un editor y buscá los comentarios `<!-- TODO: -->`.
Hay **8 puntos** para personalizar: nombre, rol, descripción, habilidades, proyectos, educación y contacto.

**4. Commiteá los cambios**

```bash
git add index.html style.css
git commit -m "feat: personalizar portfolio con información propia"
git push origin main
```

> Para la guía completa con todos los pasos y el checklist de verificación, consultá el archivo `ejercicio-integrador.md` dentro del template.

> **Próximas fases:** En Docker vas a construir la imagen del portfolio en nginx. En GitHub Actions vas a automatizar el deploy a GitHub Pages con cada push.