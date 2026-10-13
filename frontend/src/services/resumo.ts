import api from './api';
import type { Resumo } from '../types/api';

export async function gerarResumo(documentoId: number): Promise<Resumo> {
  const { data } = await api.post<Resumo>(`/api/documentos/${documentoId}/resumo`);
  return data;
}

export async function buscarResumo(documentoId: number): Promise<Resumo | null> {
  try {
    const { data } = await api.get<Resumo>(`/api/documentos/${documentoId}/resumo`);
    return data;
  } catch (e: any) {
    if (e?.response?.status === 404) return null;
    throw e;
  }
}
