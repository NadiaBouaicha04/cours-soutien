<!-- Liste des utilisateurs + création d'un compte -->
<script setup lang="ts">
import type { Utilisateur, UtilisateurRequest } from '~/types/api'

const api = useApi()
const { chargement, erreur, succes, executer } = useAction()

const { data: utilisateurs, refresh } = await useAsyncData('utilisateurs',
  () => api<Utilisateur[]>('/gestion/utilisateurs'))

// Tri : par nom puis prénom
const tries = computed(() => [...(utilisateurs.value ?? [])]
  .sort((a, b) => (a.nom + a.prenom).localeCompare(b.nom + b.prenom)))

const modes = { CHEQUE: 'Chèque', ESPECES: 'Espèces', VIREMENT: 'Virement' }

const formVisible = ref(false)
const formCle = ref(0)   // change de valeur pour vider le formulaire

async function creer(donnees: UtilisateurRequest) {
  // POST /api/gestion/utilisateurs
  const ok = await executer('Compte créé.', () =>
    api('/gestion/utilisateurs', { method: 'POST', body: donnees }))
  if (ok) {
    formVisible.value = false
    formCle.value++
    await refresh()
  }
}
</script>

<template>
  <div>
    <div class="entete-bloc">
      <h1>Utilisateurs</h1>
      <button @click="formVisible = !formVisible">{{ formVisible ? 'Fermer' : 'Nouveau compte' }}</button>
    </div>

    <Messages :succes="succes" :erreur="erreur" />

    <section v-if="formVisible" class="carte bloc">
      <h2>Nouveau compte</h2>
      <UtilisateurForm :key="formCle" libelle-bouton="Créer" :chargement="chargement" @valider="creer" />
    </section>

    <div class="carte">
      <table>
        <thead>
          <tr><th>Nom</th><th>E-mail</th><th>Rôle</th><th>Paiement</th><th></th></tr>
        </thead>
        <tbody>
          <tr v-for="u in tries" :key="u.id">
            <td>{{ u.prenom }} {{ u.nom }}</td>
            <td>{{ u.email }}</td>
            <td>{{ u.role === 'PARENT' ? 'Parent' : 'Gestionnaire' }}</td>
            <td>
              <span v-if="u.modePaiement">{{ modes[u.modePaiement] }}, {{ u.nombrePaiements }} fois</span>
              <span v-else class="discret">—</span>
            </td>
            <td class="droite"><NuxtLink :to="`/gestion/utilisateurs/${u.id}`">Ouvrir</NuxtLink></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
