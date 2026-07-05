CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);
