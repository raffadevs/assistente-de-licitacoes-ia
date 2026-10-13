import api from './api';
import type { Avaliacao, CasoAvaliacao } from '../types/api';

export async function executarAvaliacoes(documentoId: number, casos: CasoAvaliacao[]): Promise<Avaliacao[]> {
  const { data } = await api.post<Avaliacao[]>(`/api/documentos/${documentoId}/avaliacoes`, { casos });
  return data;
}

export async function listarAvaliacoes(documentoId: number): Promise<Avaliacao[]> {
  const { data } = await api.get<Avaliacao[]>(`/api/documentos/${documentoId}/avaliacoes`);
  return data;
}
