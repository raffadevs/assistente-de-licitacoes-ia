export type StatusProcessamento = 'PENDENTE' | 'PROCESSANDO' | 'CONCLUIDO' | 'FALHOU';

export interface Documento {
  id: number;
  titulo: string;
  orgao: string | null;
  arquivoNome: string;
  paginas: number | null;
  statusProcessamento: StatusProcessamento;
  observacoes: string | null;
  criadoEm: string;
}

export interface RespostaChat {
  conversaId: number;
  resposta: string;
  paginasCitadas: number[];
  semResposta: boolean;
}

export type Papel = 'USUARIO' | 'ASSISTENTE';

export interface Mensagem {
  id: number;
  papel: Papel;
  conteudo: string;
  paginasCitadas: number[];
  criadaEm: string;
}

export interface Resumo {
  documentoId: number;
  objeto: string | null;
  valorEstimado: string | null;
  modalidade: string | null;
  prazos: string[];
  documentosExigidos: string[];
  atualizadoEm: string;
}

export interface CasoAvaliacao {
  pergunta: string;
  respostaEsperada: string;
}

export interface Avaliacao {
  id: number;
  pergunta: string;
  respostaEsperada: string;
  respostaObtida: string | null;
  passou: boolean | null;
  modelo: string;
  executadoEm: string;
}
