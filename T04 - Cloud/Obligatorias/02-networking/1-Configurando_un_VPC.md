## Configurando un VPC

> **Tiempo estimado:** 35 minutos

### Objetivos

Dado el siguiente diagrama de arquitectura, desplegar todos los componentes que en él se representan.
    
<p align = "center">
<img src="/Extras/Imagenes/labNetworking/vpc/arquitectura.png">
</p>

### Recursos a crear

* **VPC**
  * Name: `vpc-practico01`
  * CIDR: `10.0.0.0/16`
  * Habilitar *DNS hostnames* en la configuración del VPC

* **Subnets** — una por AZ
  * Subnet 1: `10.0.1.0/24` → AZ `us-east-1a`
  * Subnet 2: `10.0.2.0/24` → AZ `us-east-1b`
  * Habilitar *Auto-assign public IPv4 address* en cada subnet

* **Internet Gateway**
  * Crear y asociar al VPC (`Actions > Attach to VPC`)

* **Route Table**
  * Agregar ruta: destino `0.0.0.0/0` → target: el IGW creado
  * Asociar ambas subnets a esta route table (pestaña *Subnet associations*)

* **Security Group** (asociado al VPC creado)
  * Reglas de entrada (*Inbound rules*): `SSH (22)` y `HTTP (80)` desde `0.0.0.0/0`

* **Instancias EC2** — una en cada subnet
  * Tipo: `t2.micro` / `t3.micro`
  * Asignar el Security Group creado
  * Verificar que cada instancia recibe una IP pública

### Verificación

1. Las instancias muestran una IP pública en la consola
2. SSH a ambas instancias funciona desde la máquina local

### Limpieza de recursos

Eliminar en orden (las dependencias entre recursos pueden bloquear la eliminación):

1. Instancias EC2
2. Internet Gateway: desasociar del VPC (`Actions > Detach`), luego eliminar
3. Subnets
4. Route Tables (no la default del VPC)
5. Security Groups (no el default del VPC)
6. VPC

### Para investigar: Security Groups vs Network ACLs

Con el VPC creado, explorar la sección `VPC > Network ACLs` en la consola. Hay una NACL default asociada a todas las subnets.

| | Security Group | Network ACL |
|---|---|---|
| **Nivel** | Instancia EC2 | Subnet |
| **Estado** | Stateful (permite respuesta automática) | Stateless (requiere regla explícita de entrada Y salida) |
| **Reglas** | Solo Allow | Allow y Deny |
| **Evaluación** | Todas las reglas | En orden numérico (primera coincidencia) |

En la mayoría de los casos los Security Groups son suficientes. Las NACLs se usan para bloqueo a nivel de subnet (ej: denegar un rango de IPs específico).

### Spoiler Alert

En caso de trancarse, pueden consultar la ayuda [aquí](./soluciones/1-Solucion_configurando_vpc.md).
