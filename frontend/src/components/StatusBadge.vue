<script setup lang="ts">
import type { StatusProcessamento } from '../types/api';

defineProps<{ status: StatusProcessamento }>();

const CLASSES: Record<StatusProcessamento, string> = {
  PENDENTE: 'bg-slate-100 text-slate-600',
  PROCESSANDO: 'bg-amber-100 text-amber-700',
  CONCLUIDO: 'bg-green-100 text-green-700',
  FALHOU: 'bg-red-100 text-red-700',
};
</script>

<template>
  <span class="text-xs font-semibold px-2 py-0.5 rounded-full" :class="CLASSES[status]">
    {{ status.toLowerCase() }}
  </span>
</template>
