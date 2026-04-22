# Branches y merges

> **Tiempo estimado:** 25 minutos

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

Continuar con [3 — Herramientas avanzadas](3-Herramientas-avanzadas.md)
