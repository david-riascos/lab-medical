<template>
  <v-container>
    <v-row justify="center" class="ma-0">
      <v-col cols="12" md="4">
        <v-card>
          <v-card-title>Iniciar sesión</v-card-title>
          <v-card-text>
            <v-form ref="formulario" v-model="formularioValido" @submit.prevent="ingresar">
              <v-text-field
                v-model="usuario"
                label="Usuario"
                prepend-icon="mdi-account"
                :rules="reglas.usuario"
                autofocus
              />
              <v-text-field
                v-model="clave"
                label="Contraseña"
                type="password"
                prepend-icon="mdi-lock"
                :rules="reglas.clave"
                @keyup.enter="ingresar"
              />
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn color="primary" :loading="cargando" @click="ingresar">
              <v-icon left>mdi-login</v-icon>
              Ingresar
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'LoginPage',
  data () {
    return {
      formularioValido: false,
      cargando: false,
      usuario: '',
      clave: '',
      reglas: {
        usuario: [v => !!v || 'El usuario es obligatorio'],
        clave: [v => !!v || 'La clave es obligatoria']
      }
    }
  },
  methods: {
    async ingresar () {
      // Las :rules validan en el navegador; el backend valida otra vez (nunca se confía solo en el cliente).
      if (!this.$refs.formulario.validate()) {
        return
      }
      this.cargando = true
      try {
        const respuesta = await this.$axios.$post('api/login', {
          usuario: this.usuario,
          clave: this.clave
        })
        this.$toast.success(respuesta.RESPUESTA)
        // TODO (siguiente paso del plan): guardar la sesión y redirigir a una pantalla protegida.
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
