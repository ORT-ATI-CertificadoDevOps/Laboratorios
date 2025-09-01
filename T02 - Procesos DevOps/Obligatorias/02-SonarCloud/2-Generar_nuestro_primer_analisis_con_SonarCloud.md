# Laboratorio 2 - Análisis de Código Automático con SonarQube

Ya con todo configurado en el ambiente, es hora de ensuciarnos las manos 💪. En esta sección vas a crear un repositorio nuevo y configurarlo para que cada vez que integres cambios, se ejecute un análisis de código de forma automática mediante GitHub Actions.

## 2.1 Crear repositorio con código de ejemplo

### Paso 1: Crear el repositorio en GitHub

1. Ve a tu cuenta de GitHub
2. Haz clic en **"New repository"**
3. Configura el repositorio con los siguientes datos:
   - **Nombre**: `PracticoSonarQube`
   - **Visibilidad**: **Público**
   - **Inicializar**: Marca "Add a README file"

<p align="center">
<img src="/Extras/Imagenes/laboratorioSonarCloud/imagenRepo.png" width=100%>
</p>

### Paso 2: Subir el código de ejemplo

1. Descarga el archivo:

<a href="Extras/lab-sonarqube.zip" download>
  <span>lab-sonarqube.zip</span>
  <img src="/Extras/Imagenes/zip.png" style="vertical-align: middle; margin-left: 5px;width: 30px;height: 30px;">
</a>

2. Extrae el contenido
3. Sube los archivos a tu repositorio usando cualquiera de estos métodos:
   - **Interfaz web**: Arrastra y suelta los archivos en GitHub
   - **Git CLI**: Clona el repo, copia los archivos y haz push
   - **GitHub Desktop**: Si prefieres una interfaz gráfica

---

## 2.2 Configurar SonarCloud

### Paso 1: Conectar GitHub con SonarCloud

1. Ve a [SonarCloud.io](https://sonarcloud.io)
2. Haz clic en **"Log in"** y selecciona **"With GitHub"**
3. Autoriza el acceso a tu cuenta de GitHub

### Paso 2: Importar tu organización

1. En SonarCloud, haz clic en **"Import an organization"**
2. Selecciona tu cuenta de GitHub
3. **Importante**: Solo selecciona el repositorio `PracticoSonarQube` que acabas de crear

<p align="center">
<img src="/Extras/Imagenes/laboratorioSonarCloud/configSonar.png" width=100%>
</p>

4. Haz clic en **"Install and Authorize"**

### Paso 3: Crear la organización

1. Asigna un nombre memorable a tu organización (ej: `devops-ort`)
2. Selecciona **"Free plan"** 
3. Haz clic en **"Create Organization"**

### Paso 4: Configurar el proyecto

1. En la sección **"Analyze projects"**, selecciona tu repositorio `PracticoSonarQube`
2. Haz clic en **"Set Up"**
3. En la configuración, selecciona **"Previous version"**
4. Haz clic en **"Create project"**

Si todo salió bien, deberías llegar a una pantalla similar a esta:

<p align="center">
<img src="/Extras/Imagenes/laboratorioSonarCloud/configSonar2.png" width=100%>
</p>

---

## 2.3 Integrar SonarQube con GitHub Actions

Vamos a configurar la integración entre SonarCloud y [GitHub Actions](https://github.com/features/actions) para automatizar el análisis de código.

> 📋 **¿Qué son las GitHub Actions?** Son herramientas de automatización que permiten ejecutar pipelines de CI/CD. En nuestro caso, las usaremos para analizar el código automáticamente cada vez que se hagan cambios en ciertas ramas.

### Paso 1: Configurar el token secreto

SonarCloud te mostrará un asistente con 3 pasos. Empezamos con el primero:

1. **Copia el token** que SonarCloud te proporciona
2. Ve a tu repositorio en GitHub
3. Navega a **Settings** → **Secrets and variables** → **Actions**
4. Haz clic en **"New repository secret"**
5. Configura:
   - **Name**: `SONAR_TOKEN`
   - **Value**: Pega el token que copiaste de SonarCloud
6. Haz clic en **"Add secret"**

> 🔒 **¿Por qué usar secrets?** Los secrets permiten guardar información sensible (como API keys) de forma segura, sin exponerla en el código.

### Paso 2: Crear el workflow de GitHub Actions

1. En tu repositorio, crea la siguiente estructura de carpetas: `.github/workflows/`
2. Dentro de `workflows/`, crea un archivo llamado `build.yml`
3. Agrega el siguiente contenido:

```yaml
name: Build
on:
  push:
    branches:
      - main
  pull_request:
    types: [opened, synchronize, reopened]
jobs:
  sonarqube:
    name: SonarQube
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Shallow clones should be disabled for a better relevancy of analysis
      - name: SonarQube Scan
        uses: SonarSource/sonarqube-scan-action@v5
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

> 💡 **Explicación del workflow**:
> - Se ejecuta en cada push a `main` y en pull requests
> - Usa Ubuntu como sistema operativo
> - Hace checkout del código completo (`fetch-depth: 0`)
> - Ejecuta el análisis usando el token secreto

### Paso 3: Configurar las propiedades de SonarQube

El tercer paso del asistente te pedirá configurar el archivo de propiedades:

1. Busca el archivo `sonar-project.properties` en tu repositorio (debería estar incluido en el código de ejemplo)
2. Ábrelo y actualiza los siguientes valores:
   - `sonar.organization`: El ID de tu organización en SonarCloud
   - `sonar.projectKey`: El project key (aparece en la URL de tu proyecto en SonarCloud)

Ejemplo del archivo:
```properties
sonar.projectKey=tu-usuario_PracticoSonarQube
sonar.organization=tu-usuario-devops
sonar.sources=.
sonar.exclusions=**/*test*/**
```

---

## 2.4 Verificar la configuración

### Validar GitHub Actions

1. Ve a tu repositorio en GitHub
2. Haz clic en la pestaña **"Actions"**
3. Deberías ver tu workflow ejecutándose o completado

<p align="center">
<img src="/Extras/Imagenes/laboratorioSonarCloud/configFinal1.png" width=100%>
</p>

### Validar SonarCloud

1. Regresa a tu proyecto en SonarCloud
2. Deberías ver el análisis completado con métricas como:
   - Líneas de código analizadas
   - Issues encontrados
   - Coverage (si aplica)
   - Duplicaciones

<p align="center">
<img src="/Extras/Imagenes/laboratorioSonarCloud/configFinal2.png" width=100%>
</p>

<p align="center">
<img src="/Extras/Imagenes/laboratorioSonarCloud/configFinal3.png" width=100%>
</p>

---

## 🎉 ¡Felicitaciones!

Si llegaste hasta aquí y ves resultados similares a las imágenes, has configurado exitosamente:

✅ Un repositorio de GitHub con el código de ejemplo  
✅ Integración automática con SonarCloud  
✅ Pipeline de análisis con GitHub Actions  
✅ Análisis automático en cada cambio de código  

### Para los curiosos:

- Explorar las diferentes métricas que SonarCloud proporciona
- Investigar los "Issues" encontrados en tu código
- Configurar Quality Gates personalizados
- Prueba hacer cambios en el código y observa cómo se ejecuta el análisis automático

> 💡 **Para seguir aprendiendo**: Dedica unos minutos a navegar por ambas interfaces (GitHub Actions y SonarCloud) para familiarizarte con todas las opciones disponibles.