<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { buscarDocumento } from '../services/documentos';
import type { Documento } from '../types/api';
import StatusBadge from '../components/StatusBadge.vue';
import ChatPanel from '../components/ChatPanel.vue';
import ResumoPanel from '../components/ResumoPanel.vue';
import AvaliacaoPanel from '../components/AvaliacaoPanel.vue';

const props = defineProps<{ id: number }>();

const documento = ref<Documento | null>(null);
const carregando = ref(true);
const abaAtiva = ref<'chat' | 'resumo' | 'avaliacao'>('chat');

let intervalo: ReturnType<typeof setInterval> | null = null;

const pronto = computed(() => documento.value?.statusProcessamento === 'CONCLUIDO');

async function carregar() {
  documento.value = await buscarDocumento(props.id);
  carregando.value = false;

  if (documento.value.statusProcessamento === 'CONCLUIDO' || documento.value.statusProcessamento === 'FALHOU') {
    if (intervalo) {
      clearInterval(intervalo);
      intervalo = null;
    }
  }
}

onMounted(() => {
  carregar();
  intervalo = setInterval(carregar, 2000);
});

onUnmounted(() => {
  if (intervalo) clearInterval(intervalo);
});
</script>

<template>
  <div class="max-w-2xl mx-auto px-5 py-8">
    <RouterLink to="/" class="text-sm text-slate-500">&larr; todos os editais</RouterLink>

    <div v-if="carregando" class="text-sm text-slate-500 mt-4">Carregando...</div>

    <template v-else-if="documento">
      <div class="flex items-center justify-between mt-2">
        <h1 class="text-xl font-bold text-slate-900">{{ documento.titulo }}</h1>
        <StatusBadge :status="documento.statusProcessamento" />
      </div>
      <p class="text-sm text-slate-500">{{ documento.orgao || 'sem órgão informado' }}</p>

      <div v-if="!pronto" class="mt-6 bg-amber-50 border border-amber-200 rounded-lg p-4 text-sm text-amber-800">
        <template v-if="documento.statusProcessamento === 'FALHOU'">
          O processamento falhou: {{ documento.observacoes }}
        </template>
        <template v-else>
          Processando o edital (extração, divisão em trechos e indexação)... esta página atualiza sozinha.
        </template>
      </div>

      <template v-else>
        <div class="mt-6 flex gap-4 border-b border-slate-200 text-sm">
          <button type="button" class="pb-2 px-1" :class="abaAtiva === 'chat' ? 'border-b-2 border-indigo-600 text-indigo-600 font-semibold' : 'text-slate-500'"
                  @click="abaAtiva = 'chat'">Perguntas</button>
          <button type="button" class="pb-2 px-1" :class="abaAtiva === 'resumo' ? 'border-b-2 border-indigo-600 text-indigo-600 font-semibold' : 'text-slate-500'"
                  @click="abaAtiva = 'resumo'">Resumo</button>
          <button type="button" class="pb-2 px-1" :class="abaAtiva === 'avaliacao' ? 'border-b-2 border-indigo-600 text-indigo-600 font-semibold' : 'text-slate-500'"
                  @click="abaAtiva = 'avaliacao'">Avaliação</button>
        </div>

        <div class="mt-4">
          <ChatPanel v-if="abaAtiva === 'chat'" :documento-id="documento.id" />
          <ResumoPanel v-else-if="abaAtiva === 'resumo'" :documento-id="documento.id" />
          <AvaliacaoPanel v-else :documento-id="documento.id" />
        </div>
      </template>
    </template>
  </div>
</template>
