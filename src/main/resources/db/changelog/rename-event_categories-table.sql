-- liquibase formatted sql

--changeset vbanasevych:rename-categories-table
ALTER TABLE event_categories RENAME TO categories;