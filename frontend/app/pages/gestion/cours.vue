<!-- Gestion des cours : liste avec remplissage, création, modification, suppression -->
<script setup lang="ts">
import { JOURS, type CoursGestion, type CoursRequest, type Niveau, type Salle } from '~/types/api'

const api = useApi()
const { chargement, erreur, succes, executer } = useAction()

const { data: cours, refresh } = await useAsyncData('cours-gestion', () => api<CoursGestion[]>('/gestion/cours'))
const { data: niveaux } = await useAsyncData('niveaux-gestion', () => api<Niveau[]>('/gestion/niveaux'))
const { data: salles } = await useAsyncData('salles', () => api<Salle[]>('/gestion/salles'))

const enEdition = ref<number | null>(null)
const vide: CoursRequest = { jour: null, heureDebut: '', heureFin: '', niveauId: null, salleId: null }
const form = reactive<CoursRequest>({ ...vide })

function editer(c: CoursGestion) {
  enEdition.value = c.id
  Object.assign(form, {
    jour: c.jour,
    heureDebut: c.heureDebut.slice(0, 5),   // "10:00:00" -> "10:00" pour le champ heure
    heureFin: c.heureFin.slice(0, 5),
    niveauId: c.niveauId,
    salleId: c.salleId,
  })
}

function annuler() {
  enEdition.value = null
  Object.assign(form, vide)
}

async function enregistrer() {
  // le back-end vérifie : fin après début, pas de chevauchement dans la salle,
  // pas de changement de niveau ni de salle trop petite si le cours a des inscrits
  const ok = enEdition.value === null
    ? await executer('Cours créé.', () => api('/gestion/cours', { method: 'POST', body: form }))
    : await executer('Cours modifié.', () => api(`/gestion/cours/${enEdition.value}`, { method: 'PUT', body: form }))
  if (ok) { annuler(); await refresh() }
}

async function supprimer(c: CoursGestion) {
  if (!confirm(`Supprimer le cours de ${c.niveau}, ${formatCreneau(c)} ?`)) return
  // refusé (409) si le cours a des inscrits
  if (await executer('Cours supprimé.', () => api(`/gestion/cours/${c.id}`, { method: 'DELETE' }))) await refresh()
}
</script>

<template>
  <div>
    <h1>Cours</h1>
    <Messages :succes="succes" :erreur="erreur" />

    <section class="carte bloc">
      <h2>{{ enEdition === null ? 'Nouveau cours' : 'Modifier le cours' }}</h2>
      <form @submit.prevent="enregistrer">
        <div class="trois-colonnes">
          <label>
            Jour
            <select v-model="form.jour" required>
              <option :value="null" disabled>Choisir</option>
              <option v-for="j in JOURS" :key="j" :value="j">{{ formatJour(j) }}</option>
            </select>
          </label>
          <label>Début <input v-model="form.heureDebut" type="time" required /></label>
          <label>Fin <input v-model="form.heureFin" type="time" required /></label>
        </div>
        <div class="deux-colonnes">
          <label>
            Niveau
            <select v-model.number="form.niveauId" required>
              <option :value="null" disabled>Choisir</option>
              <option v-for="n in niveaux" :key="n.id" :value="n.id">{{ n.libelle }}</option>
            </select>
          </label>
          <label>
            Salle
            <select v-model.number="form.salleId" required>
              <option :value="null" disabled>Choisir</option>
              <option v-for="s in salles" :key="s.id" :value="s.id">{{ s.nom }} ({{ s.capacite }} places)</option>
            </select>
          </label>
        </div>
        <button type="submit" :disabled="chargement">{{ enEdition === null ? 'Créer' : 'Enregistrer' }}</button>
        <button v-if="enEdition !== null" type="button" class="secondaire" @click="annuler">Annuler</button>
      </form>
    </section>

    <div class="carte">
      <table>
        <thead><tr><th>Niveau</th><th>Créneau</th><th>Salle</th><th>Inscrits</th><th></th></tr></thead>
        <tbody>
          <tr v-for="c in cours" :key="c.id">
            <td>{{ c.niveau }}</td>
            <td>{{ formatCreneau(c) }}</td>
            <td>{{ c.salle }}</td>
            <td :class="{ erreur: c.nombreInscrits >= c.capacite }">
              {{ c.nombreInscrits }} / {{ c.capacite }}
            </td>
            <td class="droite actions">
              <button class="secondaire" @click="editer(c)">Modifier</button>
              <button class="danger" @click="supprimer(c)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
