<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { enviarDocumento, listarDocumentos } from '../services/documentos';
import type { Documento } from '../types/api';
import StatusBadge from '../components/StatusBadge.vue';

const documentos = ref<Documento[]>([]);
const carregando = ref(true);
const enviando = ref(false);
const erro = ref('');

const form = reactive({ titulo: '', orgao: '', arquivo: null as File | null });

async function carregar() {
  carregando.value = true;
  documentos.value = await listarDocumentos();
  carregando.value = false;
}

function aoSelecionarArquivo(evento: Event) {
  const input = evento.target as HTMLInputElement;
  form.arquivo = input.files?.[0] ?? null;
  if (form.arquivo && !form.titulo) {
    form.titulo = form.arquivo.name.replace(/\.pdf$/i, '');
  }
}

async function enviar() {
  if (!form.arquivo) return;
  erro.value = '';
  enviando.value = true;
  try {
    const documento = await enviarDocumento(form.arquivo, form.titulo.trim(), form.orgao.trim());
    documentos.value.unshift(documento);
    form.titulo = '';
    form.orgao = '';
    form.arquivo = null;
  } catch {
    erro.value = 'Não foi possível enviar o edital.';
  } finally {
    enviando.value = false;
  }
}

onMounted(carregar);
</script>

<template>
  <div class="max-w-2xl mx-auto px-5 py-8">
    <h1 class="text-lg font-bold text-slate-900">Editais</h1>
    <p class="text-sm text-slate-500 mt-1">Envie um PDF para extrair, indexar e poder perguntar sobre ele.</p>

    <form class="mt-4 bg-white border border-slate-200 rounded-lg p-4 flex flex-col gap-3" @submit.prevent="enviar">
      <input type="file" accept="application/pdf" required @change="aoSelecionarArquivo"
             class="text-sm" />
      <div class="grid grid-cols-2 gap-3">
        <input v-model="form.titulo" type="text" placeholder="Título" required
               class="border border-slate-300 rounded-md px-3 py-2 text-sm" />
        <input v-model="form.orgao" type="text" placeholder="Órgão (opcional)"
               class="border border-slate-300 rounded-md px-3 py-2 text-sm" />
      </div>
      <button type="submit" :disabled="enviando || !form.arquivo"
              class="self-start bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold rounded-md px-4 py-2 text-sm">
        {{ enviando ? 'Enviando...' : 'Enviar edital' }}
      </button>
      <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>
    </form>

    <div v-if="carregando" class="text-sm text-slate-500 mt-6">Carregando...</div>
    <ul v-else class="mt-6 flex flex-col gap-2">
      <li v-for="documento in documentos" :key="documento.id">
        <RouterLink :to="`/documentos/${documento.id}`"
                    class="block bg-white border border-slate-200 rounded-lg p-3 hover:border-indigo-400">
          <div class="flex items-center justify-between">
            <span class="font-medium text-slate-900">{{ documento.titulo }}</span>
            <StatusBadge :status="documento.statusProcessamento" />
          </div>
          <p class="text-xs text-slate-500 mt-1">
            {{ documento.orgao || 'sem órgão informado' }}
            <span v-if="documento.paginas"> · {{ documento.paginas }} páginas</span>
          </p>
        </RouterLink>
      </li>
      <li v-if="documentos.length === 0" class="text-sm text-slate-500">Nenhum edital enviado ainda.</li>
    </ul>
  </div>
</template>
