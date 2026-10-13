<!-- Formulaire d'un enfant, réutilisé pour l'ajout ET la modification -->
<script setup lang="ts">
import type { EnfantRequest, Niveau } from '~/types/api'

// Ce que la page parente donne au composant
const props = defineProps<{
  initial?: EnfantRequest     // valeurs de départ (modification) ou rien (ajout)
  libelleBouton: string       // "Ajouter" ou "Enregistrer"
  chargement?: boolean
}>()

// Ce que le composant renvoie à la page parente
const emit = defineEmits<{
  valider: [donnees: EnfantRequest]
}>()

// La liste des niveaux pour la liste déroulante
const api = useApi()
const { data: niveaux } = await useAsyncData('niveaux', () => api<Niveau[]>('/parent/niveaux'))

// Les champs du formulaire, pré-remplis si on modifie
const form = reactive<EnfantRequest>({
  nom: props.initial?.nom ?? '',
  prenom: props.initial?.prenom ?? '',
  dateNaissance: props.initial?.dateNaissance ?? '',
  etablissement: props.initial?.etablissement ?? '',
  niveauId: props.initial?.niveauId ?? null,
})

function valider() {
  // un établissement vide est envoyé comme null
  emit('valider', { ...form, etablissement: form.etablissement || null })
}
</script>

<template>
  <form @submit.prevent="valider">
    <div class="deux-colonnes">
      <label>
        Prénom
        <input v-model="form.prenom" required maxlength="50" />
      </label>
      <label>
        Nom
        <input v-model="form.nom" required maxlength="50" />
      </label>
    </div>

    <div class="deux-colonnes">
      <label>
        Date de naissance
        <input v-model="form.dateNaissance" type="date" required />
      </label>
      <label>
        Niveau
        <select v-model.number="form.niveauId" required>
          <option :value="null" disabled>Choisir un niveau</option>
          <option v-for="n in niveaux" :key="n.id" :value="n.id">
            {{ n.libelle }} ({{ formatEuros(n.montant) }})
          </option>
        </select>
      </label>
    </div>

    <label>
      Établissement fréquenté
      <input v-model="form.etablissement" maxlength="100" />
    </label>

    <button type="submit" :disabled="chargement">{{ libelleBouton }}</button>
  </form>
</template>
