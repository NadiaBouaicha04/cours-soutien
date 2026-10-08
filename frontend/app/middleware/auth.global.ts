// Middleware global : exécuté avant l'affichage de CHAQUE page
export default defineNuxtRouteMiddleware((to) => {
  const { estConnecte, utilisateur } = useAuth()
  const role = utilisateur.value?.role

  // Page de connexion : si déjà connecté, on va directement à son accueil
  if (to.path === '/login') {
    return estConnecte.value ? navigateTo(accueilDuRole(role)) : undefined
  }

  // Toutes les autres pages exigent d'être connecté
  if (!estConnecte.value) {
    return navigateTo('/login')
  }

  // Chaque rôle reste dans son espace
  if (to.path.startsWith('/parent') && role !== 'PARENT') {
    return navigateTo(accueilDuRole(role))
  }
  if (to.path.startsWith('/gestion') && role !== 'GESTIONNAIRE') {
    return navigateTo(accueilDuRole(role))
  }

  // Racine du site : vers l'accueil du rôle
  if (to.path === '/') {
    return navigateTo(accueilDuRole(role))
  }
})
