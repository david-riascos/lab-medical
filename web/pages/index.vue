<template>
  <v-container>
    <v-row justify="center" class="ma-0">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Prueba de conexión</v-card-title>
          <v-card-text>
            Comprueba que este frontend (Nuxt 2 + Vuetify 2) conversa con el backend
            (Payara Micro + H2) llamando a <strong>GET api/ping</strong>.
            <div class="mt-2">Backend: <code>{{ urlBackend }}</code></div>
            <v-alert
              v-if="resultado"
              :type="resultado.ESTADO > 0 ? 'success' : 'error'"
              dense
              text
              class="mt-4 mb-0"
            >
              {{ resultado.RESPUESTA }}
              <span v-if="resultado.BASEDEDATOS"> · {{ resultado.BASEDEDATOS }}</span>
            </v-alert>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" :loading="cargando" @click="probarBackend">
              <v-icon left>mdi-lan-connect</v-icon>
              Probar backend
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'IndexPage',
  data () {
    return {
      cargando: false,
      resultado: null
    }
  },
  computed: {
    urlBackend () {
      return this.$axios.defaults.baseURL
    }
  },
  methods: {
    async probarBackend () {
      this.cargando = true
      this.resultado = null
      try {
        this.resultado = await this.$axios.$get('api/ping')
        this.$toast.success(this.resultado.RESPUESTA)
      } catch (error) {
        this.$toast.error(this.mensajeError(error))
      } finally {
        this.cargando = false
      }
    },
    // Mismo patrón del proyecto real: mostrar el RESPUESTA del backend si existe
    mensajeError (error) {
      return (error.response && error.response.data && error.response.data.RESPUESTA) ||
        'No se pudo conectar con el backend. ¿Está encendido el micro en el puerto 8081?'
    }
  }
}
</script>
