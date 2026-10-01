# lab-medical

Laboratorio educativo de práctica: réplica a pequeña escala de los flujos de un sistema de información hospitalaria (HIS) —
login, permisos/usuarios, historia clínica, banco de sangre, RIPS — usado para entrenamiento personal comparando el mismo
problema en distintos stacks. Este repositorio contiene el stack **Nuxt + Java**.

> **Todo el contenido es ficticio y educativo.** No contiene datos reales de pacientes, credenciales ni detalles internos
> de ningún proyecto productivo.

## Estructura

| Carpeta | Qué es | Puerto |
|---|---|---|
| [`micro/`](micro/README.md) | Backend: Java 11, Jakarta EE 8, Payara Micro 5.201, H2 en memoria | 8081 |
| [`web/`](web/README.md) | Frontend: Nuxt 2.15.8, Vuetify 2 | 3001 |

Capas en el backend: `endpoints` (solo delega) → `services` (lógica) → `core` (único que toca la base de datos).
Contrato de respuesta común: `{"RESPUESTA": "...", "ESTADO": n}` (`ESTADO > 0` = éxito).

El detalle de cada parte (stack, cómo correrla, trampas resueltas, estado de verificación) vive en el README de su propia
carpeta; este archivo es solo el punto de entrada.

## Cómo correrlo

```powershell
# backend
cd micro
mvn payara-micro:start      # http://localhost:8081/api/ping  (~45 s la 1.ª vez)

# frontend (en otra terminal)
cd web
yarn install
yarn dev                    # http://localhost:3001  (~40 s la 1.ª vez)
```

El puerto del backend abre unos segundos antes de que termine de desplegarse: si `/api/ping` da 404 al inicio, espera y
reintenta.

## Cómo se verifica

Clic real en el navegador (abrir la página, pulsar el botón, leer el resultado y revisar la consola), no solo HTTP 200.
Además, las pruebas unitarias/funcionales de cada stack cuando existan.

## Estado

- Backend y frontend creados y compilando; pendiente el clic real end-to-end del botón "Probar backend" contra el backend
  Java.
- Siguiente paso: login básico (tabla `USUARIOS`, `login()` en el backend, pantalla con `v-form` y `:rules` en el
  frontend).
