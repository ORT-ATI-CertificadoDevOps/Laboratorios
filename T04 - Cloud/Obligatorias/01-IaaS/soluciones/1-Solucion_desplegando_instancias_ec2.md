### Solución parte 1

En la consola de EC2 ubicar el botón **_Launch Instance_**  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step0.png">
</p>

Seleccionar la AMI: **_Amazon Linux_**  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step1.png">
</p>

Seleccionar el tipo de instancia: **_t2.micro_**  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step2.png">
</p>

En **_Configure Instance_** desplegar el listado de **_Subnets_** y seleccionar **_us-east-1a_**  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step3.png">
</p>

En **_Storage_** dejar como está.  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step4.png">
</p>

En **_Tags_** agregar un nuevo Tag con la key **_Name_** y el value **_test-instance01_**.  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step5.png">
</p>

Crear un nuevo security group de nombre **_permitir-ssh_** y verificar que la regla esté correcta.  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step6.png">
</p>

Seleccionar la opción **_Create a new key pair_** y descargar el archivo .pem en un lugar accesible desde consola y un cliente ssh.  

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2/step7.png">
</p>
