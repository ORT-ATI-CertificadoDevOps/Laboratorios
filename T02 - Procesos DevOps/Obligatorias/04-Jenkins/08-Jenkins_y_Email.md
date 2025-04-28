# 8 - Jenkins + Email

Vamos a configurar nuestro servidor Jenkins para que nos envie notificaciones sobre nuestros trabajos mediante correo electronico.

## 8.1 Instalar plugin Mailer

>**Nota** En caso de ya tenerlo instalado, obviar los siguientes pasos y continuar.

- Ir al manejador de plugins
- Instalar el plugin **Mailer Plugin**
- Reiniciar el servidor.

## 8.2 Integrar Jenkins + Gmail

Vamos a realizar la configuración para que los envios se efectuen mediante una cuenta de Gmail.

>**Nota:** En caso de no contar con una cuenta de Gmail y quieren realizar la configuración con otro proveedor de correo, puede realizarlo. Solamente en los siguientes pasos necesitan los valores SMTP que maneja su proveedor para completar en el Jenkins.

- Ir a la **Configure System**
- Encontrar la opción **E-mail Notification**
- Clickear en el botón **advanced**
- Configurar los valores necesarios con los **Default Gmail SMTP Settings**, buscarlos en la web.
- Validar la conexión con el botón **Test configuration**

>**Nota:** Pueden tener problemas con las aplicaciones poco seguras en caso que les rebote el envio cuando realizan el test, para Gmail con buscar **gmail less secure apps** pueden solucionar el problema. Una vez realizado el cambio, puede volver a probar si la configuración se encuentra correcta.

## 8.3 Agregar envio de notificaciones a nuestros trabajos

Vamos a agregar un paso más a nuestro trabajamos, en donde enviaremos una notificación con el resultado de mismo mediante correo electronico.

- Seleccinamos un trabajo.
- Vamos a la parte de **Post-build Actions**
- Seleccionamos en el botón **Add post-build action** la acción de **E-mail Notification**
- Ingresamos la dirección la cual va a recibir las notificaciones y tildamos las dos opciones que aparecen.
- Guardamos y ejecutamos el trabajo, validar que se reciben las notificaciones sobre cual fue el resultado del trabajo.

## Próximos pasos
Para el siguiente paso del laboratorio, diríjase a [9 - Jenkins y Maven](09-Jenkins_y_Maven.md)