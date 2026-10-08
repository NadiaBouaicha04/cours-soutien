<script setup lang="ts">
// Cette page n'utilise pas le layout par défaut (pas de menu avant connexion)
definePageMeta({ layout: false })

const { login } = useAuth()

// Données du formulaire (réactives : l'écran se met à jour quand elles changent)
const email = ref('')
const motDePasse = ref('')
const erreur = ref('')
const chargement = ref(false)

async function seConnecter() {
  erreur.value = ''
  chargement.value = true
  try {
    const role = await login(email.value, motDePasse.value)
    await navigateTo(accueilDuRole(role))
  } catch (e) {
    // ex. 401 « E-mail ou mot de passe incorrect »
    erreur.value = messageErreur(e)
  } finally {
    chargement.value = false
  }
}
</script>

<template>
  <div class="page-login">
    <form class="carte" @submit.prevent="seConnecter">
      <h1>Connexion</h1>
      <p class="discret">Cours de soutien</p>

      <label>
        E-mail
        <input v-model="email" type="email" required autocomplete="username" />
      </label>

      <label>
        Mot de passe
        <input v-model="motDePasse" type="password" required autocomplete="current-password" />
      </label>

      <p v-if="erreur" class="erreur">{{ erreur }}</p>

      <button type="submit" :disabled="chargement">
        {{ chargement ? 'Connexion…' : 'Se connecter' }}
      </button>
    </form>
  </div>
</template>
