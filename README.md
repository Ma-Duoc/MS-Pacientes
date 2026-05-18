# MS-PACIENTES

Microservicio para gestión de pacientes desarrollado con Spring Boot 3. Este servicio proporciona funcionalidades de registro, autenticación y gestión de perfiles de pacientes utilizando JWT para la seguridad.

## Características

- **Java 17** - Versión de Java requerida
- **Spring Boot 3.2.5** - Framework principal
- **Spring Web** - Para endpoints REST
- **Spring Data JPA** - Para persistencia de datos
- **Spring Security** - Para autenticación y autorización
- **JWT (JSON Web Tokens)** - Para autenticación stateless
- **H2 Database** - Base de datos en memoria para desarrollo
- **Lombok** - Para reducir código boilerplate
- **Validación de datos** - Validaciones con Jakarta Validation
- **BCrypt** - Para encriptación de contraseñas

El servicio se ejecutará en el puerto `8082`.

### Acceso a la Consola H2

Durante el desarrollo, puedes acceder a la consola de H2 en:
```
http://localhost:8082/h2-console
```
## Endpoints API

### Autenticación

#### Registro de Paciente
```http
POST /api/pacientes/usuarios/registro
Content-Type: application/json

{
  "rut": "12345678-9",
  "nombre": "Juan",
  "apellido": "Pérez",
  "fechaNacimiento": "1990-01-01",
  "email": "juan.perez@email.com",
  "telefono": "987654321",
  "direccion": "Calle Principal 123",
  "password": "Password123!"
}
```

**Validaciones:**
- RUT debe ser único
- Email debe ser válido y único
- El paciente debe ser mayor de 18 años
- Todos los campos obligatorios deben estar presentes

#### Login de Paciente
```http
POST /api/pacientes/usuarios/login
Content-Type: application/json

{
  "rut": "12345678-9",
  "password": "Password123!"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "rut": "12345678-9",
  "nombre": "Juan"
}
```

### Pacientes

#### Obtener Paciente por RUT (Público)
```http
GET /api/pacientes/{rut}
```

**Respuesta:**
```json
{
  "rut": "12345678-9",
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan.perez@email.com",
  "telefono": "987654321",
  "direccion": "Calle Principal 123"
}
```

#### Obtener Perfil (Protegido)
```http
GET /api/pacientes/perfil
Authorization: Bearer {token}
```

**Respuesta:**
```
Acceso autorizado. RUT: 12345678-9
```

## Seguridad

### Configuración JWT

El servicio utiliza JWT para autenticación stateless:

- **Secret Key**: Configurada en `application.properties`
- **Expiración**: 86400 segundos (24 horas)
- **Algoritmo**: HMAC SHA

### Endpoints Públicos
- `/api/pacientes/usuarios/**` - Registro y login
- `/api/pacientes/{rut}` - Consulta de paciente por RUT
- `/h2-console/**` - Consola H2 (solo desarrollo)

### Endpoints Protegidos
- `/api/pacientes/perfil` - Requiere token JWT válido

### Flujo de Autenticación

1. El paciente se registra con sus credenciales
2. La contraseña se encripta con BCrypt antes de guardar
3. Al hacer login, se valida la contraseña con BCrypt
4. Si las credenciales son válidas, se genera un JWT
5. El JWT se usa en el header `Authorization: Bearer {token}` para acceder a endpoints protegidos

## Estructura del Proyecto

```
src/main/java/com/microservicios/mspacientes/
├── controller/
│   └── PacienteController.java          # Endpoints REST
├── service/
│   ├── PacienteService.java             # Lógica de negocio
│   └── JwtService.java                  # Generación y validación JWT
├── repository/
│   └── PacienteRepository.java          # Acceso a datos (JPA)
├── model/
│   └── Paciente.java                    # Entidad JPA
├── dto/
│   ├── PacienteRegistroRequest.java     # DTO para registro
│   ├── PacienteLoginRequest.java        # DTO para login
│   ├── PacienteLoginResponse.java       # DTO respuesta login
│   └── PacienteResponse.java            # DTO respuesta paciente
├── security/
│   ├── SecurityConfig.java              # Configuración Spring Security
│   ├── JwtAuthenticationFilter.java     # Filtro JWT
│   └── CustomUserDetailsService.java    # UserDetailsService personalizado
├── exception/
│   └── PacienteException.java           # Excepciones personalizadas
└── MsPacientesApplication.java          # Clase principal
```

## Modelo de Datos

### Entidad Paciente

| Campo          | Tipo         | Descripción                          |
|----------------|--------------|--------------------------------------|
| rut            | String       | ID principal (RUT chileno)           |
| nombre         | String       | Nombre del paciente                  |
| apellido       | String       | Apellido del paciente                |
| fechaNacimiento| LocalDate    | Fecha de nacimiento                  |
| email          | String       | Email (único)                        |
| telefono       | String       | Teléfono (opcional)                  |
| direccion      | String       | Dirección (opcional)                 |
| password       | String       | Contraseña encriptada                |
| fechaCreacion  | LocalDateTime | Fecha de creación (auto)             |
| fechaActualizacion| LocalDateTime| Fecha de actualización (auto)      |

## Configuración

### application.properties

```properties
# Servidor
server.port=8082
spring.application.name=ms-pacientes

# Base de datos H2
spring.datasource.url=jdbc:h2:mem:pacientes_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Consola H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT
jwt.secret=miClaveSuperSecretaJWT_2026_segura_1234567890
jwt.expiration=86400

# Logging
logging.level.com.microservicios=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Dependencias Principales

- **spring-boot-starter-web** - Para aplicaciones web
- **spring-boot-starter-data-jpa** - Para JPA/Hibernate
- **spring-boot-starter-security** - Para seguridad
- **spring-boot-starter-validation** - Para validaciones
- **spring-boot-starter-test** - Para pruebas
- **h2** - Base de datos en memoria
- **lombok** - Para reducir código boilerplate
- **jjwt-api, jjwt-impl, jjwt-jackson** - Para JWT (v0.12.3)


##  Notas Importantes

- El proyecto usa H2 en memoria para desarrollo.
- Las contraseñas se encriptan usando BCrypt antes de almacenarlas.
- El RUT se usa como identificador principal del paciente.
