# Arquitectura técnica

## Diagrama de clases (UML)

Modelo de dominio: relación entre `Usuario`, `Ticket` y `Comentario`.

```mermaid
classDiagram
    class Usuario {
        -Long id
        -String nombre
        -String email
        -String password
        -Rol rol
        -LocalDateTime fechaCreacion
        -Boolean activo
    }
    class Rol {
        <<enumeration>>
        CLIENTE
        AGENTE
        ADMIN
    }
    class Ticket {
        -Long id
        -String titulo
        -String descripcion
        -Estado estado
        -Prioridad prioridad
        -LocalDateTime fechaCreacion
        -LocalDateTime fechaActualizacion
    }
    class Estado {
        <<enumeration>>
        ABIERTO
        EN_PROGRESO
        RESUELTO
        CERRADO
    }
    class Prioridad {
        <<enumeration>>
        ALTA
        MEDIA
        BAJA
    }
    class Comentario {
        -Long id
        -String contenido
        -LocalDateTime fechaCreacion
    }

    Usuario "1" --> "0..*" Ticket : cliente
    Usuario "0..1" --> "0..*" Ticket : agenteAsignado
    Ticket "1" --> "0..*" Comentario : comentarios
    Usuario "1" --> "0..*" Comentario : autor
    Usuario --> Rol
    Ticket --> Estado
    Ticket --> Prioridad
```

## Diagrama de secuencia: autenticación (login + JWT)

```mermaid
sequenceDiagram
    participant C as Cliente (React)
    participant A as AuthController
    participant AM as AuthenticationManager
    participant UDS as CustomUserDetailsService
    participant DB as PostgreSQL
    participant JWT as JwtService

    C->>A: POST /api/auth/login {email, password}
    A->>AM: authenticate(email, password)
    AM->>UDS: loadUserByUsername(email)
    UDS->>DB: findByEmail(email)
    DB-->>UDS: Usuario
    UDS-->>AM: UsuarioDetails
    AM->>AM: verifica password (BCrypt)
    AM-->>A: Authentication OK
    A->>JWT: generarToken(usuarioDetails)
    JWT-->>A: token JWT
    A-->>C: 200 {token, email, rol}
```

## Diagrama de secuencia: petición protegida (crear ticket)

```mermaid
sequenceDiagram
    participant C as Cliente (React)
    participant F as JwtAuthenticationFilter
    participant JWT as JwtService
    participant TC as TicketController
    participant TS as TicketService
    participant DB as PostgreSQL

    C->>F: POST /api/tickets (Authorization: Bearer token)
    F->>JWT: extraerEmail(token) / esTokenValido()
    JWT-->>F: token valido
    F->>F: SecurityContext.setAuthentication()
    F->>TC: continua la peticion
    TC->>TS: crear(ticket)
    TS->>DB: save(ticket)
    DB-->>TS: Ticket guardado
    TS-->>TC: Ticket
    TC-->>C: 201 TicketResponseDTO
```

## Arquitectura en capas

```mermaid
flowchart TB
    subgraph Frontend["Frontend — React + Vite"]
        UI[Componentes / Paginas]
    end

    subgraph Backend["Backend — Spring Boot"]
        SEC["JwtAuthenticationFilter
        SecurityConfig"]
        CTRL["Controllers
        REST endpoints"]
        SRV["Services
        logica de negocio"]
        REPO["Repositories
        Spring Data JPA"]
    end

    DB[(PostgreSQL)]

    UI -->|HTTPS + JWT| SEC
    SEC --> CTRL
    CTRL --> SRV
    SRV --> REPO
    REPO --> DB
```

## Modelo entidad-relación

```mermaid
erDiagram
    USUARIOS ||--o{ TICKETS : "es cliente de"
    USUARIOS ||--o{ TICKETS : "es agente de"
    USUARIOS ||--o{ COMENTARIOS : "escribe"
    TICKETS ||--o{ COMENTARIOS : "tiene"

    USUARIOS {
        bigint id PK
        varchar nombre
        varchar email UK
        varchar password
        varchar rol
        timestamp fecha_creacion
        boolean activo
    }
    TICKETS {
        bigint id PK
        varchar titulo
        text descripcion
        varchar estado
        varchar prioridad
        timestamp fecha_creacion
        timestamp fecha_actualizacion
        bigint cliente_id FK
        bigint agente_id FK
    }
    COMENTARIOS {
        bigint id PK
        text contenido
        timestamp fecha_creacion
        bigint ticket_id FK
        bigint autor_id FK
    }
```

## Diagrama de despliegue

```mermaid
flowchart LR
    U[Usuario / Navegador] -->|HTTPS| V["Vercel
    Frontend React"]
    V -->|HTTPS + JWT| R["Render
    Backend Spring Boot
    (Docker)"]
    R -->|JDBC + SSL| N[("Neon
    PostgreSQL")]
```