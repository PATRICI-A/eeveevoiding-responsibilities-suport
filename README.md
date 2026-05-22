# eeveevoiding-responsibilities-suport

Microservicio Spring Boot del sistema PATRICI.A para bienestar y soporte.

## Correr local en Windows

Desde la raiz del proyecto, levanta PostgreSQL:

```powershell
docker compose up -d postgres
```

Luego configura las variables y arranca la app:

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/db_suport"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
$env:JWT_SECRET="local-dev-secret-not-for-production-at-all-minimum-32"
$env:DB_POOL_SIZE="3"
$env:DB_MIN_IDLE="0"
.\mvnw.cmd spring-boot:run
```

Swagger queda disponible en:

```text
http://localhost:8080/swagger-ui.html
```

## Comandos utiles

Solo levantar la base:

```powershell
docker compose up -d postgres
```

Ver logs de la app o la base:

```powershell
docker compose logs -f
```

Detener contenedores:

```powershell
docker compose down
```

Borrar tambien los datos locales de PostgreSQL:

```powershell
docker compose down -v
```

## Configuracion local

La app local usa:

- PostgreSQL: `jdbc:postgresql://localhost:5433/db_suport`
- Usuario: `postgres`
- Password: `postgres`
- Perfil: `local`
- Pool local: `DB_POOL_SIZE=3`, `DB_MIN_IDLE=0`

## QA/Prod

QA y Prod pueden compartir la misma base de datos. Para evitar agotar conexiones, el pool queda configurable por variables:

```text
DB_POOL_SIZE
DB_MIN_IDLE
```

Valores sugeridos:

```text
QA:   DB_POOL_SIZE=1, DB_MIN_IDLE=0
Prod: DB_POOL_SIZE=2, DB_MIN_IDLE=0
```
