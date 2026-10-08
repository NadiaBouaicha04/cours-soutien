<!-- Gestion des niveaux : liste, création, modification, suppression -->
<script setup lang="ts">
import type { Niveau } from '~/types/api'

const api = useApi()
const { chargement, erreur, succes, executer } = useAction()
const { data: niveaux, refresh } = await useAsyncData('niveaux-gestion', () => api<Niveau[]>('/gestion/niveaux'))

const enEdition = ref<number | null>(null)
const form = reactive({ libelle: '', ordre: null as number | null, montant: null as number | null })

function editer(n: Niveau) {
  enEdition.value = n.id
  Object.assign(form, { libelle: n.libelle, ordre: n.ordre, montant: n.montant })
}

function annuler() {
  enEdition.value = null
  Object.assign(form, { libelle: '', ordre: null, montant: null })
}

async function enregistrer() {
  // modifier le tarif change automatiquement le solde des parents (solde calculé)
  const ok = enEdition.value === null
    ? await executer('Niveau créé.', () => api('/gestion/niveaux', { method: 'POST', body: form }))
    : await executer('Niveau modifié.', () => api(`/gestion/niveaux/${enEdition.value}`, { method: 'PUT', body: form }))
  if (ok) { annuler(); await refresh() }
}

async function supprimer(n: Niveau) {
  if (!confirm(`Supprimer le niveau ${n.libelle} ?`)) return
  // refusé (409) si un cours ou un enfant utilise ce niveau
  if (await executer('Niveau supprimé.', () => api(`/gestion/niveaux/${n.id}`, { method: 'DELETE' }))) await refresh()
}
</script>

<template>
  <div>
    <h1>Niveaux</h1>
    <Messages :succes="succes" :erreur="erreur" />

    <section class="carte bloc">
      <h2>{{ enEdition === null ? 'Nouveau niveau' : 'Modifier le niveau' }}</h2>
      <form @submit.prevent="enregistrer">
        <div class="trois-colonnes">
          <label>Libellé <input v-model="form.libelle" required maxlength="20" placeholder="ex. 3e" /></label>
          <label>Ordre <input v-model.number="form.ordre" type="number" min="1" required /></label>
          <label>Tarif (€) <input v-model.number="form.montant" type="number" min="0.01" step="0.01" required /></label>
        </div>
        <button type="submit" :disabled="chargement">{{ enEdition === null ? 'Créer' : 'Enregistrer' }}</button>
        <button v-if="enEdition !== null" type="button" class="secondaire" @click="annuler">Annuler</button>
      </form>
    </section>

    <div class="carte">
      <table>
        <thead><tr><th>Ordre</th><th>Libellé</th><th>Tarif</th><th></th></tr></thead>
        <tbody>
          <tr v-for="n in niveaux" :key="n.id">
            <td>{{ n.ordre }}</td>
            <td>{{ n.libelle }}</td>
            <td>{{ formatEuros(n.montant) }}</td>
            <td class="droite actions">
              <button class="secondaire" @click="editer(n)">Modifier</button>
              <button class="danger" @click="supprimer(n)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
