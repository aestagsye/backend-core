--liquibase formatted sql
--changeset HESHEGTO:BCORE-32-4

CREATE TABLE deals (
    id UUID PRIMARY KEY NOT NULL,
    lead_id UUID NOT NULL REFERENCES leads(id),
    amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_deals_lead_id ON deals (lead_id);
CREATE INDEX idx_deals_status ON deals (status);
