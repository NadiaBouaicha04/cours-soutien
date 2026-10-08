<!-- Tableau de bord du parent : résumé du solde + liste des enfants -->
<script setup lang="ts">
import type { Enfant, Solde } from '~/types/api'

const { utilisateur } = useAuth()
const api = useApi()

// Deux appels à l'API, lancés au chargement de la page
const { data: solde } = await useAsyncData('solde', () => api<Solde>('/parent/solde'))
const { data: enfants, error } = await useAsyncData('enfants', () => api<Enfant[]>('/parent/enfants'))
</script>

<template>
  <div>
    <h1>Bonjour {{ utilisateur?.prenom }}</h1>

    <!-- Résumé du solde -->
    <section v-if="solde" class="carte bloc">
      <div class="entete-bloc">
        <h2>Mon solde</h2>
        <NuxtLink to="/parent/solde">Voir le détail</NuxtLink>
      </div>
      <div class="chiffres">
        <div><span class="discret">Total dû</span><strong>{{ formatEuros(solde.totalDu) }}</strong></div>
        <div><span class="discret">Déjà payé</span><strong>{{ formatEuros(solde.totalPaye) }}</strong></div>
        <div><span class="discret">Reste à payer</span><strong>{{ formatEuros(solde.soldeRestant) }}</strong></div>
      </div>
    </section>

    <!-- Liste des enfants -->
    <section class="bloc">
      <div class="entete-bloc">
        <h2>Mes enfants</h2>
        <NuxtLink to="/parent/enfants/nouveau" class="bouton">Ajouter un enfant</NuxtLink>
      </div>

      <p v-if="error" class="erreur">{{ messageErreur(error) }}</p>
      <p v-else-if="enfants?.length === 0" class="discret">Aucun enfant enregistré pour le moment.</p>

      <div class="liste-cartes">
        <div v-for="enfant in enfants" :key="enfant.id" class="carte">
          <h3>{{ enfant.prenom }} {{ enfant.nom }}</h3>
          <p class="discret">{{ enfant.niveau }}<span v-if="enfant.etablissement"> · {{ enfant.etablissement }}</span></p>

          <p v-if="enfant.cours.length > 0">
            Cours : {{ formatCreneau(enfant.cours[0]) }}, {{ enfant.cours[0].salle }}
          </p>
          <p v-else class="avertissement">Pas encore inscrit à un cours</p>

          <NuxtLink :to="`/parent/enfants/${enfant.id}`">Gérer</NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>
