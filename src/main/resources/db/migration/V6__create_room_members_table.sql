CREATE TABLE room_members (
                             id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                             room_id            UUID        NOT NULL REFERENCES study_rooms(id) ON DELETE CASCADE,
                             user_id            UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             joined_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);