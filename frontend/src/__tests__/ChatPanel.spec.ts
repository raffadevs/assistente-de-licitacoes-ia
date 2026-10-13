import { describe, expect, it, vi } from 'vitest';
import { mount } from '@vue/test-utils';
import ChatPanel from '../components/ChatPanel.vue';
import * as conversasService from '../services/conversas';
import type { RespostaChat } from '../types/api';

vi.mock('../services/conversas');

function flushPromises() {
  return new Promise((resolve) => setTimeout(resolve, 0));
}

describe('ChatPanel', () => {
  it('cria uma conversa ao montar', async () => {
    vi.mocked(conversasService.criarConversa).mockResolvedValue(42);

    mount(ChatPanel, { props: { documentoId: 1 } });
    await flushPromises();

    expect(conversasService.criarConversa).toHaveBeenCalledWith(1);
  });

  it('envia a pergunta e mostra a resposta com as páginas citadas', async () => {
    vi.mocked(conversasService.criarConversa).mockResolvedValue(42);
    const respostaDeExemplo: RespostaChat = {
      conversaId: 42, resposta: 'O prazo é de 3 dias úteis [p. 2].', paginasCitadas: [2], semResposta: false,
    };
    vi.mocked(conversasService.perguntar).mockResolvedValue(respostaDeExemplo);

    const wrapper = mount(ChatPanel, { props: { documentoId: 1 } });
    await flushPromises();

    await wrapper.find('input').setValue('Qual o prazo de impugnação?');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();

    expect(conversasService.perguntar).toHaveBeenCalledWith(42, 'Qual o prazo de impugnação?');
    expect(wrapper.text()).toContain('O prazo é de 3 dias úteis');
    expect(wrapper.text()).toContain('páginas: 2');
  });

  it('mostra mensagem de ausência de resposta sem páginas citadas', async () => {
    vi.mocked(conversasService.criarConversa).mockResolvedValue(42);
    vi.mocked(conversasService.perguntar).mockResolvedValue({
      conversaId: 42, resposta: 'Não encontrei essa informação no edital.', paginasCitadas: [], semResposta: true,
    });

    const wrapper = mount(ChatPanel, { props: { documentoId: 1 } });
    await flushPromises();

    await wrapper.find('input').setValue('Pergunta fora do documento');
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();

    expect(wrapper.text()).toContain('Não encontrei essa informação');
    expect(wrapper.text()).not.toContain('páginas:');
  });
});
