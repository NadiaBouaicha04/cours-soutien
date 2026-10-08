<!-- Gestion des salles : liste, création, modification, suppression -->
<script setup lang="ts">
import type { Salle } from '~/types/api'

const api = useApi()
const { chargement, erreur, succes, executer } = useAction()
const { data: salles, refresh } = await useAsyncData('salles', () => api<Salle[]>('/gestion/salles'))

// null = création ; un id = modification de cette salle
const enEdition = ref<number | null>(null)
const form = reactive({ nom: '', capacite: null as number | null })

function editer(s: Salle) {
  enEdition.value = s.id
  form.nom = s.nom
  form.capacite = s.capacite
}

function annuler() {
  enEdition.value = null
  form.nom = ''
  form.capacite = null
}

async function enregistrer() {
  const ok = enEdition.value === null
    ? await executer('Salle créée.', () => api('/gestion/salles', { method: 'POST', body: form }))
    : await executer('Salle modifiée.', () => api(`/gestion/salles/${enEdition.value}`, { method: 'PUT', body: form }))
  if (ok) { annuler(); await refresh() }
}

async function supprimer(s: Salle) {
  if (!confirm(`Supprimer ${s.nom} ?`)) return
  // refusé (409) si un cours utilise la salle
  if (await executer('Salle supprimée.', () => api(`/gestion/salles/${s.id}`, { method: 'DELETE' }))) await refresh()
}
</script>

<template>
  <div>
    <h1>Salles</h1>
    <Messages :succes="succes" :erreur="erreur" />

    <section class="carte bloc">
      <h2>{{ enEdition === null ? 'Nouvelle salle' : 'Modifier la salle' }}</h2>
      <form @submit.prevent="enregistrer">
        <div class="deux-colonnes">
          <label>Nom <input v-model="form.nom" required maxlength="50" /></label>
          <label>Capacité <input v-model.number="form.capacite" type="number" min="1" required /></label>
        </div>
        <button type="submit" :disabled="chargement">{{ enEdition === null ? 'Créer' : 'Enregistrer' }}</button>
        <button v-if="enEdition !== null" type="button" class="secondaire" @click="annuler">Annuler</button>
      </form>
    </section>

    <div class="carte">
      <table>
        <thead><tr><th>Nom</th><th>Capacité</th><th></th></tr></thead>
        <tbody>
          <tr v-for="s in salles" :key="s.id">
            <td>{{ s.nom }}</td>
            <td>{{ s.capacite }} places</td>
            <td class="droite actions">
              <button class="secondaire" @click="editer(s)">Modifier</button>
              <button class="danger" @click="supprimer(s)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
