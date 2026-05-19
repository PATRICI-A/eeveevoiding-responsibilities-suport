# eeveevoiding-responsibilities-suport

## Levantar el proyecto

### 1. Crear el archivo `.env`

Copia el archivo de ejemplo:

```bash
cp .env.example .env
```

Ya tiene los valores por defecto listos. No necesitas cambiar nada para correrlo localmente.

### 2. Construir y correr los contenedores

```bash
docker compose up --build
```

Este comando se encarga de todo:
- Compila la aplicación con Maven dentro de Docker
- Levanta la base de datos PostgreSQL
- Levanta la app Spring Boot y la conecta a la base de datos

> La primera vez tarda más porque descarga las imágenes base. Las siguientes veces es más rápido.

### 3. (Opcional) Correr en segundo plano

```bash
docker compose up --build -d
```

Para ver los logs después:

```bash
docker compose logs -f
```

---

## Probar la API

Una vez levantado, abre Swagger en tu navegador:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Ambiente Local (IMPORTANTE LECTURA)

Cuando estes trabajando localmente, es más cómodo **solo levantar la base de datos con Docker** y correr la app desde IntelliJ:

```bash
# Solo levanta PostgreSQL, a
docker compose up db
```

Normalmente para probar swagger o la compilacion de codigo, solo montamos la bd de docker y levantamos la aplicacion desde intelliIDEA
o desde cmd con el comando

```bash
mvn spring-boot:run
```

Cuando acabamos de trabajar localmente, subimos los cambios al repositorio y sera el ambiente de qa quien construya la imagen
Docker, asi que no hace falta crearla manualmente, en otras palabras, solo levanta la base de datos y ya.

---

## Aplicar cambios en la imagen

Cada vez que hagas un cambio en el código y quieras verlo reflejado en Docker:

```bash
docker compose up --build
```

El `--build` le indica a Docker que reconstruya la imagen en lugar de usar la cacheada.

---

## Comandos útiles de Docker

| Comando | Descripción |
|---|---|
| `docker compose up --build` | Construye y levanta todos los contenedores |
| `docker compose up --build -d` | Igual pero en segundo plano |
| `docker compose down` | Detiene y elimina los contenedores |
| `docker compose down -v` | Detiene contenedores y **borra los datos** de la BD |
| `docker compose logs -f` | Ver logs en tiempo real |
| `docker compose ps` | Ver estado de los contenedores |
| `docker compose stop` | Solo detiene sin eliminar |
| `docker compose start` | Vuelve a iniciar contenedores detenidos |

---

## Verificar los contenedores

```bash
docker compose ps
```

Deberías ver dos contenedores con estado `running`:
- `eeveevoiding-postgres-db`
- `eeveevoiding-app`

---

## Entrar a la base de datos

```bash
docker exec -it eeveevoiding-postgres-db psql -U patricia -d eeveevoiding_db
```

### Comandos básicos dentro de psql

| Comando | Descripción |
|---|---|
| `\dt` | Ver todas las tablas |
| `\d nombre_tabla` | Ver estructura de una tabla |
| `SELECT * FROM nombre_tabla;` | Ver datos de una tabla |
| `\q` | Salir de psql |

---

## Detener el proyecto

```bash
docker compose down
```

Si además quieres borrar los datos de la base de datos:

```bash
docker compose down -v
```