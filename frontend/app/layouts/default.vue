<!-- Cadre commun de toutes les pages (sauf le login) : en-tête + menu -->
<script setup lang="ts">
const { utilisateur, logout } = useAuth()
</script>

<template>
  <div>
    <header class="entete">
      <NuxtLink to="/" class="logo">Cours de soutien</NuxtLink>

      <nav v-if="utilisateur?.role === 'PARENT'" class="menu">
        <NuxtLink to="/parent">Tableau de bord</NuxtLink>
        <NuxtLink to="/parent/solde">Mon solde</NuxtLink>
      </nav>
      <nav v-else-if="utilisateur?.role === 'GESTIONNAIRE'" class="menu">
        <NuxtLink to="/gestion">Accueil</NuxtLink>
      </nav>

      <div class="compte">
        <span>{{ utilisateur?.prenom }} {{ utilisateur?.nom }}</span>
        <button class="secondaire" @click="logout()">Se déconnecter</button>
      </div>
    </header>

    <main class="contenu">
      <!-- La page courante s'affiche ici -->
      <slot />
    </main>
  </div>
</template>
