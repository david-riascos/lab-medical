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

## Endpoint

| Método y ruta | Respuesta |
|---|---|
| `GET /api/ping` | `200` `{"RESPUESTA":"pong","ESTADO":1,"BASEDEDATOS":"H2 1.4.196 …"}` |
| (si la BD falla) | `400` `{"RESPUESTA":"No se pudo consultar la base de datos","ESTADO":0}` — sin detalles internos |

Contrato: `ESTADO > 0` es éxito; `ESTADO 0` se devuelve como HTTP 400.

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
| `endpoints` | `PingEndpoint` | Solo traduce HTTP (200 / 400); **no** tiene lógica |
| `services` | `PingService` | Lógica y SQL de la prueba de vida |
| `core` | `DBPool` | Único acceso a la BD (`consultar(sql, parámetros…)` → `JsonArray`) |

## Estado de verificación

- Compila y responde `GET /api/ping` (verificado al crear el proyecto).
- **No tiene pruebas automáticas todavía.**
- **Pendiente:** la prueba con clic real desde el navegador (el botón "Probar backend" del frontend). Hasta ahora backend y frontend se comprobaron cada uno por separado y el CORS con una petición `curl` que simula el origen del frontend.

## Limitaciones conocidas

- `DBPool` es una versión mínima: sin pool de conexiones ni transacciones (se irá ampliando en las semanas del plan).
- La BD en memoria se pierde al apagar; no hay migraciones ni tablas todavía (la tabla `USUARIOS` es de la semana 1).
