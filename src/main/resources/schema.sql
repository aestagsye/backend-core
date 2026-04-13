-- schema.sql для PostgreSQL
-- Сущности: Company, Lead, Deal

-- Таблица companies
CREATE TABLE IF NOT EXISTS companies
(
    id       UUID PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE,
    industry VARCHAR(100)
);

-- Таблица leads
CREATE TABLE IF NOT EXISTS leads
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    status     VARCHAR(50)  NOT NULL,
    company_id UUID         NOT NULL,
    version    BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL,
    CONSTRAINT fk_leads_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE RESTRICT
);

-- Таблица deals
CREATE TABLE IF NOT EXISTS deals
(
    id         UUID PRIMARY KEY,
    lead_id    UUID           NOT NULL,
    amount     DECIMAL(15, 2) NOT NULL,
    status     VARCHAR(50)    NOT NULL,
    created_at TIMESTAMP      NOT NULL,
    CONSTRAINT fk_deals_lead FOREIGN KEY (lead_id) REFERENCES leads (id) ON DELETE RESTRICT
);

-- Индексы для производительности
CREATE INDEX IF NOT EXISTS idx_leads_email ON leads (email);
CREATE INDEX IF NOT EXISTS idx_leads_status ON leads (status);
CREATE INDEX IF NOT EXISTS idx_leads_company_id ON leads (company_id);
CREATE INDEX IF NOT EXISTS idx_deals_lead_id ON deals (lead_id);
CREATE INDEX IF NOT EXISTS idx_deals_status ON deals (status);