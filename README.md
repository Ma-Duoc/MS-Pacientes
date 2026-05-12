# MS-PACIENTES

Microservicio para gestión de pacientes desarrollado con Spring Boot 3.

## Características

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Security
- Lombok
- Validación de datos

## Configuración

### Base de Datos PostgreSQL

```sql
CREATE DATABASE pacientes_db;
CREATE USER postgres WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE pacientes_db TO postgres;
```

### Ejecutar el proyecto

```bash
mvn spring-boot:run
```

El servicio se ejecutará en el puerto `8081`.

## Endpoints API

### Pacientes

- `GET /api/pacientes` - Obtener todos los pacientes
- `GET /api/pacientes/{id}` - Obtener paciente por ID
- `GET /api/pacientes/dni/{dni}` - Obtener paciente por DNI
- `POST /api/pacientes` - Crear nuevo paciente
- `PUT /api/pacientes/{id}` - Actualizar paciente existente
- `DELETE /api/pacientes/{id}` - Eliminar paciente

### Ejemplo de JSON para crear paciente

```json
{
  "nombre": "Juan Pérez",
  "dni": "12345678",
  "email": "juan.perez@email.com",
  "telefono": "987654321",
  "fechaNacimiento": "1990-01-01T10:00:00"
}
```

## Seguridad

El servicio está configurado con Spring Security con autenticación básica:

- Usuario: `admin`
- Contraseña: `admin`

Los endpoints de pacientes están configurados para acceso público (permitAll()).

## Estructura del Proyecto

```
src/main/java/com/microservicios/mspacientes/
|- controller/
|  |- PacienteController.java
|- service/
|  |- PacienteService.java
|- repository/
|  |- PacienteRepository.java
|- model/
|  |- Paciente.java
|- dto/
|  |- PacienteDTO.java
|- security/
|  |- SecurityConfig.java
|- MsPacientesApplication.java
```

## Dependencias Principales

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- PostgreSQL Driver
- Lombok
