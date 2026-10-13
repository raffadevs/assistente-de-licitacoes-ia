<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { criarConversa, perguntar } from '../services/conversas';
import type { Mensagem } from '../types/api';

const props = defineProps<{ documentoId: number }>();

const conversaId = ref<number | null>(null);
const mensagens = ref<Mensagem[]>([]);
const pergunta = ref('');
const enviando = ref(false);
const erro = ref('');

async function iniciar() {
  conversaId.value = await criarConversa(props.documentoId);
}

async function enviar() {
  if (!pergunta.value.trim() || !conversaId.value) return;
  erro.value = '';
  enviando.value = true;

  mensagens.value.push({
    id: Date.now(), papel: 'USUARIO', conteudo: pergunta.value, paginasCitadas: [], criadaEm: new Date().toISOString(),
  });
  const perguntaAtual = pergunta.value;
  pergunta.value = '';

  try {
    const resposta = await perguntar(conversaId.value, perguntaAtual);
    mensagens.value.push({
      id: Date.now() + 1, papel: 'ASSISTENTE', conteudo: resposta.resposta,
      paginasCitadas: resposta.paginasCitadas, criadaEm: new Date().toISOString(),
    });
  } catch {
    erro.value = 'Não foi possível obter uma resposta agora.';
  } finally {
    enviando.value = false;
  }
}

onMounted(iniciar);
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="flex flex-col gap-2 max-h-96 overflow-y-auto">
      <div v-for="mensagem in mensagens" :key="mensagem.id"
           class="rounded-lg p-3 text-sm max-w-[85%]"
           :class="mensagem.papel === 'USUARIO'
             ? 'bg-indigo-600 text-white self-end'
             : 'bg-white border border-slate-200 self-start'">
        <p>{{ mensagem.conteudo }}</p>
        <p v-if="mensagem.paginasCitadas.length" class="text-xs mt-1.5 opacity-75">
          páginas: {{ mensagem.paginasCitadas.join(', ') }}
        </p>
      </div>
      <p v-if="mensagens.length === 0" class="text-sm text-slate-400">
        Pergunte algo sobre este edital, por exemplo "qual o prazo para impugnação?".
      </p>
    </div>

    <form class="flex gap-2" @submit.prevent="enviar">
      <input v-model="pergunta" type="text" placeholder="Digite sua pergunta..." :disabled="!conversaId || enviando"
             class="flex-1 border border-slate-300 rounded-md px-3 py-2 text-sm" />
      <button type="submit" :disabled="!conversaId || enviando"
              class="bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md px-4 text-sm">
        {{ enviando ? '...' : 'Enviar' }}
      </button>
    </form>
    <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>
  </div>
</template>
