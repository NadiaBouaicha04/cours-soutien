<!-- Fiche d'un enfant : inscription à un cours, modification, suppression -->
<script setup lang="ts">
import type { CoursDisponible, Enfant, EnfantRequest } from '~/types/api'

const api = useApi()
const route = useRoute()
const id = Number(route.params.id)   // l'id vient de l'URL : /parent/enfants/1

const erreur = ref('')
const succes = ref('')
const chargement = ref(false)

// L'enfant : on récupère la liste des enfants du parent et on garde celui de l'URL
const { data: enfants, refresh: rechargerEnfants } = await useAsyncData('enfants',
  () => api<Enfant[]>('/parent/enfants'))
const enfant = computed(() => enfants.value?.find(e => e.id === id))

// Les cours disponibles pour SON niveau (les cours complets ne sont pas renvoyés)
const { data: coursDisponibles, refresh: rechargerCours } = await useAsyncData(`cours-disponibles-${id}`,
  () => enfant.value
    ? api<CoursDisponible[]>('/parent/cours-disponibles', { query: { niveauId: enfant.value.niveauId } })
    : Promise.resolve([]),
  { watch: [enfant] })

// On ne propose pas le cours qu'il suit déjà
const coursProposes = computed(() =>
  (coursDisponibles.value ?? []).filter(c => !enfant.value?.cours.some(actuel => actuel.id === c.id)))

const coursChoisi = ref<number | null>(null)

// Exécute une action, affiche le succès ou l'erreur, puis recharge les données
async function action(message: string, appel: () => Promise<unknown>) {
  erreur.value = ''
  succes.value = ''
  chargement.value = true
  try {
    await appel()
    succes.value = message
    await Promise.all([rechargerEnfants(), rechargerCours()])
  } catch (e) {
    erreur.value = messageErreur(e)
  } finally {
    chargement.value = false
  }
}

function inscrire() {
  if (!coursChoisi.value) return
  // PUT /api/parent/enfants/{id}/cours
  return action('Inscription enregistrée.', () =>
    api(`/parent/enfants/${id}/cours`, { method: 'PUT', body: { coursId: coursChoisi.value } }))
    .then(() => { coursChoisi.value = null })
}

function desinscrire(coursId: number) {
  // DELETE /api/parent/enfants/{id}/cours/{coursId}
  return action('Désinscription enregistrée.', () =>
    api(`/parent/enfants/${id}/cours/${coursId}`, { method: 'DELETE' }))
}

function modifier(donnees: EnfantRequest) {
  // PUT /api/parent/enfants/{id}
  return action('Informations enregistrées.', () =>
    api(`/parent/enfants/${id}`, { method: 'PUT', body: donnees }))
}

async function supprimer() {
  if (!confirm(`Supprimer ${enfant.value?.prenom} ?`)) return
  try {
    // DELETE /api/parent/enfants/{id}
    await api(`/parent/enfants/${id}`, { method: 'DELETE' })
    await navigateTo('/parent')
  } catch (e) {
    erreur.value = messageErreur(e)
  }
}
</script>

<template>
  <div>
    <NuxtLink to="/parent">← Tableau de bord</NuxtLink>

    <p v-if="!enfant" class="erreur">Enfant introuvable.</p>

    <template v-else>
      <h1>{{ enfant.prenom }} {{ enfant.nom }}</h1>
      <p class="discret">{{ enfant.niveau }}<span v-if="enfant.etablissement"> · {{ enfant.etablissement }}</span></p>

      <p v-if="succes" class="succes">{{ succes }}</p>
      <p v-if="erreur" class="erreur">{{ erreur }}</p>

      <!-- Cours actuel -->
      <section class="carte bloc">
        <h2>Cours suivi</h2>
        <p v-if="enfant.cours.length === 0" class="avertissement">Pas encore inscrit à un cours.</p>
        <div v-for="c in enfant.cours" :key="c.id" class="ligne">
          <span>{{ formatCreneau(c) }}, {{ c.salle }}</span>
          <button class="secondaire" :disabled="chargement" @click="desinscrire(c.id)">Désinscrire</button>
        </div>
      </section>

      <!-- Inscription -->
      <section class="carte bloc">
        <h2>{{ enfant.cours.length === 0 ? 'Inscrire à un cours' : 'Changer de cours' }}</h2>
        <p v-if="enfant.cours.length > 0" class="discret">Le nouveau cours remplacera le cours actuel.</p>

        <p v-if="coursProposes.length === 0" class="discret">Aucun autre cours disponible pour ce niveau.</p>
        <form v-else @submit.prevent="inscrire">
          <label>
            Créneau
            <select v-model.number="coursChoisi" required>
              <option :value="null" disabled>Choisir un créneau</option>
              <option v-for="c in coursProposes" :key="c.id" :value="c.id">
                {{ formatCreneau(c) }}, {{ c.salle }} ({{ c.placesRestantes }} places)
              </option>
            </select>
          </label>
          <button type="submit" :disabled="chargement || !coursChoisi">Inscrire</button>
        </form>
      </section>

      <!-- Modification -->
      <section class="carte bloc">
        <h2>Modifier les informations</h2>
        <EnfantForm
          :key="enfant.id"
          :initial="{ nom: enfant.nom, prenom: enfant.prenom, dateNaissance: enfant.dateNaissance,
                      etablissement: enfant.etablissement, niveauId: enfant.niveauId }"
          libelle-bouton="Enregistrer"
          :chargement="chargement"
          @valider="modifier"
        />
      </section>

      <button class="danger" @click="supprimer">Supprimer cet enfant</button>
    </template>
  </div>
</template>
