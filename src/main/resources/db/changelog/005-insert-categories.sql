-- liquibase formatted sql

--changeset vbanasevych:insert-default-categories
INSERT INTO categories (name, slug)
VALUES ('Спорт та активний відпочинок', 'sport_active'),
       ('Ігри та хобі', 'games_hobbies'),
       ('Соціальні та міські заходи', 'social_city'),
       ('Волонтерство', 'volunteering'),
       ('Подорожі та поїздки', 'travel'),
       ('Освіта та розвиток', 'education') ON CONFLICT (slug) DO NOTHING;