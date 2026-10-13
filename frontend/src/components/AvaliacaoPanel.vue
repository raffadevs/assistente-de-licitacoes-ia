<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { executarAvaliacoes, listarAvaliacoes } from '../services/avaliacoes';
import type { Avaliacao } from '../types/api';

const props = defineProps<{ documentoId: number }>();

const avaliacoes = ref<Avaliacao[]>([]);
const carregando = ref(true);
const executando = ref(false);

const form = reactive({ pergunta: '', respostaEsperada: '' });

async function carregar() {
  carregando.value = true;
  avaliacoes.value = await listarAvaliacoes(props.documentoId);
  carregando.value = false;
}

async function executar() {
  if (!form.pergunta.trim() || !form.respostaEsperada.trim()) return;
  executando.value = true;
  try {
    const resultado = await executarAvaliacoes(props.documentoId, [
      { pergunta: form.pergunta.trim(), respostaEsperada: form.respostaEsperada.trim() },
    ]);
    avaliacoes.value = [...resultado, ...avaliacoes.value];
    form.pergunta = '';
    form.respostaEsperada = '';
  } finally {
    executando.value = false;
  }
}

onMounted(carregar);
</script>

<template>
  <div class="flex flex-col gap-3">
    <form class="bg-white border border-slate-200 rounded-lg p-3 flex flex-col gap-2" @submit.prevent="executar">
      <label class="text-xs font-semibold text-slate-500">Novo caso de teste</label>
      <input v-model="form.pergunta" type="text" placeholder="Pergunta" required
             class="border border-slate-300 rounded-md px-3 py-2 text-sm" />
      <input v-model="form.respostaEsperada" type="text" placeholder="Trecho esperado na resposta" required
             class="border border-slate-300 rounded-md px-3 py-2 text-sm" />
      <button type="submit" :disabled="executando"
              class="self-start bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md px-4 py-2 text-sm">
        {{ executando ? 'Executando...' : 'Executar caso' }}
      </button>
    </form>

    <div v-if="carregando" class="text-sm text-slate-500">Carregando...</div>
    <ul v-else class="flex flex-col gap-2">
      <li v-for="avaliacao in avaliacoes" :key="avaliacao.id"
          class="bg-white border rounded-lg p-3 text-sm"
          :class="avaliacao.passou ? 'border-green-200' : 'border-red-200'">
        <div class="flex items-center justify-between">
          <p class="font-medium text-slate-900">{{ avaliacao.pergunta }}</p>
          <span class="text-xs font-semibold px-2 py-0.5 rounded-full"
                :class="avaliacao.passou ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'">
            {{ avaliacao.passou ? 'passou' : 'falhou' }}
          </span>
        </div>
        <p class="text-xs text-slate-500 mt-1">esperado: {{ avaliacao.respostaEsperada }}</p>
        <p class="text-xs text-slate-600 mt-1">obtido: {{ avaliacao.respostaObtida }}</p>
      </li>
      <li v-if="avaliacoes.length === 0" class="text-sm text-slate-400">Nenhuma avaliação executada ainda.</li>
    </ul>
  </div>
</template>
