<!-- Récapitulatif : chaque cours avec la liste de ses inscrits -->
<script setup lang="ts">
import type { CoursRecap } from '~/types/api'

const api = useApi()
const { data: recap, error } = await useAsyncData('recap', () => api<CoursRecap[]>('/gestion/cours/recap'))

// Filtre : afficher seulement les cours qui ont des inscrits
const seulementInscrits = ref(true)
const affiches = computed(() =>
  (recap.value ?? []).filter(c => !seulementInscrits.value || c.nombreInscrits > 0))
</script>

<template>
  <div>
    <div class="entete-bloc">
      <h1>Récapitulatif des cours</h1>
      <label class="case">
        <input v-model="seulementInscrits" type="checkbox" />
        Seulement les cours avec des inscrits
      </label>
    </div>

    <p v-if="error" class="erreur">{{ messageErreur(error) }}</p>
    <p v-else-if="affiches.length === 0" class="discret">Aucun cours à afficher.</p>

    <section v-for="c in affiches" :key="c.id" class="carte bloc">
      <div class="entete-bloc">
        <h2>{{ c.niveau }} · {{ formatCreneau(c) }}</h2>
        <span :class="c.nombreInscrits >= c.capacite ? 'erreur' : 'discret'">
          {{ c.salle }} · {{ c.nombreInscrits }} / {{ c.capacite }}
        </span>
      </div>

      <p v-if="c.inscrits.length === 0" class="discret">Aucun inscrit.</p>
      <table v-else>
        <thead><tr><th>Enfant</th><th>Établissement</th><th>Parent</th></tr></thead>
        <tbody>
          <tr v-for="e in c.inscrits" :key="e.id">
            <td>{{ e.prenom }} {{ e.nom }}</td>
            <td>{{ e.etablissement ?? '—' }}</td>
            <td>{{ e.parent }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>
