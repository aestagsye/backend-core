--liquibase formatted sql
--changeset HESHEGTO:BCORE-32-5

CREATE TABLE products (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(19, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
