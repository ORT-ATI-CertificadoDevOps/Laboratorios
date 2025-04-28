## Solución T/P VPC

Vamos a crear un VPC y todos los componentes necesarios, uno a uno, sin utilizar el wizard que crea todos los componentes de una sola vez.

1. Vamos a la consola de VPC y le damos al botón **Create VPC**.

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc01.png">
</p>

2. Seleccionamos la opción **"VPC Only"** y llenamos los campos necesarios.

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc02.png">
</p>

3. Creamos la **Subnet** desde la consola de VPC también

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc03.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc04.png">
</p>

4. Creamos el **Internet Gateway** desde la consola de VPC

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc05.png">
</p>

5. Asociamos el Internet Gateway recién creado, al VPC

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc06.png">
</p>

6. Identificamos la **Route Table** que se crea automáticamente con la creación del VPC y le agregamos una ruta para que el tráfico hacia internet (0.0.0.0/0) se vaya a través del Internet Gateway.

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc07.png">
</p>

<p align = "center">
<img src = "../../../../Extras/Imagenes/labNetworking/vpc/vpc08.png">
</p>