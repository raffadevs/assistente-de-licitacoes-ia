import api from './api';
import type { Documento } from '../types/api';

export async function listarDocumentos(): Promise<Documento[]> {
  const { data } = await api.get<Documento[]>('/api/documentos');
  return data;
}

export async function buscarDocumento(id: number): Promise<Documento> {
  const { data } = await api.get<Documento>(`/api/documentos/${id}`);
  return data;
}

export async function enviarDocumento(arquivo: File, titulo: string, orgao: string): Promise<Documento> {
  const form = new FormData();
  form.append('arquivo', arquivo);
  form.append('titulo', titulo);
  if (orgao) form.append('orgao', orgao);

  const { data } = await api.post<Documento>('/api/documentos', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return data;
}
