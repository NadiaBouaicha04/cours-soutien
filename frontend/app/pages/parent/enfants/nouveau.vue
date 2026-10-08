<!-- Ajout d'un enfant -->
<script setup lang="ts">
import type { Enfant, EnfantRequest } from '~/types/api'

const api = useApi()
const erreur = ref('')
const chargement = ref(false)

async function ajouter(donnees: EnfantRequest) {
  erreur.value = ''
  chargement.value = true
  try {
    // POST /api/parent/enfants
    const enfant = await api<Enfant>('/parent/enfants', { method: 'POST', body: donnees })
    // On va directement sur sa fiche pour l'inscrire à un cours
    await navigateTo(`/parent/enfants/${enfant.id}`)
  } catch (e) {
    // ex. 409 si l'enfant existe déjà, 400 si un champ est invalide
    erreur.value = messageErreur(e)
  } finally {
    chargement.value = false
  }
}
</script>

<template>
  <div>
    <NuxtLink to="/parent">← Tableau de bord</NuxtLink>
    <h1>Ajouter un enfant</h1>

    <div class="carte">
      <p v-if="erreur" class="erreur">{{ erreur }}</p>
      <EnfantForm libelle-bouton="Ajouter" :chargement="chargement" @valider="ajouter" />
    </div>
  </div>
</template>
