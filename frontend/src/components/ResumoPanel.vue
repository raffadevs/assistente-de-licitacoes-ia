<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { buscarResumo, gerarResumo } from '../services/resumo';
import type { Resumo } from '../types/api';

const props = defineProps<{ documentoId: number }>();

const resumo = ref<Resumo | null>(null);
const carregando = ref(true);
const gerando = ref(false);

async function carregar() {
  carregando.value = true;
  resumo.value = await buscarResumo(props.documentoId);
  carregando.value = false;
}

async function gerar() {
  gerando.value = true;
  try {
    resumo.value = await gerarResumo(props.documentoId);
  } finally {
    gerando.value = false;
  }
}

onMounted(carregar);
</script>

<template>
  <div class="flex flex-col gap-3">
    <button type="button" :disabled="gerando"
            class="self-start bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md px-4 py-2 text-sm"
            @click="gerar">
      {{ gerando ? 'Gerando...' : (resumo ? 'Gerar novamente' : 'Gerar resumo') }}
    </button>

    <div v-if="carregando" class="text-sm text-slate-500">Carregando...</div>

    <div v-else-if="resumo" class="bg-white border border-slate-200 rounded-lg p-4 flex flex-col gap-3 text-sm">
      <div>
        <p class="text-xs font-semibold text-slate-500">Objeto</p>
        <p class="text-slate-900">{{ resumo.objeto || '—' }}</p>
      </div>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <p class="text-xs font-semibold text-slate-500">Valor estimado</p>
          <p class="text-slate-900">{{ resumo.valorEstimado ? `R$ ${resumo.valorEstimado}` : '—' }}</p>
        </div>
        <div>
          <p class="text-xs font-semibold text-slate-500">Modalidade</p>
          <p class="text-slate-900">{{ resumo.modalidade || '—' }}</p>
        </div>
      </div>
      <div v-if="resumo.prazos.length">
        <p class="text-xs font-semibold text-slate-500 mb-1">Prazos</p>
        <ul class="list-disc list-inside text-slate-700">
          <li v-for="(prazo, i) in resumo.prazos" :key="i">{{ prazo }}</li>
        </ul>
      </div>
      <div v-if="resumo.documentosExigidos.length">
        <p class="text-xs font-semibold text-slate-500 mb-1">Documentos de habilitação</p>
        <ul class="list-disc list-inside text-slate-700">
          <li v-for="(doc, i) in resumo.documentosExigidos" :key="i">{{ doc }}</li>
        </ul>
      </div>
    </div>

    <p v-else class="text-sm text-slate-400">Nenhum resumo gerado ainda.</p>
  </div>
</template>
