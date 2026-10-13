<script setup lang="ts">
import type { Utilisateur } from '~/types/api'

const { utilisateur } = useAuth()
const api = useApi()

// Appel à GET /api/gestion/utilisateurs (réservé au rôle GESTIONNAIRE)
const { data: utilisateurs, error } = await useAsyncData('utilisateurs', () => api<Utilisateur[]>('/gestion/utilisateurs'))
</script>

<template>
  <div>
    <h1>Bonjour {{ utilisateur?.prenom }}</h1>

    <p v-if="error" class="erreur">{{ messageErreur(error) }}</p>

    <div v-else-if="utilisateurs" class="carte">
      <h2>Comptes utilisateurs</h2>
      <p>{{ utilisateurs.length }} comptes enregistrés.</p>
    </div>
  </div>
</template>
