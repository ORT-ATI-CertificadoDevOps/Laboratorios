## Instancias Windows en EC2

> **Tiempo estimado:** 20 minutos

### Contexto

EC2 soporta instancias Windows Server además de Linux. El acceso se realiza via **RDP (Remote Desktop Protocol)** en lugar de SSH. AWS gestiona el cifrado de la contraseña de administrador usando el Key Pair de la instancia.

### Objetivos

* Desplegar una instancia Windows Server en EC2
* Obtener y descifrar la contraseña de administrador
* Conectarse via RDP

### Despliegue

* Nombre: `test-instance-windows01` (Tag `Name`)
* AMI: `Microsoft Windows Server 2022 Base` *(buscar "Windows Server 2022"; el ID puede variar por región)*
* Tipo: `t2.medium`
* AZ: `us-east-1a`
* Key Pair: `vockey`
* Security Group: permitir `RDP (TCP 3389)` desde tu IP

### Conexión vía RDP

1. Esperar a que `Status Check` pase a `2/2 checks passed` *(Windows tarda más que Linux en estar listo)*
2. `EC2 > Instances` → seleccionar la instancia → `Connect` → pestaña `RDP client`
3. Click en `Get password` → subir el `.pem` del Key Pair `vockey` → `Decrypt password`
4. Descargar el archivo `.rdp` y abrirlo:
   * **Windows:** Remote Desktop Connection *(incluido en el OS)*
   * **Mac:** Microsoft Remote Desktop *(App Store)*
   * **Linux:** Remmina o `xfreerdp`
5. Conectarse con usuario `Administrator` y la contraseña descifrada

### Para discutir en grupo

* ¿Cuánto tardó la instancia en estar lista vs una instancia Linux?
* ¿Qué diferencias hay entre el flujo SSH (Linux) y RDP (Windows)?
* ¿Qué casos de uso justificarían usar Windows en la nube?

### Limpieza de recursos

* `EC2 > Instances` → terminar `test-instance-windows01`
* `EC2 > Security Groups` → eliminar el SG creado para RDP

### Spoiler Alert

Si no se puede obtener la contraseña, verificar:
* Que el Key Pair seleccionado es `vockey`
* Que la instancia tiene al menos 4 minutos de running time *(Windows necesita tiempo para generar la contraseña)*
