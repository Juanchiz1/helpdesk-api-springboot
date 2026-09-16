# Support Ticket API

API REST para gestión de tickets de soporte técnico, con autenticación JWT, control de roles y documentación interactiva. Backend construido con Spring Boot, pensado como base para un sistema de mesa de ayuda (helpdesk) real.

## Stack técnico

- **Java 17**
- **Spring Boot 4.1.1**
- **Spring Data JPA** (Hibernate) — persistencia
- **PostgreSQL** — base de datos relacional
- **Spring Security + JWT** (jjwt 0.12.6) — autenticación y autorización
- **Springdoc OpenAPI** — documentación interactiva (Swagger UI)
- **JUnit 5 + Mockito + AssertJ** — pruebas unitarias
- **Maven** — gestión de dependencias y build
- **Lombok** — reducción de código repetitivo

## Características

- Registro y login de usuarios con contraseñas encriptadas (BCrypt)
- Autenticación stateless con JWT
- Tres roles diferenciados: `CLIENTE`, `AGENTE`, `ADMIN`
- Endpoints protegidos según rol
- CRUD completo de tickets de soporte (crear, listar, asignar agente, cambiar estado)
- Sistema de comentarios/seguimiento por ticket
- Manejo centralizado de excepciones con respuestas HTTP consistentes
- Documentación interactiva vía Swagger UI, con soporte de autorización Bearer integrado
- Arquitectura en capas: Controller → Service → Repository (DAO) → Model

## Arquitectura

```
com.juandiego.sistema_tickets_soporte
├── config/          # Configuración de Spring Security y OpenAPI
├── controller/       # Endpoints REST
├── dto/               # Objetos de transferencia (request/response)
├── exception/         # Excepciones personalizadas y manejador global
├── model/             # Entidades JPA (Usuario, Ticket, Comentario)
├── repository/        # Interfaces Spring Data JPA (capa DAO)
├── security/          # JWT, UserDetails, filtros de autenticación
└── service/           # Lógica de negocio
```

## Modelo de datos

- **Usuario**: id, nombre, email, password (encriptado), rol, fechaCreacion, activo
- **Ticket**: id, titulo, descripcion, estado, prioridad, cliente, agenteAsignado, fechaCreacion, fechaActualizacion
- **Comentario**: id, contenido, fechaCreacion, ticket, autor

Relaciones: un `Ticket` pertenece a un `Usuario` (cliente) y opcionalmente tiene un `Usuario` (agente) asignado. Un `Comentario` pertenece a un `Ticket` y tiene un `Usuario` como autor.

## Requisitos previos

- JDK 17+
- PostgreSQL 15+ corriendo localmente (o accesible remotamente)
- Maven (se puede usar el wrapper incluido, `mvnw`/`mvnw.cmd`)

## Configuración

1. Crea la base de datos y el usuario en PostgreSQL:
```sql
   CREATE DATABASE tickets_db;
   CREATE USER tickets_user WITH PASSWORD 'tickets_pass';
   GRANT ALL PRIVILEGES ON DATABASE tickets_db TO tickets_user;
```

2. Configura la variable de entorno `JWT_SECRET` (una clave larga y aleatoria, mínimo 256 bits):
```bash
   # Windows PowerShell
   $env:JWT_SECRET="tu_clave_secreta_aqui"
```

3. Ajusta `src/main/resources/application.properties` si tu conexión a PostgreSQL difiere de los valores por defecto (`localhost:5432`, `tickets_db`, `tickets_user`/`tickets_pass`).

## Cómo correr el proyecto

```bash
./mvnw clean install
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Documentación interactiva (Swagger)

Con la aplicación corriendo:

```
http://localhost:8080/swagger-ui.html
```

Desde ahí puedes probar todos los endpoints, incluida la autenticación: usa el botón **Authorize** y pega el token JWT obtenido en `/api/auth/login`.

## Endpoints principales

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | `/api/usuarios/registro` | Registrar nuevo usuario | Público |
| POST | `/api/auth/login` | Iniciar sesión, obtener JWT | Público |
| GET | `/api/usuarios/{id}` | Consultar usuario por id | AGENTE, ADMIN |
| POST | `/api/tickets` | Crear ticket | Autenticado |
| GET | `/api/tickets` | Listar todos los tickets | Autenticado |
| GET | `/api/tickets/{id}` | Consultar ticket por id | Autenticado |
| PATCH | `/api/tickets/{id}/asignar/{agenteId}` | Asignar agente a un ticket | Autenticado |
| PATCH | `/api/tickets/{id}/estado` | Cambiar estado del ticket | Autenticado |
| POST | `/api/tickets/{ticketId}/comentarios` | Agregar comentario a un ticket | Autenticado |
| GET | `/api/tickets/{ticketId}/comentarios` | Listar comentarios de un ticket | Autenticado |

## Pruebas

El proyecto incluye pruebas unitarias con JUnit 5 y Mockito para la capa de servicios, cubriendo casos de éxito, casos de error y reglas de negocio (por ejemplo, que asignar un agente cambia automáticamente el estado del ticket a `EN_PROGRESO`).

```bash
./mvnw test
```

## Ejemplo de flujo de uso

1. `POST /api/usuarios/registro` — crear un usuario con rol `CLIENTE`
2. `POST /api/auth/login` — obtener el token JWT
3. `POST /api/tickets` (con header `Authorization: Bearer <token>`) — crear un ticket
4. `PATCH /api/tickets/{id}/asignar/{agenteId}` — un agente toma el ticket
5. `POST /api/tickets/{id}/comentarios` — seguimiento de la conversación

## Notas de seguridad

- Las contraseñas se almacenan con hash BCrypt, nunca en texto plano
- Los tokens JWT expiran a las 24 horas
- La clave de firma JWT se maneja por variable de entorno, no está hardcodeada en el repositorio
- La API es stateless (sin sesiones de servidor), apropiado para arquitecturas REST

## Roadmap

- [ ] Cobertura de tests para `AuthController` y `JwtService`
- [ ] Frontend (en desarrollo)
- [ ] Paginación y filtros de búsqueda en el listado de tickets
- [ ] Despliegue en la nube

## Autor

**Juan Diego Negrete Portillo**
Estudiante de Ingeniería de Sistemas y Telecomunicaciones — Universidad de Córdoba
[Portafolio](https://juanchiz1.github.io/Portafolio-Juan_Diego_Negrete/) · [LinkedIn](https://www.linkedin.com/in/juan-diego-negrete-portillo-352111195/) · [GitHub](https://github.com/Juanchiz1)