# lab-medical

![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-8-blue)
![Payara Micro](https://img.shields.io/badge/Payara%20Micro-5.201-orange)
![H2](https://img.shields.io/badge/H2-in--memory-informational)
![Nuxt](https://img.shields.io/badge/Nuxt-2.15.8-00C58E?logo=nuxtdotjs&logoColor=white)
![Vuetify](https://img.shields.io/badge/Vuetify-2-1867C0?logo=vuetify&logoColor=white)
![Estado](https://img.shields.io/badge/estado-en%20construcci%C3%B3n-yellow)

Laboratorio educativo de práctica, desde cero: réplica a pequeña escala de los flujos de un sistema de información
hospitalaria (HIS) — login, permisos y usuarios, historia clínica, banco de sangre, RIPS — construida para entrenamiento
personal. Este repositorio contiene el stack **Nuxt + Java**, uno de varios en los que se compara el mismo problema con
tecnologías distintas.

> **Todo el contenido es ficticio y educativo.** Ningún dato de paciente, credencial o detalle interno de un proyecto
> productivo se copia aquí; solo se imita la *forma* de flujos reales (capas, contratos de respuesta, validaciones) para
> practicar.

## Contenido

- [Por qué existe](#por-qué-existe)
- [Arquitectura](#arquitectura)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Stack y versiones](#stack-y-versiones)
- [Requisitos previos](#requisitos-previos)
- [Cómo correrlo](#cómo-correrlo)
- [Configuración](#configuración)
- [Contrato de la API](#contrato-de-la-api)
- [Cómo se verifica](#cómo-se-verifica)
- [Hoja de ruta](#hoja-de-ruta)
- [Estado actual](#estado-actual)

## Por qué existe

Es un espacio propio para aprender backend y frontend comparando el **mismo problema** resuelto en varios stacks, en
lugar de solo leer documentación. Cada funcionalidad del plan de entrenamiento (login, historia clínica, banco de sangre,
RIPS, integración) se construye primero aquí, de forma mínima y ficticia, antes de entenderla en profundidad.

## Arquitectura

El backend sigue una separación estricta en tres capas, igual que un sistema real:

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  endpoints   │ ──▶ │   services   │ ──▶ │     core     │ ──▶  Base de datos
│ (JAX-RS,     │     │  (lógica de  │     │ (único punto │
│  solo        │     │   negocio)   │     │  que toca    │
│  delega)     │     │              │     │  la BD)      │
└──────────────┘     └──────────────┘     └──────────────┘
```

- **`endpoints`**: reciben la petición HTTP y delegan; no contienen lógica ni SQL.
- **`services`**: lógica de negocio; único lugar donde se arma el SQL.
- **`core`**: acceso físico a la base de datos (pool de conexiones).

El frontend (Nuxt) consume el backend por HTTP (`axios`) y nunca accede a la base de datos directamente.

## Estructura del repositorio

```
lab-medical/
├── micro/                          # backend — Java 11 + Jakarta EE 8 + Payara Micro
│   ├── pom.xml
│   └── src/main/java/com/co/entrenamiento/
│       ├── configuration/          # arranque y CORS
│       ├── endpoints/              # capa HTTP (JAX-RS)
│       ├── services/               # lógica de negocio
│       └── core/                   # acceso a datos (DBPool)
└── web/                            # frontend — Nuxt 2 + Vuetify 2
    ├── nuxt.config.js
    ├── layouts/
    └── pages/
```

Cada carpeta (`micro/`, `web/`) tiene su propio README con el detalle real: stack completo, cómo correrla, trampas ya
resueltas y estado de verificación. Este archivo es solo el punto de entrada.

| Carpeta | Detalle | Puerto |
|---|---|---|
| [`micro/`](micro/README.md) | Backend | **8081** |
| [`web/`](web/README.md) | Frontend | **3001** |

## Stack y versiones

| Capa | Tecnología | Versión | Notas |
|---|---|---|---|
| Backend — lenguaje | Java | 11 | |
| Backend — API | Jakarta EE 8 | `jakarta.jakartaee-web-api` (provided) | JAX-RS, `@ApplicationPath("api")` |
| Backend — servidor | Payara Micro | 5.201 | `--noCluster`, contexto `ROOT` |
| Backend — base de datos | H2 | 1.4.196, en memoria | se reinicia al apagar el servidor |
| Frontend — framework | Nuxt | 2.15.8 | SPA (`ssr: false`), router en modo hash |
| Frontend — UI | Vuetify | 2 | vía `@nuxtjs/vuetify` |
| Frontend — runtime | Node / Yarn | 16.16 / 1.22 | usar siempre `yarn`, nunca `npm` (ver `resolutions`) |

## Requisitos previos

- JDK 11 y Maven (el proyecto usa Maven 3.6.3).
- Node 16.16 y Yarn 1.22 (versiones fijadas; ver [`web/README.md`](web/README.md) para el porqué).

## Cómo correrlo

```powershell
# 1) backend
cd micro
mvn payara-micro:start      # http://localhost:8081/api/ping  (~45 s la 1.ª vez)

# 2) frontend (en otra terminal)
cd web
yarn install
yarn dev                    # http://localhost:3001  (~40 s la 1.ª vez)
```

> El puerto del backend abre unos segundos **antes** de que el `.war` termine de desplegarse: si `/api/ping` responde
> 404 justo al inicio, espera unos segundos y reintenta.

Otros comandos útiles:

```powershell
cd micro && mvn package     # compilar y empaquetar el war, sin levantar el servidor
cd web && yarn build        # compilación de producción
cd web && yarn generate     # sitio estático
```

## Configuración

Variables del backend (se pasan con `-D`, por ejemplo `mvn payara-micro:start -DORIGEN_PERMITIDO=http://localhost:3001`):

| Variable | Por defecto | Para qué |
|---|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | H2 en memoria | conexión a la base de datos |
| `ORIGEN_PERMITIDO` | `http://localhost:3001` | origen permitido por CORS |

Variable del frontend (en `nuxt.config.js` → `axios.baseURL`):

| Variable | Por defecto | Para qué |
|---|---|---|
| `URL_API` | `http://localhost:8081/` en desarrollo | URL base del backend |

## Contrato de la API

Toda respuesta del backend sigue el mismo formato:

```json
{ "RESPUESTA": "mensaje para el usuario", "ESTADO": 1 }
```

`ESTADO > 0` indica éxito; `ESTADO = 0` se traduce en un HTTP 400 y nunca expone detalles internos (stack traces,
mensajes de la base de datos, etc.) en el cuerpo de la respuesta.

## Cómo se verifica

No basta con un HTTP 200: antes de dar algo por terminado se prueba con clic real en un navegador (Chrome) — abrir la
página, pulsar el botón, leer el resultado y revisar la consola — además de las pruebas unitarias/funcionales que
correspondan a cada capa.

## Hoja de ruta

Plan de entrenamiento de 6 meses (90 min/día), construido lunes/miércoles/viernes:

| Mes | Funcionalidad |
|---|---|
| 1 | Login y permisos/usuarios (tabla `USUARIOS`, `login()`, pantalla con `v-form` y `:rules`) |
| 2 | Historia clínica mínima |
| 3 | Banco de sangre mini |
| 4 | RIPS mini |
| 5 | Integración entre módulos |
| 6 | Debugging y comparación con los demás stacks del laboratorio |

## Estado actual

- Backend y frontend creados, compilando y sirviendo página; **pendiente** el clic real end-to-end del botón
  "Probar backend" contra el backend Java.
- Sin pruebas automáticas todavía en ninguno de los dos lados.
- **Siguiente paso:** login básico — mes 1 de la hoja de ruta.
