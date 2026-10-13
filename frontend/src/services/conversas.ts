import api from './api';
import type { Mensagem, RespostaChat } from '../types/api';

export async function criarConversa(documentoId: number): Promise<number> {
  const { data } = await api.post<{ conversaId: number }>(`/api/documentos/${documentoId}/conversas`);
  return data.conversaId;
}

export async function perguntar(conversaId: number, pergunta: string): Promise<RespostaChat> {
  const { data } = await api.post<RespostaChat>(`/api/conversas/${conversaId}/perguntas`, { pergunta });
  return data;
}

export async function listarMensagens(conversaId: number): Promise<Mensagem[]> {
  const { data } = await api.get<Mensagem[]>(`/api/conversas/${conversaId}/mensagens`);
  return data;
}
