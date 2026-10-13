<!-- Fiche d'un utilisateur : modification, suppression, paiements -->
<script setup lang="ts">
import type { MouvementRequest, Solde, Utilisateur, UtilisateurRequest } from '~/types/api'

const api = useApi()
const id = Number(useRoute().params.id)
const { chargement, erreur, succes, executer } = useAction()

// GET /api/gestion/utilisateurs/{id}
const { data: utilisateur, refresh: rechargerUtilisateur } = await useAsyncData(`utilisateur-${id}`,
  () => api<Utilisateur>(`/gestion/utilisateurs/${id}`))

// GET /api/gestion/utilisateurs/{id}/mouvements : solde + liste des paiements
const { data: solde, refresh: rechargerSolde } = await useAsyncData(`mouvements-${id}`,
  () => api<Solde>(`/gestion/utilisateurs/${id}/mouvements`))

// ---------- Enregistrer un paiement ----------
const aujourdHui = new Date().toISOString().slice(0, 10)
const paiement = reactive<MouvementRequest>({ date: aujourdHui, montant: null, libelle: '' })

async function ajouterPaiement() {
  // POST /api/gestion/utilisateurs/{id}/mouvements
  const ok = await executer('Paiement enregistré.', () =>
    api(`/gestion/utilisateurs/${id}/mouvements`, {
      method: 'POST',
      body: { ...paiement, libelle: paiement.libelle || null },
    }))
  if (ok) {
    paiement.montant = null
    paiement.libelle = ''
    await rechargerSolde()
  }
}

// ---------- Modifier / supprimer le compte ----------
async function modifier(donnees: UtilisateurRequest) {
  // PUT /api/gestion/utilisateurs/{id}
  const ok = await executer('Compte modifié.', () =>
    api(`/gestion/utilisateurs/${id}`, { method: 'PUT', body: donnees }))
  if (ok) await rechargerUtilisateur()
}

async function supprimer() {
  if (!confirm('Supprimer ce compte ? Ses enfants seront aussi supprimés.')) return
  // DELETE /api/gestion/utilisateurs/{id} (refusé en 409 s'il a des paiements)
  const ok = await executer('Compte supprimé.', () =>
    api(`/gestion/utilisateurs/${id}`, { method: 'DELETE' }))
  if (ok) await navigateTo('/gestion/utilisateurs')
}
</script>

<template>
  <div>
    <NuxtLink to="/gestion/utilisateurs">← Utilisateurs</NuxtLink>

    <template v-if="utilisateur">
      <h1>{{ utilisateur.prenom }} {{ utilisateur.nom }}</h1>
      <p class="discret">{{ utilisateur.email }} · {{ utilisateur.role === 'PARENT' ? 'Parent' : 'Gestionnaire' }}</p>

      <Messages :succes="succes" :erreur="erreur" />

      <!-- Paiements : seulement pour un parent -->
      <template v-if="utilisateur.role === 'PARENT' && solde">
        <section class="carte bloc">
          <h2>Solde</h2>
          <div class="chiffres">
            <div><span class="discret">Total dû</span><strong>{{ formatEuros(solde.totalDu) }}</strong></div>
            <div><span class="discret">Déjà payé</span><strong>{{ formatEuros(solde.totalPaye) }}</strong></div>
            <div><span class="discret">Reste à payer</span><strong>{{ formatEuros(solde.soldeRestant) }}</strong></div>
          </div>
        </section>

        <section class="carte bloc">
          <h2>Enregistrer un paiement reçu</h2>
          <form @submit.prevent="ajouterPaiement">
            <div class="trois-colonnes">
              <label>Date <input v-model="paiement.date" type="date" required /></label>
              <label>Montant (€) <input v-model.number="paiement.montant" type="number" min="0.01" step="0.01" required /></label>
              <label>Libellé <input v-model="paiement.libelle" maxlength="100" placeholder="ex. Chèque n°1234" /></label>
            </div>
            <button type="submit" :disabled="chargement">Enregistrer le paiement</button>
          </form>
        </section>

        <section class="carte bloc">
          <h2>Mouvements</h2>
          <p v-if="solde.mouvements.length === 0" class="discret">Aucun paiement enregistré.</p>
          <table v-else>
            <thead><tr><th>Date</th><th>Libellé</th><th class="droite">Montant</th></tr></thead>
            <tbody>
              <tr v-for="m in solde.mouvements" :key="m.id">
                <td>{{ formatDate(m.date) }}</td>
                <td>{{ m.libelle ?? '—' }}</td>
                <td class="droite">{{ formatEuros(m.montant) }}</td>
              </tr>
            </tbody>
          </table>
        </section>
      </template>

      <section class="carte bloc">
        <h2>Modifier le compte</h2>
        <UtilisateurForm
          :key="utilisateur.id"
          :initial="{ ...utilisateur, motDePasse: null }"
          modification
          libelle-bouton="Enregistrer"
          :chargement="chargement"
          @valider="modifier"
        />
      </section>

      <button class="danger" :disabled="chargement" @click="supprimer">Supprimer ce compte</button>
    </template>
  </div>
</template>
