CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    rater_profile_id BIGINT NOT NULL,
    rated_profile_id BIGINT NOT NULL,
    score INT NOT NULL,

    CONSTRAINT fk_ratings_events FOREIGN KEY (event_id) REFERENCES events(id),

    CONSTRAINT uk_rating_event_rater UNIQUE (event_id, rater_profile_id)
);