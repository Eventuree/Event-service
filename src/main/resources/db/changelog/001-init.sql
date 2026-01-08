-- liquibase formatted sql
-- changeset ebudovskyi:create-tables
CREATE TABLE IF NOT EXISTS event_categories(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    organizer_id BIGINT NOT NULL ,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    original_event_date TIMESTAMP,
    max_participants SMALLINT,
    category_id BIGINT,
    banner_photo_url VARCHAR(255),
    location VARCHAR(200),
    min_age SMALLINT,
    max_age SMALLINT,
    required_gender VARCHAR(50),
    chat_link VARCHAR(255),
    is_alive BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY (category_id) REFERENCES event_categories(id),
    CHECK (min_age IS NULL OR max_age IS NULL OR min_age <= max_age),
    CHECK (max_participants IS NULL OR max_participants > 0)
);
CREATE TABLE IF NOT EXISTS event_participants(
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    registration_status VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, event_id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE INDEX idx_event_participants_event_id
    ON event_participants(event_id);

CREATE INDEX idx_events_title_date
    ON events(title, event_date);

CREATE INDEX idx_events_date
    ON events(event_date);

CREATE INDEX idx_events_category_id
    ON events(category_id);

CREATE INDEX idx_events_alive_date
    ON events(event_date)
    WHERE is_alive = true;

CREATE INDEX idx_events_category_date
    ON events(category_id, event_date);


