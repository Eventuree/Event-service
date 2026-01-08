-- changeset vbanasevych:insert-test-data

-- 1. Додаємо категорії (нам потрібні ID для івентів)
INSERT INTO event_categories (name, slug, description)
VALUES
    ('Music', 'music', 'Concerts, festivals and live performances'),
    ('Tech', 'tech', 'IT conferences, meetups and workshops'),
    ('Sports', 'sports', 'Football, yoga, gym and running');

-- 2. Додаємо івенти з різним view_count (щоб перевірити Trending)

-- Популярний івент (1500 переглядів) - має бути першим
INSERT INTO events (
    organizer_id, title, description, status, event_date,
    max_participants, category_id, is_alive, view_count, location
)
VALUES (
           1, 'Grand Rock Festival 2026', 'Huge music festival', 'PUBLISHED', NOW() + INTERVAL '30 days',
           5000, (SELECT id FROM event_categories WHERE name = 'Music'), true, 1500, 'Kyiv'
       );

-- Середній івент (500 переглядів)
INSERT INTO events (
    organizer_id, title, description, status, event_date,
    max_participants, category_id, is_alive, view_count, location
)
VALUES (
           1, 'Java Tech Talk', 'Discussing Spring Boot 3 features', 'PUBLISHED', NOW() + INTERVAL '7 days',
           100, (SELECT id FROM event_categories WHERE name = 'Tech'), true, 500, 'Online'
       );

-- Звичайний івент (10 переглядів) - має бути останнім
INSERT INTO events (
    organizer_id, title, description, status, event_date,
    max_participants, category_id, is_alive, view_count, location
)
VALUES (
           1, 'Morning Yoga Park', 'Relaxing yoga session', 'PUBLISHED', NOW() + INTERVAL '2 days',
           20, (SELECT id FROM event_categories WHERE name = 'Sports'), true, 10, 'Lviv'
       );

-- Новий івент (0 переглядів)
INSERT INTO events (
    organizer_id, title, description, status, event_date,
    max_participants, category_id, is_alive, view_count, location
)
VALUES (
           1, 'Local Running Club', '5km run for beginners', 'PUBLISHED', NOW() + INTERVAL '1 day',
           50, (SELECT id FROM event_categories WHERE name = 'Sports'), true, 0, 'Odesa'
       );