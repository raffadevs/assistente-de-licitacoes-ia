CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE documento (
    id                   BIGSERIAL PRIMARY KEY,
    titulo               VARCHAR(250) NOT NULL,
    orgao                VARCHAR(250),
    arquivo_nome         VARCHAR(255) NOT NULL,
    paginas              INTEGER,
    status_processamento VARCHAR(20)  NOT NULL,
    observacoes          TEXT,
    criado_em            TIMESTAMPTZ  NOT NULL DEFAULT now()
);
