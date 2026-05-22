<div align="center">

# Bienestar Service (Support)

### *"Momentos que inspiran, parches que unen."*

---

### Stack Tecnológico

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)

### Infraestructura & Calidad

![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

### Arquitectura

![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=for-the-badge)
![Clean Architecture](https://img.shields.io/badge/Clean-Architecture-blue?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST-API-009688?style=for-the-badge)

</div>

---

## Tabla de Contenidos

1. [Integrantes](#1-integrantes)
2. [Tecnologías Utilizadas](#2-tecnologías-utilizadas)
3. [Descripción del Microservicio](#3-descripción-del-microservicio)
4. [Cómo Funciona](#4-cómo-funciona)
5. [Diagrama de Datos](#5-diagrama-de-datos)
6. [Diagrama de Clases](#6-diagrama-de-clases)
7. [Diagrama de Componentes](#7-diagrama-de-componentes)
8. [Funcionalidades Principales](#8-funcionalidades-principales)
9. [Endpoints](#9-endpoints)
10. [Colas de Mensajería](#10-colas-de-mensajería)
11. [Evidencia de Pruebas](#11-evidencia-de-pruebas)
12. [Evidencia de Cobertura](#12-evidencia-de-cobertura)
13. [Cómo Ejecutar](#13-cómo-ejecutar)
14. [Evidencia CI/CD](#14-evidencia-cicd)
15. [Link Swagger](#15-link-swagger)
16. [Estructura del Código](#16-estructura-del-código)
17. [Código Documentado](#17-código-documentado)
18. [Conexiones Externas](#18-conexiones-externas)
19. [Pipeline de Desarrollo](#19-pipeline-de-desarrollo)
20. [Pipeline de Producción](#20-pipeline-de-producción)
21. [Dockerizado](#21-dockerizado)
22. [Versionamiento](#22-versionamiento)

---

## 1. Integrantes

- Tomas Espitia
- Juan Gonzalez
- Camilo Cristancho
- Andres Pineda

---

## 2. Tecnologías Utilizadas

| **Tecnología / Herramienta** | **Uso principal en el proyecto** |
|---|---|
| **Java 21 (OpenJDK)** | Lenguaje base con soporte para Spring Boot. |
| **Spring Boot 3.3.0** | Framework principal para la exposición de endpoints REST. |
| **Spring Web** | Exposición de endpoints REST mediante `WellnessController`. |
| **Spring Security + JWT** | Protección de endpoints mediante token de sesión. |
| **Spring Data JPA** | Acceso y manipulación de datos en la BD relacionar PostgreSQL. |
| **PostgreSQL** | BD relacional principal empleada para almacenar recursos de bienestar. |
| **Apache Maven** | Gestión de dependencias y automatización de builds. |
| **Lombok 1.18.38** | Reducción de boilerplate (`@RequiredArgsConstructor`, `@Getter`, etc). |
| **H2** | BD en memoria para pruebas unitarias y de integración. |
| **JUnit 5 & Mockito** | Framework de pruebas unitarias y simulación de dependencias. |
| **JaCoCo 0.8.13** | Análisis de la cobertura de código probada. |
| **SpringDoc OpenAPI 2.8.8** | Exposición dinámica del esquema de API mediante Swagger UI. |
| **Docker** | Contenedorización de la aplicación y la infraestructura para despliegues portables. |

---

## 3. Descripción del Microservicio

El microservicio de **Bienestar y Soporte** (conocido como `suport-service` o `eeveevoiding-responsibilities-suport`) dentro de la plataforma PATRIC.IA es responsable de la administración de recursos de apoyo estudiantil, tanto académicos como de salud mental. Su principal enfoque es permitir que los estudiantes conozcan y se comuniquen con recursos de apoyo institucionales (incluyendo la agenda de citas vía mail).

Puerto: `8080` (por defecto). Integrado con **PostgreSQL** como BD principal.

---

## 4. Cómo Funciona

Emplea **Arquitectura Hexagonal (Ports & Adapters)** y **Clean Architecture** estructurado de la siguiente forma:

- **Dominio:** La entidad core `WellnessResource` gestiona dos tipos de creaciones (general o para salud mental), validando que estos últimos contengan información psicológica.
- **Aplicación:** Casos de uso (`CreateWellnessResourceUseCase`, `UpdateWellnessResourceUseCase`, etc.) controlan la orquestación lógica y el mapeo.
- **Infraestructura y Entrypoints:** Controladores como `WellnessController` y adaptadores JPA (ej. `WellnessResourceRepositoryAdapter`) ejecutan las operaciones de red y de persistencia.

### Patrones de Diseño

| Patrón | Descripción |
|---|---|
| **Ports & Adapters** | Los casos de uso fungen como puertos de entrada (Ej. `GetWellnessResourcesPort`) que aíslan el dominio del framework REST. |
| **Value Object** | `ResourceId` y `MailtoAppointment` para abstraer los comportamientos y validación de tipos nativos. |
| **Factory Methods** | Métodos como `createGeneral` y `createMentalHealth` en la entidad core para inicializar con estado consistente. |

---

## 5. Diagrama de Datos

> 📷 **[Insert Image: Diagrama_Entidad.jpg]**

*(Modelo de datos administrado mediante `WellnessResourceEntity` utilizando JPA).*

---

## 6. Diagrama de Clases

> 📷 **[Insert Image: Diagrama_Clases.jpg]**

**Resumen del dominio:**

- **`WellnessResource`**: Representa un recurso de la universidad (contacto, categoría, psicólogo). 
- **`MailtoAppointment`**: Objeto de valor inmutable con los metadatos para enviar un correo de solicitud de cita.

---

## 7. Diagrama de Componentes

> 📷 **[Insert Image: Diagrama_Componentes.png]**

| Componente | Tipo | Interfaz |
|---|---|---|
| `WellnessController` | REST Controller | Expone rutas HTTP en `/api/v1/bienestar/recursos` |
| `GlobalExceptionHandler` | Controller Advice | Centraliza y captura errores de dominio |
| `WellnessResourceJpaRepository` | Spring Data Repository | Ejecuta persistencia en PostgreSQL |

---

## 8. Funcionalidades Principales

<div align="center">

| ID | Funcionalidad | Descripción |
|---|---|---|
| F01 | **Gestión de Recursos (CRUD)** | Creación, consulta (listados y por ID), actualización y eliminación de recursos de bienestar universitario. |
| F02 | **Citas de Salud Mental** | Generación de enlaces `mailto:` con la estructura de la solicitud de citas enfocadas en psicología. |
| F03 | **Filtrado Categórico** | Capacidad de filtrar los recursos por su tipo de categoría (`MENTAL_HEALTH`, `ACADEMIC`, etc). |
| F04 | **Protección JWT** | Autenticación de las rutas del controlador basada en la lectura de tokens. |

</div>

---

## 9. Endpoints

### Resumen

| Método | Endpoint | Funcionalidad |
|---|---|---|
| `GET` | `/api/v1/bienestar/recursos` | Consulta el listado de recursos (Soporta filtro `?categoryFilter=`) |
| `GET` | `/api/v1/bienestar/recursos/{id}` | Retorna el detalle de un recurso mediante su ID |
| `POST` | `/api/v1/bienestar/recursos` | Crea un nuevo recurso |
| `PUT` | `/api/v1/bienestar/recursos/{id}` | Actualiza un recurso existente |
| `DELETE` | `/api/v1/bienestar/recursos/{id}` | Desactiva/elimina un recurso específico |
| `GET` | `/api/v1/bienestar/recursos/{id}/cita-mailto` | Retorna los metadatos de cita (`subject`, `body`, `email`) utilizando el header `X-Student-Name`. |

---

## 10. Colas de Mensajería

Para este módulo, de momento no se maneja cola de mensajería (las interacciones con recursos son mediante persistencia relacional síncrona).

---

## 11. Evidencia de Pruebas

### Clases de prueba implementadas

Se incluye cobertura unitaria a través de test de contexto, adaptadores y casos de uso:

```
src/test/java/edu/eci/patricia/
└── EeveevoidingResponsibilitiesSuportApplicationTests.java  (Y test funcionales anexos).
```

### Cómo ejecutar las pruebas

```bash
# Pruebas unitarias
./mvnw test

# Todas las pruebas + reporte JaCoCo
./mvnw verify

# Reporte de cobertura
./mvnw clean test jacoco:report
```

---

## 12. Evidencia de Cobertura

> 📷 **[Insert Image: jacoco.png]**

---

## 13. Cómo Ejecutar

### Prerrequisitos

- Java 21
- Maven 3.9+
- Docker & Docker Compose

### Opción 1: Local con Maven

```bash
# Levantar en modo desarrollo (requiere definir credenciales de BD y JWT)
./mvnw spring-boot:run
```

**URL:** `http://localhost:8080`
**Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Opción 2: Docker Compose

```bash
docker compose up --build
```

### Variables de Entorno

| Variable | Descripción |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña BD |
| `PORT` | Puerto del servidor (def. 8080) |
| `JWT_SECRET` | Secreto para validación del token |

---

## 14. Evidencia CI/CD

> 📷 **[Insert Image: ci.png]**

> 📷 **[Insert Image: cd.png]**

El proyecto cuenta con GitHub Actions (configuración de CI/CD para compilar y dockerizar la aplicación).

---

## 15. Link Swagger

| Ambiente | URL |
|---|---|
| Local (Maven) | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## 16. Estructura del Código

```
src/main/java/edu/eci/patricia/
├── application/
│   ├── dto/                   # Requests/Responses (ej. WellnessResourceResponse)
│   ├── mapper/                # Mapeo DTO -> Dominio (ej. WellnessResourceMapper)
│   └── usecase/               # Lógica orquestadora (ej. CreateWellnessResourceUseCase)
├── domain/
│   ├── exceptions/            # Excepciones (ej. InvalidCategoryException)
│   ├── model/                 # Entidad (WellnessResource)
│   ├── ports/                 # Interfaces (In/Out)
│   └── valueobjects/          # Objetos de valor (MailtoAppointment)
├── entrypoints/
│   ├── advice/                # Controlador de errores (GlobalExceptionHandler)
│   └── rest/controller/       # WellnessController
└── infrastructure/
    ├── adapters/              # Conexión JPA / Repositorios
    └── config/                # Configuraciones OpenAPI y Spring Security JWT
```

---

## 17. Código Documentado

El código contiene validaciones documentadas (p.e. aserciones en los `ValueObjects` e instanciaciones de dominio restrictivas como `createMentalHealth()`).

---

## 18. Conexiones Externas

| Módulo | Tipo | Dirección | Detalle |
|---|---|---|---|
| **M01 — Autenticación** | JWT (validación local) | M01 → M09 | Valida el JWT configurado mediante `SecurityConfig`. |

---

## 19. Pipeline de Desarrollo

```bash
# Levantar en modo desarrollo
./mvnw spring-boot:run
```

El proyecto posee perfiles como `application-local.properties` y `application-qa.properties`.

---

## 20. Pipeline de Producción

Para el despliegue de los servicios dockerizados se puede usar Docker Compose:

```bash
docker compose up --build
```

---

## 21. Dockerizado

### Dockerfile

La aplicación utiliza un Dockerfile preparado para la contenerización del servicio a través del JAR de Spring Boot (empaquetado por Maven).

---

## 22. Versionamiento

Proyecto empleando Git Flow. El feature principal se desarrolló bajo `feat/wellness`.