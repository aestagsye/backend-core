--liquibase formatted sql
--changeset HESHEGTO:BCORE-32-2

CREATE TABLE leads (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    company_id UUID NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_leads_email ON leads (email);
CREATE INDEX idx_leads_status ON leads (status);
CREATE INDEX idx_leads_company_id ON leads (company_id);
