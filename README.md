# eeveevoiding-responsibilities-suport

# PATRICI.A — M09 Eventos Universitarios
### Equipo 5 — Campus · Escuela Colombiana de Ingeniería Julio Garavito · DOSW 2026

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.9.x-red?style=flat-square&logo=apachemaven)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-green?style=flat-square&logo=swagger)

---

## Tabla de Contenidos

1. [Descripción](#1-descripción)
2. [Funcionalidades](#2-funcionalidades)
3. [Tecnologías](#3-tecnologías)
4. [Estructura del Proyecto](#4-estructura-del-proyecto)
5. [Requisitos Previos](#5-requisitos-previos)
6. [Variables de Entorno](#7-variables-de-entorno)
7. [Endpoints de la API](#8-endpoints-de-la-api)
8. [Modelo de Datos](#9-modelo-de-datos)
9. [Diagramas de Arquitectura](#10-diagramas-de-arquitectura)
10. [Pruebas](#11-pruebas)
11. [Manejo de Errores](#12-manejo-de-errores)
12. [Dependencias con Otros Módulos](#13-dependencias-con-otros-módulos)
13. [Integrantes](#14-integrantes)

---

## 1. Descripción

**M09 — Eventos Universitarios** es el microservicio de PATRICI.A encargado de centralizar el descubrimiento y gestión de eventos oficiales del campus universitario.

El módulo resuelve un problema concreto: los estudiantes pierden eventos importantes porque no existe un canal centralizado oficial. Este servicio expone un feed paginado de eventos activos con soporte de filtros por categoría y fecha, consumido por el frontend y por los equipos dependientes (Equipo 3 y Equipo 4).

**Base URL:** `/api/v1/eventos`  
**Documentación Swagger:** `/swagger-ui.html`

---

## 2. Funcionalidades

| ID   | Funcionalidad                                         | Estado       |
|------|-------------------------------------------------------|--------------|
| RF20 | Modelo base del Evento — estructura de datos completa | Implementado |
| RF11 | Feed de eventos con filtros por categoría y fecha     | Implementado |
| RF22 | Publicación y edición de eventos (Organizador)        | En desarrollo |
| RF13 | RSVP y agenda personal del estudiante                 | Planificado  |
| RF14 | Dashboard estadístico de eventos                      | Planificado  |

---

## 3. Tecnologías

| Tecnología        | Versión        | Uso                        |
|-------------------|----------------|----------------------------|
| Java              | 21             | Lenguaje principal         |
| Spring Boot       | 3.3.0          | Framework del microservicio |
| Spring Data JPA   | Boot 3.3       | Capa de persistencia ORM   |
| Spring Security   | Boot 3.3       | Autenticación JWT          |
| PostgreSQL        | 16             | Base de datos principal    |
| H2                | Embebida       | Base de datos para pruebas |
| Lombok            | Última estable | Reducción de boilerplate   |
| SpringDoc OpenAPI | 2.5.0          | Documentación Swagger      |
| JaCoCo            | Última estable | Cobertura de código        |
| Maven             | 3.9.x          | Gestión de dependencias    |
| Docker            | Última estable | Contenedorización          |

---

## 4. Estructura del Proyecto

```
edu.eci.patricia
├── domain
│   ├──exception
│   ├── model                   
│   ├── valueobjects                
│   └── ports
│       ├── in     
│       └── out        
├── application
│   ├── mapper
│   ├── service                  
│   ├── usercase                
│   └── dto       
│       ├── request     
│       └── response   
├── entrypoints
│   ├── advice             
│   └── rest
│       ├── controller     
│       └── mapper   
├── infrastructure
│   ├── config          
│   ├── external          
│   └── adapters        
│       ├── adapter     
│       └── persitence
│           ├── entity
│           ├── mapper
│           └── repository
```

---

## 5. Requisitos Previos

Antes de ejecutar el proyecto asegúrate de tener instalado:

- **Java 21** o superior
- **Maven 3.9.x** o superior
- **PostgreSQL 16** vía Docker
- **Docker** y **Docker Compose** (para levantar con contenedores)

---

## 6. Endpoints de la API

Todos los endpoints requieren autenticación mediante **Bearer Token JWT** en el header:
```
Authorization: Bearer <token>
```

### M09 — Eventos Universitarios

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/eventos` | Feed paginado de eventos ACTIVOS con filtros |
| `GET` | `/api/v1/eventos/{id}` | Detalle de un evento por ID |
| `POST` | `/api/v1/eventos` | Crear nuevo evento (solo ORGANIZADOR) |
| `PUT` | `/api/v1/eventos/{id}` | Editar evento existente |
| `PATCH` | `/api/v1/eventos/{id}/cancelar` | Cancelar un evento |
| `POST` | `/api/v1/eventos/{id}/rsvp` | Confirmar asistencia |
| `DELETE` | `/api/v1/eventos/{id}/rsvp` | Cancelar asistencia |
| `GET` | `/api/v1/eventos/agenda` | Agenda personal del estudiante |

---

### GET `/api/v1/eventos` — Parámetros de filtrado

| Parámetro   | Tipo       | Obligatorio | Descripción                                                         |
|-------------|------------|-------------|---------------------------------------------------------------------|
| `categoria` | Enum       | No          | `ACADEMICO`, `CULTURAL`, `DEPORTIVO`, `SOCIAL`, `BIENESTAR`, `OTRO` |
| `fecha`     | LocalDate  | No          | Formato `YYYY-MM-DD`. Filtra eventos del día indicado               |
| `page`      | Integer    | No          | Número de página. Default: `0`. Mínimo: `0`                         |
| `size`      | Integer    | No          | Tamaño de página. Default: `20`. Máximo: `50`                       |

---

## 9. Modelo de Datos

### Tabla `eventos`

| Campo             | Tipo         | Restricciones            | Descripción                                                         |
|-------------------|--------------|--------------------------|---------------------------------------------------------------------|
| `id`              | UUID         | PK, NOT NULL             | Identificador único del evento                                      |
| `nombre`          | VARCHAR(100) | NOT NULL                 | Nombre del evento                                                   |
| `descripcion`     | TEXT         | NOT NULL                 | Descripción completa                                                |
| `fecha_hora`      | DATETIME     | NOT NULL                 | Fecha y hora de inicio                                              |
| `lugar`           | VARCHAR(150) | NOT NULL                 | Lugar dentro del campus                                             |
| `categoria`       | ENUM         | NOT NULL                 | `ACADEMICO`, `CULTURAL`, `DEPORTIVO`, `SOCIAL`, `BIENESTAR`, `OTRO` |
| `tipo`            | ENUM         | NOT NULL                 | `ABIERTO` = sin límite · `CON_CUPO` = con límite                    |
| `cupo_maximo`     | INTEGER      | NULL si ABIERTO          | Cupo total del evento                                               |
| `cupo_disponible` | INTEGER      | NULL si ABIERTO          | Cupos restantes                                                     |
| `organizador_id`  | UUID         | FK                       | ID del usuario organizador                                          |
| `estado`          | ENUM         | NOT NULL, DEFAULT ACTIVO | `ACTIVO`, `CANCELADO`                                               |
| `creado_en`       | DATETIME     | NOT NULL                 | Fecha de creación del registro                                      |

### Tabla `event_rsvp`

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identificador único del RSVP |
| `evento_id` | UUID | FK → eventos.id | Evento confirmado |
| `estudiante_id` | UUID | FK | ID del estudiante |
| `confirmado_en` | DATETIME | NOT NULL | Fecha de confirmación |
| `estado` | ENUM | NOT NULL | `CONFIRMADO`, `CANCELADO` |

---

## 10. Diagramas de Arquitectura

Los diagramas están disponibles en la carpeta `/docs` del repositorio.

### Diagrama de Clases

>Falta poner la imagen del diagrama que tenemos
---

### Diagrama de Componentes Específico

>Falta poner la imagen del diagrama que tenemos
---

### Diagrama Entidad-Relación
>Falta poner la imagen del diagrama que tenemos

---

## 11. Pruebas

### Ejecutar todas las pruebas
```bash
   mvn test
```

### Ejecutar con reporte de cobertura JaCoCo
```bash
   mvn test jacoco:report
```

El reporte se genera en: `target/site/jacoco/index.html`

### Escenarios de prueba implementados

| ID     | Funcionalidad        | Escenario                              | Resultado esperado                             |
|--------|----------------------|----------------------------------------|------------------------------------------------|
| TP-01  | GET /api/v1/eventos  | Happy path — sin filtros               | `200 OK` con lista paginada de eventos ACTIVOS |
| TP-02  | GET /api/v1/eventos  | Filtro por categoría válida            | `200 OK` con eventos filtrados                 |
| TP-03  | GET /api/v1/eventos  | Filtro por fecha válida                | `200 OK` con eventos del día indicado          |
| TP-04  | GET /api/v1/eventos  | Filtros combinados (categoría + fecha) | `200 OK` con intersección de filtros           |
| TP-05  | GET /api/v1/eventos  | Sin eventos disponibles                | `200 OK` con lista vacía `[]`                  |
| TP-06  | GET /api/v1/eventos  | Categoría con valor inválido           | `400 Bad Request` con mensaje descriptivo      |
| TP-07  | GET /api/v1/eventos  | Fecha con formato inválido             | `400 Bad Request` con mensaje descriptivo      |
| TP-08  | GET /api/v1/eventos  | Sin token JWT                          | `401 Unauthorized`                             |
| TP-09  | GET /api/v1/eventos  | Token expirado                         | `401 Unauthorized`                             |
| TP-10  | GET /api/v1/eventos  | page negativo                          | `400 Bad Request`                              |

---

## 12. Manejo de Errores

Todos los errores siguen el formato estándar del sistema PATRICI.A:

```json
{
  "timestamp": "2026-05-10T14:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El parámetro 'categoria' tiene un valor inválido: 'MUSICA'",
  "path": "/api/v1/eventos"
}
```

| Código | Tipo | Escenario |
|---|---|---|
| `400` | Bad Request | Parámetros inválidos (enum incorrecto, fecha mal formada, page negativo) |
| `401` | Unauthorized | Token JWT ausente, inválido o expirado |
| `403` | Forbidden | Acceso a recurso de otro usuario |
| `404` | Not Found | Evento no encontrado por ID |
| `409` | Conflict | RSVP duplicado — estudiante ya confirmó asistencia |
| `422` | Unprocessable Entity | Evento sin cupo disponible o en estado CANCELADO |
| `500` | Internal Server Error | Error inesperado del servidor |

---

## 13. Dependencias con Otros Módulos

| Módulo | Tipo | Descripción |
|---|---|---|
| **M01 — Autenticación** | Fuerte | Todos los endpoints requieren JWT válido emitido por M01. El `userId` del token identifica al estudiante en cada operación. |
| **M05 — Notificaciones** | Débil | Al confirmar RSVP, M09 emite un evento que M05 procesa para notificar al estudiante. |
| **M03 — Perfil** | Datos | M09 puede consultar los intereses del estudiante para personalizar el feed de eventos. |

---

## 14. Integrantes

| Nombre                  | Rol                            | Responsabilidad principal                                                      |
|-------------------------|--------------------------------|--------------------------------------------------------------------------------|
| Tomas Espitia Quiroga   | Lider                          | Organizacion, Coordinacion, Documentacion para la funcionalidad                |
| Juan Sebastian Gonzalez | Backend Developer / Arquitecto | Estructura del microservicio, modelo de datos, repositorios, Swagger, diagramas |
| Camilo Cristancho       | Backend Developer              | EventoController, EventoService, pruebas unitarias                             |
| Andres Pineda           | Frontend UX                    | Mockups Figma del feed y detalle de evento                                     |

---

*PATRICI.A · Equipo 5 Campus · Escuela Colombiana de Ingeniería Julio Garavito · DOSW 2026*

 
 # #   E q u i p o  
 M � d u l o   d e s a r r o l l a d o   p o r   e l   E q u i p o   5      C a m p u s ,   E s c u e l a   C o l o m b i a n a   d e   I n g e n i e r � a   J u l i o   G a r a v i t o .  
 