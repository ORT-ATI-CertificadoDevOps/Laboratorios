#!/bin/bash

# Ejemplo básico de un script bash
# Para ejecutarlo: ./example.sh

# Definición de una variable
NOMBRE="Mundo"

# Imprimir un mensaje usando la variable
echo "Hola $NOMBRE, bienvenido a Bash Scripting!"

# Guardar la salida de un comando en una variable
EQUIPO=$(hostname)
echo "Este script se ejecuta en: $EQUIPO"