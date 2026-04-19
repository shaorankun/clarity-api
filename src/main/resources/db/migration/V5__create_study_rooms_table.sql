CREATE TABLE study_rooms (
                                id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                owner_id           UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                name               VARCHAR     NOT NULL,
                                invite_code        VARCHAR     UNIQUE NOT NULL,
                                is_active          BOOLEAN     DEFAULT TRUE,
                                created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);