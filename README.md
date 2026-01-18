# GestionoCosas
Proyecto de INSO1
# GestionoCosas - Guía de Configuración

Para que la aplicación funcione en tu ordenador, sigue estos pasos:

## 1. Requisitos previos
* Tener instalado el **JDK 17** o superior.
* Tener **MySQL Server** funcionando.
* Usar **Visual Studio Code** con el "Extension Pack for Java".

## 2. Configuración de la Base de Datos
1. Abre MySQL Workbench.
2. Crea una base de datos llamada `test` ejecutando: `CREATE DATABASE test;`.
3. Abre el archivo `SQL data base` de la carpeta `Other` y ejecútalo entero para crear las tablas.

## 3. Configuración en VS Code
Para que Java reconozca la base de datos:
1. Ve a la sección **JAVA PROJECTS** (abajo a la izquierda).
2. En **Referenced Libraries**, pulsa el botón **+**.
3. Selecciona el archivo `mysql-connector-j-9.5.0.jar` que está en la carpeta `Code/lib`.

## 4. Ejecución
* Abre `src/app/Main.java` y dale a **Run**.
* **Credenciales de prueba:** admin123 / admin123