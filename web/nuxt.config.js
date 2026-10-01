import es from 'vuetify/es5/locale/es'

export default {
  // Sin renderizado en servidor (SPA), igual que el web real
  ssr: false,
  target: 'static',

  // Puerto propio para no chocar con el 3000 del proyecto real
  server: {
    port: 3001
  },

  head: {
    titleTemplate: '%s - Proyecto Nuevo',
    title: 'Entrenamiento',
    htmlAttrs: {
      lang: 'es'
    },
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' }
    ]
  },

  css: [
    '@fortawesome/fontawesome-free/css/all.css'
  ],

  components: true,

  buildModules: [
    '@nuxtjs/vuetify'
  ],

  modules: [
    '@nuxtjs/axios',
    '@nuxtjs/toast',
    [
      'vue-sweetalert2/nuxt',
      {
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#132980',
        cancelButtonColor: '#DC3545',
        confirmButtonText: 'Si',
        cancelButtonText: 'No',
        allowOutsideClick: false
      }
    ]
  ],

  // Modo hash, como el web real
  router: {
    mode: 'hash'
  },

  toast: {
    duration: 5000,
    iconPack: 'mdi'
  },

  axios: {
    // En desarrollo apunta al micro nuevo (Payara en el 8081); en produccion, a la misma ruta base
    baseURL: process.env.NODE_ENV === 'production' ? '/' : (process.env.URL_API || 'http://localhost:8081/')
  },

  vuetify: {
    lang: {
      locales: { es },
      current: 'es'
    },
    theme: {
      dark: false,
      themes: {
        light: {
          primary: '#132980',
          accent: '#FF4F0D'
        }
      }
    }
  }
}
