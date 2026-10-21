<template>
  <v-app>
    <v-app-bar app color="primary" dark dense>
      <v-toolbar-title>Proyecto Nuevo · Entrenamiento</v-toolbar-title>
      <v-spacer />
      <v-btn text nuxt-link to="/">Ping</v-btn>
      <v-btn text nuxt-link to="/login">Login</v-btn>
    </v-app-bar>
    <v-main>
      <nuxt />
    </v-main>
  </v-app>
</template>
