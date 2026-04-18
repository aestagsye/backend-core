--liquibase formatted sql
--changeset HESHEGTO:BCORE-32-1

CREATE TABLE companies (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    industry VARCHAR(100)
);
