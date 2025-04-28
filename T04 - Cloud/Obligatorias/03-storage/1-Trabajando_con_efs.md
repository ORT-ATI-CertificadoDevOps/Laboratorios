## Práctico de Storage: EFS

### Objetivos

* Crear una instancia con Ubuntu ó Amazon Linux
* Crear un share EFS usando la interfaz web
* Montar el share en la instancia y almacenar contenido
* Crear una segunda instancia y comprobar que se puede acceder al contenido

#### Referencias

* Usar el protocolo NFSv4
* El puerto a habilitar es el tcp 2049
* Es posible que se deba instalar el soporte para NFS:
  * En ubuntu: `sudo apt install nfs-common`
  * En Amazon Linux: `sudo yum install nfs-utils`

