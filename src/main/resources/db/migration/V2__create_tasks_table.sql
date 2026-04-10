CREATE TABLE tasks (
                       id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id       UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title VARCHAR(255) NOT NULL,
                       label  VARCHAR(100),
                       is_completed BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);