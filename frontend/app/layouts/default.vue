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
        <NuxtLink to="/gestion/utilisateurs">Utilisateurs</NuxtLink>
        <NuxtLink to="/gestion/cours">Cours</NuxtLink>
        <NuxtLink to="/gestion/salles">Salles</NuxtLink>
        <NuxtLink to="/gestion/niveaux">Niveaux</NuxtLink>
        <NuxtLink to="/gestion/recap">Récap</NuxtLink>
      </nav>

      <div class="compte">
        <span>{{ utilisateur?.prenom }} {{ utilisateur?.nom }}</span>
        <button class="secondaire" @click="logout()">Se déconnecter</button>
      </div>
    </header>

    <main class="contenu">
      <slot />
    </main>
  </div>
</template>
