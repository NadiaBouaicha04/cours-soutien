// Client HTTP vers le back-end : ajoute automatiquement le token à chaque appel
export function useApi() {
  const config = useRuntimeConfig()
  const { token, logout } = useAuth()

  return $fetch.create({
    baseURL: config.public.apiBase,

    // Avant chaque requête : ajouter "Authorization: Bearer <token>"
    onRequest({ options }) {
      if (token.value) {
        options.headers.set('Authorization', `Bearer ${token.value}`)
      }
    },

    // Token expiré ou invalide (401) : on déconnecte et on renvoie au login
    async onResponseError({ response }) {
      if (response.status === 401) {
        await logout()
      }
    },
  })
}
