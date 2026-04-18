--liquibase formatted sql
--changeset HESHEGTO:BCORE-32-3

ALTER TABLE leads
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
