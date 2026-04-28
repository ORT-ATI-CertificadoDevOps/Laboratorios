## EFS con AWS CLI

> **Tiempo estimado:** 20 minutos

### Prerequisitos

* Haber completado el lab anterior (EFS: Almacenamiento Compartido)

### Objetivos

Repetir el práctico de EFS usando la AWS CLI.

### Referencias

```bash
# Crear el file system
aws efs create-file-system help

# Crear el mount target (vincula el EFS a una subnet)
aws efs create-mount-target help
```

> **Nota:** Se necesita un mount target por AZ donde haya instancias. Al crearlo, asociar el Security Group que permite TCP 2049.

### Flujo de comandos

1. Crear el file system → obtener `FileSystemId`
2. Esperar a que su estado sea `available`
3. Crear un mount target en la subnet de cada instancia
4. Montar desde las instancias (igual que en el práctico anterior)

```bash
# Verificar estado del file system
aws efs describe-file-systems --file-system-id <fs-id> \
  --query 'FileSystems[0].LifeCycleState'
```

### Limpieza de recursos

```bash
# Eliminar mount target primero
aws efs delete-mount-target --mount-target-id <mt-id>

# Verificar que fue eliminado antes de borrar el file system
aws efs describe-mount-targets --file-system-id <fs-id>

# Eliminar el file system
aws efs delete-file-system --file-system-id <fs-id>
```

### Spoiler Alert

En caso de trancarse, pueden consultar la ayuda [aquí](./soluciones/3-Solucion_creando_efs-ebs-cli.md).
