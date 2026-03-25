-- Таблица Leads (потенциальные клиенты)
CREATE TABLE IF NOT EXISTS leads (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    company VARCHAR(255),
    status VARCHAR(50) NOT NULL
);

-- Индекс для быстрого поиска по email
CREATE INDEX IF NOT EXISTS idx_leads_email ON leads(email);

-- Индекс для фильтрации по статусу
CREATE INDEX IF NOT EXISTS idx_leads_status ON leads(status);

-- -- Таблица Contacts (контактные лица)
-- CREATE TABLE IF NOT EXISTS contacts (
--     id UUID PRIMARY KEY,
--     lead_id UUID REFERENCES leads(id) ON DELETE CASCADE,
--     first_name VARCHAR(255) NOT NULL,
--     last_name VARCHAR(255) NOT NULL,
--     email VARCHAR(255) NOT NULL,
--     phone VARCHAR(50),
--     position VARCHAR(100),
--     is_primary BOOLEAN DEFAULT FALSE,
--     created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP WITH TIME ZONE
--                                                                               );
--
-- -- Индекс для быстрого поиска контактов по lead_id
-- CREATE INDEX IF NOT EXISTS idx_contacts_lead_id ON contacts(lead_id);

-- Таблица Deals
CREATE TABLE IF NOT EXISTS deals (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL REFERENCES leads(id) ON DELETE CASCADE,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для deals
CREATE INDEX IF NOT EXISTS idx_deals_lead_id ON deals(lead_id);
CREATE INDEX IF NOT EXISTS idx_deals_stage ON deals(status);