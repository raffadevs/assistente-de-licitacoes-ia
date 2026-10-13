<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router';
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="border-b border-slate-200 bg-white">
      <div class="max-w-4xl mx-auto px-5 py-3">
        <RouterLink to="/" class="font-bold text-slate-900">assistente-de-licitacoes-ia</RouterLink>
        <span class="text-sm text-slate-500 ml-2">RAG com citação de página</span>
      </div>
    </header>

    <main class="flex-1">
      <RouterView />
    </main>
  </div>
</template>
