# Configuración y commits

> **Tiempo estimado:** 25 minutos

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

Continuar con [2 — Branches y merges](2-Branches-y-merges.md)
