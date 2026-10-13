<!-- Affiche le message de succès (vert) ou d'erreur (rouge) d'une action -->
<script setup lang="ts">
defineProps<{ succes?: string; erreur?: string }>()
</script>

<template>
  <p v-if="succes" class="succes">{{ succes }}</p>
  <p v-if="erreur" class="erreur">{{ erreur }}</p>
</template>
