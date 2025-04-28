## Solución parte 4: Desplegando instancias Custom

### Parte A

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ec2-custom01.png">
</p>

Para modicar la configuración del `Networking` debemos de clikear el botón **Edit**

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ec2-custom02.png">
</p>

Debemos de expandir **Advanced details** para colocar el código en el bloque `user-data` al final.

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ec2-custom03.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ec2-custom04.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ec2-custom05.png">
</p>

El resultado deberia ser el siguiente:

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ec2-custom06.png">
</p>

### Parte B

Sobre la instancia que queremos usar para generar la `AMI` hacemos botón derecho:

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ami01.png">
</p>

Llenamos los datos y le damos **Create image**

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ami02.png">
</p>

Luego, desde la misma ventana de creación de la `AMI`, podemos desplegar una instancia.

<p align = "center">
<img src = "../../../../Extras/Imagenes/laboratorioCloud_EC2/ec2_custom/ami03.png">
</p>

