# Documentación del Proyecto

## Visión General

Este proyecto es una aplicación web desarrollada con **Spring Boot** y **MySQL**. Facilita la gestión de instalaciones, horarios, usuarios y reservas. El sistema soporta diferentes roles como **ADMIN**, **OPERARIO** y **USUARIO** con distintos niveles de acceso y funcionalidades.

### Tecnologías Utilizadas
- **Spring Boot** como framework para el backend.
- **MySQL** como base de datos.
- **Docker** para la contenedorización.
- **Thymeleaf** para el renderizado en el frontend.
- **Spring Security** para autenticación y autorización.

# 📷 Capturas de Pantalla del Proyecto

Este documento contiene capturas de pantalla del sistema.

## 🖼️ Capturas del Proyecto

### Captura 1
![Captura 1](img/Captura1.PNG)

### Captura 2
![Captura 2](img/Captura2.PNG)

### Captura 3
![Captura 3](img/Captura3.PNG)

### Captura 4
![Captura 4](img/Captura4.PNG)

### Captura 5
![Captura 5](img/Captura5.PNG)

### Captura 6
![Captura 6](img/Captura6.PNG)

### Captura 7
![Captura 7](img/Captura7.PNG)

### Captura 8
![Captura 8](img/Captura8.PNG)

---

## Requisitos

### 1. Completar el CRUD de reservas completo para **ADMIN** y **OPERARIO** en la ruta **"/reservas"**:

#### Controlador
- Crear un nuevo controlador para gestionar las reservas de ADMIN y OPERARIO.
- Proteger la ruta con **Spring Security** para garantizar que solo los usuarios con los roles adecuados (ADMIN, OPERARIO) puedan acceder.

#### CRUD de Reservas
- **CREATE**: 
    - **Maestro-detalle**:
        - Selección de instalación.
        - Selección de horario (solo se deben mostrar los horarios disponibles de la instalación seleccionada).
        - Selección de usuario.
- **READ**: 
    - Mostrar las reservas de forma paginada. Es importante tener en cuenta que podrían ser miles de reservas.
    - Filtro por instalación (maestro-detalle) o filtro por usuario (maestro-detalle).

#### Reservas de Usuario
- Las reservas deben ser gestionadas en la ruta **"/mis-datos/mis-reservas"** para los usuarios que han iniciado sesión.
    - Es necesario ampliar el controlador existente para incluir la gestión de reservas de los usuarios.
    - Asegúrate de que las rutas de los usuarios estén protegidas adecuadamente por **Spring Security**.

### 2. Restricciones en las Reservas:

#### 1. **Solo una reserva por día**:
   - No permitir que un usuario haga más de una reserva en el mismo día. 
   - Se debe lanzar una excepción con un mensaje específico si el usuario ya tiene una reserva en la fecha seleccionada.

#### 2. **No se puede reservar con más de una semana de antelación**:
   - Las reservas no pueden realizarse para fechas pasadas ni con más de dos semanas de antelación.
   - Si se intenta hacer una reserva fuera de estos límites, se lanzará una excepción con un mensaje explicativo.

#### 3. **No se pueden actualizar reservas pasadas**:
   - Se deben evitar actualizaciones de reservas para fechas ya pasadas o para el día actual.
   - Esta validación se puede hacer con **Spring** o mediante un **disparador (trigger)** en la base de datos.

## Estructura del Proyecto

### 1. Modelos

- **Usuario.java**: Representa la entidad de usuario con atributos como nombre de usuario, contraseña, correo electrónico y roles.
- **Instalacion.java**: Representa la entidad de instalación con atributos como nombre y horarios asociados.
- **Reserva.java**: Representa las reservas realizadas por los usuarios, incluyendo fecha, instalación asociada y franja horaria.
- **Horario.java**: Representa las franjas horarias disponibles para las instalaciones.
- **Rol.java**: Enum para definir los roles de los usuarios (ADMIN, OPERARIO, USUARIO).

### 2. Controladores

- **ControMain.java**: Controlador para vistas generales como la página de inicio, inicio de sesión, página de error, etc.
- **ControUsuario.java**: Controlador para gestionar el registro de usuarios, visualización y gestión de sus datos.
- **ControInstalacion.java**: Controlador para gestionar las instalaciones, incluyendo agregar, editar y eliminar instalaciones.
- **ControReserva.java**: Controlador para gestionar las reservas, incluyendo ver, agregar, editar y eliminar reservas para usuarios administradores.
- **ControHorario.java**: Controlador para gestionar los horarios de las instalaciones.
- **ControDatos.java**: Controlador para gestionar los datos del usuario y su perfil.
- **ControReservaUsuario.java**: Controlador para manejar las reservas específicas de los usuarios, permitiendo agregar, editar y ver sus propias reservas.

### 3. Repositorios

- **RepoUsuario.java**: Interfaz de repositorio para interactuar con la entidad `Usuario`.
- **RepoInstalacion.java**: Interfaz de repositorio para interactuar con la entidad `Instalacion`.
- **RepoReserva.java**: Interfaz de repositorio para interactuar con la entidad `Reserva`.
- **RepoHorario.java**: Interfaz de repositorio para interactuar con la entidad `Horario`.

### 4. Configuración de Seguridad

- **ConfiSec.java**: Contiene la configuración de **Spring Security**, incluyendo las configuraciones de autenticación y control de acceso a las rutas.

### 5.Import.sql 

- **Import.sql**:Contiene las tablas de nuestra base de datos y instrucciones necesarias para su funcionamiento.

### 6.Vistas
En nuestro proyecto tenemos la carpeta templates que contiene vistas sobre Horarios,Intalaciones, Usuarios, Reservas, login, registro entre otras muchas.

# API Endpoints

Este documento describe los endpoints de la API de reservas y administración de instalaciones.

## Rutas de Reservas

- `GET /reservas`  
  Obtiene la lista de reservas.

- `GET /acerca`  
  Información sobre el servicio de reservas.

- `GET /admin/reservas`  
  Obtiene las reservas de administrador.

- `POST /admin/reservas/add`  
  Agrega una nueva reserva.

- `GET /admin/reservas/delete/{id}`  
  Elimina una reserva especificada por su ID.

- `GET /admin/reservas/edit/{id}`  
  Obtiene los detalles de la reserva para editarla.

- `POST /admin/reservas/edit/{id}`  
  Edita una reserva especificada por su ID.

- `GET /denegado`  
  Información sobre acceso denegado.

## Rutas de Horarios

- `GET /horario`  
  Obtiene la lista de horarios.

- `POST /horario/add`  
  Agrega un nuevo horario.

- `GET /horario/del/{id}`  
  Elimina un horario especificado por su ID.

- `POST /horario/del/{id}`  
  Elimina un horario especificado por su ID.

- `GET /horario/edit/{id}`  
  Obtiene los detalles de un horario para editarlo.

- `POST /horario/edit/{id}`  
  Edita un horario especificado por su ID.

- `GET /horario/filtrar/{id}`  
  Filtra los horarios basados en un ID específico.

## Rutas de Instalaciones

- `GET /instalaciones`  
  Obtiene la lista de instalaciones.

- `POST /instalaciones/add`  
  Agrega una nueva instalación.

- `GET /instalaciones/add/{id}`  
  Agrega una instalación especificada por su ID.

- `POST /instalaciones/del/{id}`  
  Elimina una instalación especificada por su ID.

- `POST /instalaciones/edit/{id}`  
  Edita una instalación especificada por su ID.

- `GET /instalaciones/filtrar/{id}`  
  Filtra las instalaciones basadas en un ID específico.

## Rutas de Usuario

- `GET /usuario`  
  Obtiene la lista de usuarios.

- `GET /usuario/add`  
  Obtiene la página para agregar un nuevo usuario.

- `POST /usuario/add`  
  Agrega un nuevo usuario.

- `GET /usuario/delete/{id}`  
  Elimina un usuario especificado por su ID.

- `POST /usuario/delete/{id}`  
  Elimina un usuario especificado por su ID.

- `GET /usuario/edit/{id}`  
  Obtiene los detalles del usuario para editarlo.

- `POST /usuario/edit/{id}`  
  Edita un usuario especificado por su ID.

## Rutas de Mis Datos

- `GET /mis-datos`  
  Obtiene los datos personales del usuario.

- `GET /mis-datos/add`  
  Obtiene la página para agregar datos personales.

- `POST /mis-datos/add`  
  Agrega los datos personales.

- `POST /mis-datos/editar/{id}`  
  Edita los datos personales de un usuario especificado por su ID.

- `GET /mis-datos/eliminar/{id}`  
  Elimina los datos personales de un usuario especificado por su ID.

- `POST /mis-datos/eliminar/{id}`  
  Elimina los datos personales de un usuario especificado por su ID.

- `GET /mis-datos/horarios/{instalacionId}`  
  Obtiene los horarios asociados a una instalación.

- `GET /mis-datos/mis-reservas`  
  Obtiene las reservas del usuario.

## Rutas de Registro y Login

- `GET /login`  
  Obtiene la página de login.

- `POST /login`  
  Realiza el login del usuario.

- `GET /register`  
  Obtiene la página de registro.

- `POST /register`  
  Registra un nuevo usuario.

## Instrucciones de Configuración

### 1. Configuración del Entorno

Crea un archivo `.env` en el directorio raíz con el siguiente contenido y no olvides no subirlo ya es que información privada:

```env
MYSQL_ROOT_PASSWORD=zx76wbz7FG89k
MYSQL_USERNAME=root
MYSQL_PORT=33306
MYSQL_HOST=localhost
MYSQL_DATABASE=deporte
ADMINER_PORT=8181
SERVICE_PORT=8080
```
### 2. Configuración de Docker
Asegúrate de tener **Docker** instalado y en funcionamiento. Utiliza el archivo `docker-compose.yml` proporcionado para configurar la base de datos y **Adminer** para la gestión de la base de datos.

Utiliza el comamdo docker-compose up -d para arrancarlo.


```docker-compose.yml
version: '3.1'

services:

  adminer:
    image: adminer
    restart: "no"
    ports:
      - ${ADMINER_PORT}:8080

  db:
    image: mysql:latest
    restart: "no"
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    ports:
      - ${MYSQL_PORT}:3306
    volumes:
      - ./scripts:/docker-entrypoint-initdb.d

```
### application.properties

```properties
spring.application.name=pistasdeportivas
spring.datasource.url=jdbc:mysql://localhost:33306/deporte
spring.datasource.username=root
spring.datasource.password=zx76wbz7FG89k
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
logging.level.org.springframework.security=DEBUG
spring.jpa.defer-datasource-initialization=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

```




### 3. Construcción y Ejecución de la Aplicación
Puedes construir y ejecutar la aplicación Spring Boot utilizando los siguientes comandos:

## mvn clean install
## mvn spring-boot:run

Ademas necesitaras tener creado dentro de la carpeta que albergar el archivo .env y docker-compose.yml una carpeta llamada scripts con el archivo initdb.sql para crear la base de datos deportes.


```initdb.sql
CREATE DATABASE IF NOT EXISTS deporte;

USE deporte;

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL,
    email VARCHAR(80) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL,
    tipo VARCHAR(10) NOT NULL
);

```
