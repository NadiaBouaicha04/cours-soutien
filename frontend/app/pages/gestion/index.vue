<!-- Accueil du gestionnaire : chiffres clés et accès aux écrans de gestion -->
<script setup lang="ts">
import type { CoursGestion, Utilisateur } from '~/types/api'

const { utilisateur } = useAuth()
const api = useApi()

const { data: utilisateurs } = await useAsyncData('utilisateurs', () => api<Utilisateur[]>('/gestion/utilisateurs'))
const { data: cours } = await useAsyncData('cours-gestion', () => api<CoursGestion[]>('/gestion/cours'))

const nbParents = computed(() => utilisateurs.value?.filter(u => u.role === 'PARENT').length ?? 0)
const nbInscrits = computed(() => cours.value?.reduce((total, c) => total + c.nombreInscrits, 0) ?? 0)
const nbComplets = computed(() => cours.value?.filter(c => c.nombreInscrits >= c.capacite).length ?? 0)

const ecrans = [
  { lien: '/gestion/utilisateurs', titre: 'Utilisateurs', texte: 'Comptes, paiements et soldes' },
  { lien: '/gestion/cours', titre: 'Cours', texte: 'Créneaux, salles et niveaux des cours' },
  { lien: '/gestion/salles', titre: 'Salles', texte: 'Salles et capacités' },
  { lien: '/gestion/niveaux', titre: 'Niveaux', texte: 'Niveaux et tarifs' },
  { lien: '/gestion/recap', titre: 'Récapitulatif', texte: 'Les cours avec leurs inscrits' },
]
</script>

<template>
  <div>
    <h1>Bonjour {{ utilisateur?.prenom }}</h1>

    <section class="carte bloc">
      <div class="chiffres">
        <div><span class="discret">Parents</span><strong>{{ nbParents }}</strong></div>
        <div><span class="discret">Enfants inscrits</span><strong>{{ nbInscrits }}</strong></div>
        <div><span class="discret">Cours complets</span><strong>{{ nbComplets }} / {{ cours?.length ?? 0 }}</strong></div>
      </div>
    </section>

    <div class="liste-cartes">
      <NuxtLink v-for="e in ecrans" :key="e.lien" :to="e.lien" class="carte lien-carte">
        <h3>{{ e.titre }}</h3>
        <p class="discret">{{ e.texte }}</p>
      </NuxtLink>
    </div>
  </div>
</template>
