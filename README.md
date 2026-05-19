# M09 — Eventos Universitarios
Microservicio del sistema **PATRICI.A** para la gestión y consulta del feed de eventos universitarios del campus ECI.
 
---

## Requisitos funcionales cubiertos
| ID | Descripción |
|----|-------------|
| RF11 | Consultar feed de eventos activos del campus |
| RF20 | Filtrar eventos por categoría (ACADEMICO, CULTURAL, DEPORTIVO, BIENESTAR) |
 
---

## Stack tecnológico
- Java 21 + Spring Boot 3.3.0
- Spring Data MongoDB
- Spring Security (JWT)
- SpringDoc OpenAPI (Swagger UI)
- JaCoCo + SonarQube
---

## Diagrama de componentes

![](https://github.com/PATRICI-A/eeveevoiding-responsibilities-campus-events/blob/develop/docs/images/DiagramaComponentes%20Eventos.png)
 
---

## Diagrama de clases

![](https://github.com/PATRICI-A/eeveevoiding-responsibilities-campus-events/blob/develop/docs/images/DiagramaClases%20Eventos.png)

---

## Endpoints
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/eventos` | Feed de eventos activos |
| GET | `/api/v1/eventos?categoria=CULTURAL` | Feed filtrado por categoría |

Documentación interactiva disponible en `/swagger-ui.html`.
 
---

## Ejecutar localmente
```bash
# Con MongoDB local
mvn spring-boot:run
 
# Con URI personalizada
SPRING_DATA_MONGODB_URI=mongodb://host:27017/campus-events mvn spring-boot:run
```

## Ejecutar tests
```bash
mvn clean verify
```
 
---

## Variables de entorno
| Variable | Default | Descripción |
|----------|---------|-------------|
| `SPRING_DATA_MONGODB_URI` | `mongodb://localhost:27017/campus-events` | URI de conexión a MongoDB |
 
---

## Estructura del proyecto
```
src/main/java/.../
├── domain/
│   ├── model/          # Evento, EventoRsvp, enums
│   └── repository/     # EventoRepository (MongoRepository)
├── application/
│   └── service/        # EventoService
└── infrastructure/
    ├── controller/     # EventoController
    ├── dto/            # EventoResponse
    └── config/         # SecurityConfig
```
 