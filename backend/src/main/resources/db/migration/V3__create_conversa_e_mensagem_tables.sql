CREATE TABLE conversa (
    id           BIGSERIAL PRIMARY KEY,
    documento_id BIGINT      NOT NULL REFERENCES documento (id) ON DELETE CASCADE,
    criada_em    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE mensagem (
    id               BIGSERIAL PRIMARY KEY,
    conversa_id      BIGINT      NOT NULL REFERENCES conversa (id) ON DELETE CASCADE,
    papel            VARCHAR(10) NOT NULL CHECK (papel IN ('USUARIO', 'ASSISTENTE')),
    conteudo         TEXT        NOT NULL,
    trechos_citados  JSONB,
    criada_em        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_mensagem_conversa_id ON mensagem (conversa_id);
