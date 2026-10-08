<!-- Formulaire d'un compte, réutilisé pour la création ET la modification -->
<script setup lang="ts">
import type { UtilisateurRequest } from '~/types/api'

const props = defineProps<{
  initial?: UtilisateurRequest
  modification?: boolean      // true : le mot de passe devient facultatif
  libelleBouton: string
  chargement?: boolean
}>()

const emit = defineEmits<{ valider: [donnees: UtilisateurRequest] }>()

const form = reactive<UtilisateurRequest>({
  nom: props.initial?.nom ?? '',
  prenom: props.initial?.prenom ?? '',
  email: props.initial?.email ?? '',
  motDePasse: '',
  role: props.initial?.role ?? 'PARENT',
  modePaiement: props.initial?.modePaiement ?? 'CHEQUE',
  nombrePaiements: props.initial?.nombrePaiements ?? 1,
})

const estParent = computed(() => form.role === 'PARENT')

function valider() {
  emit('valider', {
    ...form,
    motDePasse: form.motDePasse || null,
    // un gestionnaire ne paie pas : on n'envoie pas ces champs
    modePaiement: estParent.value ? form.modePaiement : null,
    nombrePaiements: estParent.value ? form.nombrePaiements : null,
  })
}
</script>

<template>
  <form @submit.prevent="valider">
    <div class="deux-colonnes">
      <label>Prénom <input v-model="form.prenom" required maxlength="50" /></label>
      <label>Nom <input v-model="form.nom" required maxlength="50" /></label>
    </div>

    <div class="deux-colonnes">
      <label>E-mail <input v-model="form.email" type="email" required maxlength="150" /></label>
      <label>
        Mot de passe
        <input v-model="form.motDePasse" type="password" minlength="6" :required="!modification"
               :placeholder="modification ? 'Laisser vide pour ne pas changer' : ''" />
      </label>
    </div>

    <label>
      Rôle
      <select v-model="form.role">
        <option value="PARENT">Parent</option>
        <option value="GESTIONNAIRE">Gestionnaire</option>
      </select>
    </label>

    <!-- Affiché seulement pour un parent -->
    <div v-if="estParent" class="deux-colonnes">
      <label>
        Mode de paiement
        <select v-model="form.modePaiement">
          <option value="CHEQUE">Chèque</option>
          <option value="ESPECES">Espèces</option>
          <option value="VIREMENT">Virement</option>
        </select>
      </label>
      <label>
        Nombre de versements
        <input v-model.number="form.nombrePaiements" type="number" min="1" max="6" required />
      </label>
    </div>

    <button type="submit" :disabled="chargement">{{ libelleBouton }}</button>
  </form>
</template>
