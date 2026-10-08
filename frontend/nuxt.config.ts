// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  // Application rendue dans le navigateur uniquement (SPA) :
  // tout est derrière une connexion, pas besoin de rendu côté serveur
  ssr: false,

  // Feuille de style globale
  css: ['~/assets/css/main.css'],

  // Configuration accessible dans le code avec useRuntimeConfig()
  runtimeConfig: {
    public: {
      apiBase: 'http://localhost:8080/api',
    },
  },
})
