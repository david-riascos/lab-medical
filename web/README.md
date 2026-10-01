# web — frontend Nuxt 2 (stack Nuxt + Java)

Frontend de práctica en **Nuxt 2.15.8 + Vuetify 2**, con las mismas versiones que el web real. Consume el backend hermano `../micro`.
Todo el contenido es ficticio y educativo.

## Stack

| Pieza | Detalle |
|---|---|
| Framework | Nuxt 2.15.8, **SPA** (`ssr: false`), router en modo **hash** (como el real) |
| UI | Vuetify 2 (`@nuxtjs/vuetify`), Font Awesome 6 |
| HTTP y avisos | `@nuxtjs/axios`, `@nuxtjs/toast`, `vue-sweetalert2` |
| Vue | 2.7.14 (fijado con `resolutions`) |
| Herramientas | Node 16.16 y **yarn 1.22** (no `npm`: solo yarn respeta `resolutions`) |
| Puerto | **3001** (el web real usa 3000) |

## Cómo correrlo

```powershell
cd web
yarn install
yarn dev            # http://localhost:3001   (la primera compilación tarda ~40 s)
yarn build          # compilación de producción
yarn generate       # sitio estático (target: static)
```

El backend debe estar encendido en el 8081 (`../micro`, `mvn payara-micro:start`).

## Configuración

| Variable | Por defecto | Para qué |
|---|---|---|
| `URL_API` | `http://localhost:8081/` (en desarrollo) | URL base del backend. En producción la base es `/` |

Se lee en `nuxt.config.js` → `axios.baseURL`.

## Estructura

| Archivo | Qué es |
|---|---|
| `nuxt.config.js` | Puerto 3001, SPA, router hash, módulos, tema (`primary #132980`, `accent #FF4F0D`) y `axios.baseURL` |
| `layouts/default.vue` | Barra superior y contenedor |
| `pages/index.vue` | Botón **"Probar backend"**: llama a `GET api/ping` y muestra el resultado en un `v-alert` y un `toast` |

Si el backend responde con error, la pantalla muestra el campo `RESPUESTA` que venga en el cuerpo (mismo patrón que el proyecto real);
si no hay respuesta, avisa que revises el micro en el 8081.

## Trampas ya resueltas (no repetir)

- `package.json` necesita `resolutions.node-releases = 1.1.77` (con Node 16.16 falla si no) y las `resolutions` de `vue`, `vue-template-compiler` y `vue-server-renderer` en 2.7.14.
- Los `devDependencies` `@babel/plugin-proposal-private-property-in-object` y `@babel/plugin-proposal-optional-chaining` son obligatorios: sin ellos Babel falla con "PLACEHOLDER PACKAGE". Ambos vienen del web real.
- Si Nuxt queda colgado: busca quién escucha en el 3001 y cierra solo ese proceso.

## Estado de verificación

- Compila y sirve la página (verificado al crear el proyecto).
- **Pendiente:** clic real en "Probar backend" desde un navegador contra el backend Java (no se ha hecho).
- No tiene pruebas automáticas ni `lint` configurado.
