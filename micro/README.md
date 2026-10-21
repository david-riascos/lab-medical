# micro — backend Java (stack Nuxt + Java)

Backend de práctica en **Java 11 + Jakarta EE 8 + Payara Micro 5.201 + H2**. Replica en pequeño la forma del micro real: capas
`endpoints → services → core`, un `DBPool` como único punto de acceso a la base de datos y la respuesta estándar `{"RESPUESTA","ESTADO"}`.
Su frontend es la carpeta hermana `../web`. Todo el contenido es ficticio y educativo.

## Stack

| Pieza | Detalle |
|---|---|
| Java | 11 |
| API | Jakarta EE 8 (`jakarta.jakartaee-web-api`, scope `provided`); JAX-RS con `@ApplicationPath("api")` |
| Servidor | Payara Micro 5.201 con `payara-micro-maven-plugin` 1.0.7 — puerto **8081**, contexto `ROOT`, `--noCluster` |
| Base de datos | H2 1.4.196 **en memoria** (se borra al apagar). La trae Payara y es la que gana en ejecución; el `pom.xml` declara la misma versión para no confundirse |
| Empaquetado | `war` |

## Cómo correrlo

```powershell
cd micro
mvn payara-micro:start        # http://localhost:8081/api/ping
mvn package                   # solo compilar y empaquetar el war
```

- `mvn` está en el PATH de usuario (Maven 3.6.3 en `C:\Java\apache-maven-3.6.3`). En terminales abiertas antes del 2026-09-25 hay que reabrirlas.
- **La primera vez tarda ~45 s.** El puerto abre unos ~12 s **antes** de que el war termine de desplegarse: si `/api/ping` da 404, espera y reintenta.
- Si queda colgado, busca quién escucha en el 8081 y cierra solo ese proceso.

## Endpoints

| Método y ruta | Respuesta |
|---|---|
| `GET /api/ping` | `200` `{"RESPUESTA":"pong","ESTADO":1,"BASEDEDATOS":"H2 1.4.196 …"}` |
| (si la BD falla) | `400` `{"RESPUESTA":"No se pudo consultar la base de datos","ESTADO":0}` — sin detalles internos |
| `POST /api/login` `{"usuario","clave"}` | `200` `{"RESPUESTA":"Bienvenido, ...","ESTADO":1,"USUARIO":{"ID","USUARIO","NOMBRE"}}` |
| (usuario/clave vacíos, incorrectos o inactivo) | `400` `{"RESPUESTA":"...","ESTADO":0}` — mensaje genérico en credenciales (no dice cuál de las dos falla) |

Contrato: `ESTADO > 0` es éxito; `ESTADO 0` se devuelve como HTTP 400. El login nunca devuelve `CLAVE`, ni en éxito ni en error.

Usuario ficticio de prueba (sembrado al arrancar, ver `EsquemaInicial`): `admin` / `admin123`.

## Configuración (propiedades `-D`)

| Propiedad | Por defecto | Para qué |
|---|---|---|
| `DB_URL` | `jdbc:h2:mem:entrenamiento;DB_CLOSE_DELAY=-1` | Conexión a la BD (para guardar en archivo: `jdbc:h2:file:./data/entrenamiento`) |
| `DB_USER` | `sa` | Usuario |
| `DB_PASSWORD` | vacío | Contraseña |
| `ORIGEN_PERMITIDO` | `http://localhost:3001` | Único origen aceptado por CORS (el micro real usa `*`) |

`DB_URL` y `DB_USER` se pasan en `javaCommandLineOptions` del plugin dentro de `pom.xml`; las otras usan su valor por defecto.

## Estructura (`src/main/java/com/co/entrenamiento/`)

| Capa | Archivo | Responsabilidad |
|---|---|---|
| `configuration` | `ApplicationInit` | Activa JAX-RS bajo `/api` |
| `configuration` | `CORSFilter` | Cabeceras CORS con un solo origen configurable |
| `configuration` | `EsquemaInicial` | `@WebListener`: crea la tabla `USUARIOS` y siembra el usuario ficticio al arrancar (la BD es en memoria) |
| `endpoints` | `PingEndpoint` | Solo traduce HTTP (200 / 400); **no** tiene lógica |
| `endpoints` | `LoginEndpoint` | `POST /api/login`; solo traduce HTTP |
| `services` | `PingService` | Lógica y SQL de la prueba de vida |
| `services` | `LoginService` | Lógica y SQL del login (usuario/clave, activo) |
| `core` | `DBPool` | Único acceso a la BD: `consultar(sql, parámetros…)` → `JsonArray`; `ejecutar(sql, parámetros…)` para DDL/DML |
| `core` | `Claves` | Hash de contraseñas (SHA-256, sin librería nueva); nunca se guarda ni compara en texto plano |

## Estado de verificación

- Compila y responde `GET /api/ping` (verificado al crear el proyecto).
- **Login verificado con clic real** (Chrome + `puppeteer-core`, 2026-10-21): campos vacíos bloqueados por el cliente,
  credenciales incorrectas rechazadas (`400`, mensaje genérico), credenciales correctas (`admin` / `admin123`) devuelven
  `200` con el mensaje de bienvenida.
- **No tiene pruebas automáticas todavía** (ni unitarias ni de integración).

## Limitaciones conocidas

- `DBPool` es una versión mínima: sin pool de conexiones ni transacciones (se irá ampliando en las semanas del plan).
- La BD en memoria se pierde al apagar; `EsquemaInicial` recrea la tabla `USUARIOS` y el usuario ficticio en cada arranque.
- El login no guarda sesión (ni token ni cookie): valida y devuelve el usuario, pero cada pantalla nueva tendrá que
  pedir sesión otra vez hasta que se agregue en una semana siguiente del plan.
