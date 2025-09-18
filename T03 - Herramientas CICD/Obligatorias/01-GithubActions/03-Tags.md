# Laboratorio: Gestión Automática de Etiquetas en Issues con GitHub Actions

## Objetivos

Al finalizar este laboratorio, el estudiante será capaz de:

- Crear workflows de GitHub Actions para automatizar la gestión de issues
- Utilizar GitHub CLI en workflows para interactuar con la API de GitHub
- Configurar triggers de eventos para responder a cambios en issues
- Implementar automatización para mejorar el flujo de trabajo del proyecto

## Introducción

En este laboratorio aprenderemos a utilizar GitHub Actions para automatizar la gestión de issues mediante la adición automática de etiquetas. Esta funcionalidad es especialmente útil para equipos que manejan un alto volumen de issues y necesitan clasificarlos de manera eficiente.

La automatización que implementaremos agregará automáticamente etiquetas específicas cuando se abran o reabran issues, facilitando la posterior clasificación y asignación de tareas.

## Prerrequisitos

- Una cuenta de GitHub activa
- Acceso de escritura a un repositorio (puede ser uno existente o crear uno nuevo)
- Conocimientos básicos de YAML
- Familiaridad con conceptos básicos de GitHub Actions

## Paso 1: Preparación del Repositorio

### 1.1 Seleccionar o Crear un Repositorio

Para este laboratorio necesitas acceso de escritura a un repositorio. Puedes:

**Opción A**: Usar un repositorio existente
- Usar el repositorio creado en el laboratorio anterior

**Opción B**: Crear un nuevo repositorio
1. Ve a [GitHub](https://github.com) y haz clic en el botón "New repository"
2. Asigna un nombre descriptivo (ej: `github-actions-labels-lab`)
3. Marca la opción "Add a README file"
4. Haz clic en "Create repository"

### 1.2 Verificar Etiquetas Existentes

1. En tu repositorio, navega a la pestaña **Issues**
2. Haz clic en **Labels** para ver las etiquetas disponibles
3. Si no existe la etiqueta "triage", créala:
   - Haz clic en **New label**
   - Nombre: `triage`
   - Descripción: `Needs initial review and classification`
   - Color: Elige un color distintivo (ej: amarillo)
   - Haz clic en **Create label**

## Paso 2: Crear el Workflow

### 2.1 Estructura de Directorios

Los workflows de GitHub Actions deben ubicarse en la ruta `.github/workflows/` dentro del repositorio.

1. En tu repositorio, navega a la raíz
2. Si no existe, crea la carpeta `.github`
3. Dentro de `.github`, crea la carpeta `workflows`

### 2.2 Crear el Archivo del Workflow

1. Dentro de `.github/workflows/`, crea un nuevo archivo llamado `label-issues.yml`
2. Copia el siguiente contenido YAML:

```yaml
name: Label issues

on:
  issues:
    types:
      - reopened
      - opened

jobs:
  label_issues:
    runs-on: ubuntu-latest
    permissions:
      issues: write
    steps:
      - run: gh issue edit "$NUMBER" --add-label "$LABELS"
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GH_REPO: ${{ github.repository }}
          NUMBER: ${{ github.event.issue.number }}
          LABELS: triage
```

### 2.3 Confirmar los Cambios

1. Haz scroll hacia abajo hasta la sección "Commit new file"
2. Agrega un mensaje de commit descriptivo: `Add workflow to auto-label new issues`
3. Opcionalmente, agrega una descripción más detallada
4. Selecciona "Commit directly to the main branch"
5. Haz clic en **Commit new file**

## Paso 3: Entender el Workflow

### 3.1 Análisis del Código

Revisemos cada sección del workflow:

**Trigger del Evento:**
```yaml
on:
  issues:
    types:
      - reopened
      - opened
```
- Se ejecuta cuando un issue es abierto (`opened`) o reabierto (`reopened`)

**Configuración del Job:**
```yaml
jobs:
  label_issues:
    runs-on: ubuntu-latest
    permissions:
      issues: write
```
- Ejecuta en Ubuntu
- Requiere permisos de escritura en issues

**Variables de Entorno:**
```yaml
env:
  GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
  GH_REPO: ${{ github.repository }}
  NUMBER: ${{ github.event.issue.number }}
  LABELS: triage
```
- `GH_TOKEN`: Token de autenticación automático
- `GH_REPO`: Nombre del repositorio actual
- `NUMBER`: Número del issue que disparó el evento
- `LABELS`: Etiquetas a agregar (separadas por comas si son múltiples)

## Paso 4: Probar el Workflow

### 4.1 Crear un Issue de Prueba

1. Ve a la pestaña **Issues** de tu repositorio
2. Haz clic en **New issue**
3. Completa los siguientes campos:
   - **Title**: `Test del workflow de etiquetas automáticas`
   - **Description**: 
     ```
     Este issue sirve para probar que el workflow de GitHub Actions
     funciona correctamente agregando la etiqueta "triage" automáticamente.
     ```
4. Haz clic en **Submit new issue**

### 4.2 Verificar la Ejecución del Workflow

1. Ve a la pestaña **Actions** de tu repositorio
2. Deberías ver una nueva ejecución del workflow "Label issues"
3. Haz clic en la ejecución para ver los detalles
4. Revisa que el job se haya completado exitosamente

### 4.3 Confirmar que se Agregó la Etiqueta

1. Regresa al issue que creaste
2. Verifica que aparezca la etiqueta "triage" en el panel lateral derecho
3. Si no aparece, revisa los logs del workflow para identificar posibles errores

## Paso 5: Personalización Avanzada

### 5.1 Agregar Múltiples Etiquetas

Modifica la variable `LABELS` para agregar múltiples etiquetas:

```yaml
LABELS: triage,needs-review,enhancement
```

**Nota**: Las etiquetas deben existir previamente en el repositorio.

### 5.2 Etiquetas Condicionales

Para agregar lógica condicional basada en el contenido del issue, modifica el workflow:

```yaml
name: Smart issue labeling

on:
  issues:
    types:
      - opened

jobs:
  label_issues:
    runs-on: ubuntu-latest
    permissions:
      issues: write
    steps:
      - name: Add bug label for bug reports
        if: contains(github.event.issue.title, 'bug') || contains(github.event.issue.body, 'bug')
        run: gh issue edit "$NUMBER" --add-label "bug,triage"
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GH_REPO: ${{ github.repository }}
          NUMBER: ${{ github.event.issue.number }}
      
      - name: Add feature label for feature requests
        if: contains(github.event.issue.title, 'feature') || contains(github.event.issue.body, 'feature')
        run: gh issue edit "$NUMBER" --add-label "enhancement,triage"
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GH_REPO: ${{ github.repository }}
          NUMBER: ${{ github.event.issue.number }}
      
      - name: Add default triage label
        if: "!contains(github.event.issue.title, 'bug') && !contains(github.event.issue.title, 'feature')"
        run: gh issue edit "$NUMBER" --add-label "triage"
        env:
          GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          GH_REPO: ${{ github.repository }}
          NUMBER: ${{ github.event.issue.number }}
```

### 5.3 Notificaciones Adicionales

Agrega un paso para comentar en el issue:

```yaml
- name: Add welcome comment
  run: |
    gh issue comment "$NUMBER" --body "¡Gracias por reportar este issue! 
    Ha sido etiquetado automáticamente para revisión inicial. 
    Un miembro del equipo lo revisará pronto."
  env:
    GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    GH_REPO: ${{ github.repository }}
    NUMBER: ${{ github.event.issue.number }}
```

## Paso 6: Casos de Uso Adicionales

### 6.1 Etiquetado por Autor

```yaml
- name: Label issues from external contributors
  if: github.event.issue.author_association == 'NONE'
  run: gh issue edit "$NUMBER" --add-label "external-contribution"
  env:
    GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    GH_REPO: ${{ github.repository }}
    NUMBER: ${{ github.event.issue.number }}
```

### 6.2 Etiquetado por Plantillas

Si usas plantillas de issues, puedes crear lógica basada en su estructura:

```yaml
- name: Label based on issue template
  run: |
    if [[ "$BODY" == *"## Bug Report"* ]]; then
      gh issue edit "$NUMBER" --add-label "bug,triage"
    elif [[ "$BODY" == *"## Feature Request"* ]]; then
      gh issue edit "$NUMBER" --add-label "enhancement,triage"
    else
      gh issue edit "$NUMBER" --add-label "triage"
    fi
  env:
    GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    GH_REPO: ${{ github.repository }}
    NUMBER: ${{ github.event.issue.number }}
    BODY: ${{ github.event.issue.body }}
```

## Paso 7: Mejores Prácticas

### 7.1 Gestión de Errores

Agrega manejo de errores al workflow:

```yaml
- name: Add labels with error handling
  run: |
    if ! gh issue edit "$NUMBER" --add-label "$LABELS"; then
      echo "Failed to add labels to issue #$NUMBER"
      exit 1
    fi
  env:
    GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    GH_REPO: ${{ github.repository }}
    NUMBER: ${{ github.event.issue.number }}
    LABELS: triage
```

### 7.2 Logging Detallado

```yaml
- name: Add labels with logging
  run: |
    echo "Processing issue #$NUMBER"
    echo "Adding labels: $LABELS"
    gh issue edit "$NUMBER" --add-label "$LABELS"
    echo "Labels added successfully"
  env:
    GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    GH_REPO: ${{ github.repository }}
    NUMBER: ${{ github.event.issue.number }}
    LABELS: triage
```

## Ejercicios Prácticos

### Ejercicio 1: Etiquetado Básico
1. Implementa el workflow básico del laboratorio
2. Crea 3 issues de prueba diferentes
3. Verifica que todos reciban la etiqueta "triage"

### Ejercicio 2: Etiquetado Condicional
1. Crea etiquetas adicionales: "bug", "enhancement", "question"
2. Modifica el workflow para etiquetar automáticamente basado en palabras clave en el título
3. Crea issues con títulos que contengan "bug", "feature" y "help" para probar

### Ejercicio 3: Workflow Completo
1. Implementa etiquetado condicional
2. Agrega comentarios automáticos de bienvenida
3. Incluye manejo de errores y logging
4. Prueba con diferentes tipos de issues

## Troubleshooting

### Problemas Comunes

**Error: "Resource not accessible by integration"**
- Verifica que el workflow tenga los permisos correctos
- Asegúrate de que la sección `permissions` incluya `issues: write`

**Las etiquetas no se agregan**
- Confirma que las etiquetas existen en el repositorio
- Revisa los logs del workflow para errores específicos
- Verifica la sintaxis YAML del archivo

**El workflow no se ejecuta**
- Confirma que el archivo esté en `.github/workflows/`
- Verifica que el formato YAML sea válido
- Asegúrate de que los triggers estén configurados correctamente

### Verificación de Sintaxis

Usa herramientas online como [YAML Validator](http://www.yamllint.com/) para verificar la sintaxis de tu workflow.

## Recursos Adicionales

- [Documentación oficial de GitHub Actions](https://docs.github.com/en/actions)
- [GitHub CLI Manual](https://cli.github.com/manual/)
- [Eventos que disparan workflows](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows)
- [Contexto de GitHub en workflows](https://docs.github.com/en/actions/learn-github-actions/contexts)

## Conclusión

En este laboratorio has aprendido a:

1. Crear workflows de GitHub Actions para automatizar la gestión de issues
2. Utilizar GitHub CLI para interactuar con la API de GitHub desde un workflow
3. Configurar triggers basados en eventos de issues
4. Implementar lógica condicional para etiquetado inteligente
5. Aplicar mejores prácticas para workflows robustos y mantenibles

La automatización de tareas repetitivas como el etiquetado de issues es fundamental para mantener proyectos organizados y facilitar la colaboración en equipos de desarrollo. Estas técnicas pueden extenderse para automatizar muchas otras tareas de gestión de proyectos en GitHub.

## Evaluación

Para completar satisfactoriamente este laboratorio, debes:

1. ✅ Crear un workflow funcional que etiquete automáticamente issues nuevos
2. ✅ Probar el workflow creando al menos 2 issues diferentes
3. ✅ Implementar al menos una personalización (etiquetado condicional, comentarios automáticos, etc.)
4. ✅ Documentar cualquier problema encontrado y su solución
5. ✅ Explicar cómo este tipo de automatización mejora el flujo de trabajo del proyecto

**Entrega**: Proporciona el enlace a tu repositorio con el workflow implementado y evidencia de su funcionamiento (capturas de pantalla de issues etiquetados automáticamente).