CREATE TABLE resumo_edital (
    documento_id        BIGINT PRIMARY KEY REFERENCES documento (id) ON DELETE CASCADE,
    objeto              TEXT,
    valor_estimado       NUMERIC(14, 2),
    modalidade           VARCHAR(80),
    prazos               JSONB,
    documentos_exigidos  JSONB,
    atualizado_em         TIMESTAMPTZ NOT NULL DEFAULT now()
);
