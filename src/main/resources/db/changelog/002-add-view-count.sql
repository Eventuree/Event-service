-- changeset vbanasevych:add-view-count-column

ALTER TABLE events
    ADD COLUMN view_count BIGINT DEFAULT 0;