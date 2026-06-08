--liquibase formatted sql
--changeset HESHEGTO:GATE-4

CREATE TABLE employees (
                       id UUID PRIMARY KEY NOT NULL,
                       name VARCHAR(255) NOT NULL UNIQUE,
                       salary DECIMAL(15, 2) NOT NULL
);