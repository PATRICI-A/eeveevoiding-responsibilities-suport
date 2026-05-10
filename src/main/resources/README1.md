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
- Compila la aplicación con Maven
- Levanta la base de datos PostgreSQL
- Espera a que PostgreSQL esté listo antes de iniciar Spring Boot
- Levanta ambos contenedores
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

Una vez levantado, abre el Swagger en tu navegador:

```
http://localhost:8080/swagger-ui/index.html
```
 
---

## Verificar los contenedores

```bash
docker compose ps
```

Deberías ver dos contenedores con estado `running`:
- `eeveevoiding-postgres-db`
- `eeveevoiding-app`
---

## Entrar a la Base de datos

```bash
docker exec -it eeveevoiding-postgres-db psql -U patricia -d eeveevoiding_db
```

## Detener el proyecto



```bash
docker compose down
```

Si además quieres borrar los datos de la base de datos:

```bash
docker compose down -v
```