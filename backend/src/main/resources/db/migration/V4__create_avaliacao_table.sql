CREATE TABLE avaliacao (
    id                BIGSERIAL PRIMARY KEY,
    documento_id      BIGINT      NOT NULL REFERENCES documento (id) ON DELETE CASCADE,
    pergunta          TEXT        NOT NULL,
    resposta_esperada TEXT        NOT NULL,
    resposta_obtida   TEXT,
    passou            BOOLEAN,
    modelo            VARCHAR(100) NOT NULL,
    executado_em      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_avaliacao_documento_id ON avaliacao (documento_id);
