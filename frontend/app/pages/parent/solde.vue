<!-- Consultation du solde et de la liste des paiements -->
<script setup lang="ts">
import type { Solde } from '~/types/api'

const api = useApi()
const { data: solde, error } = await useAsyncData('solde', () => api<Solde>('/parent/solde'))
</script>

<template>
  <div>
    <h1>Mon solde</h1>

    <p v-if="error" class="erreur">{{ messageErreur(error) }}</p>

    <template v-else-if="solde">
      <section class="carte bloc">
        <div class="chiffres">
          <div><span class="discret">Total dû</span><strong>{{ formatEuros(solde.totalDu) }}</strong></div>
          <div><span class="discret">Déjà payé</span><strong>{{ formatEuros(solde.totalPaye) }}</strong></div>
          <div><span class="discret">Reste à payer</span><strong>{{ formatEuros(solde.soldeRestant) }}</strong></div>
        </div>
        <p class="discret">Les paiements se font auprès de l'association ; ils apparaissent ici une fois enregistrés.</p>
      </section>

      <section class="carte bloc">
        <h2>Paiements reçus</h2>
        <p v-if="solde.mouvements.length === 0" class="discret">Aucun paiement enregistré.</p>
        <table v-else>
          <thead>
            <tr><th>Date</th><th>Libellé</th><th class="droite">Montant</th></tr>
          </thead>
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
  </div>
</template>
