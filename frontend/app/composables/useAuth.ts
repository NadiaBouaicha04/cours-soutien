import type { LoginResponse, UtilisateurConnecte } from '~/types/api'

// Gestion de la connexion : token, utilisateur connecté, login, logout
export function useAuth() {
  const config = useRuntimeConfig()

  // Rangés dans des cookies : ils survivent au rechargement de la page,
  // et expirent après 60 minutes, comme le token côté back-end
  const token = useCookie<string | null>('token', { maxAge: 60 * 60, sameSite: 'strict' })
  const utilisateur = useCookie<UtilisateurConnecte | null>('utilisateur', { maxAge: 60 * 60, sameSite: 'strict' })

  const estConnecte = computed(() => !!token.value)

  async function login(email: string, motDePasse: string) {
    const reponse = await $fetch<LoginResponse>('/auth/login', {
      baseURL: config.public.apiBase, // http://localhost:8080/api
      method: 'POST',
      body: { email, motDePasse },
    })
    token.value = reponse.token
    utilisateur.value = { prenom: reponse.prenom, nom: reponse.nom, role: reponse.role }
    return reponse.role
  }

  function logout() {
    token.value = null
    utilisateur.value = null
    return navigateTo('/login')
  }

  return { token, utilisateur, estConnecte, login, logout }
}
