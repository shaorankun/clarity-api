CREATE TABLE focus_sessions (
                         id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id            UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         task_id            UUID        REFERENCES tasks(id) ON DELETE CASCADE,
                         started_at         TIMESTAMP   NOT NULL,
                         ended_at           TIMESTAMP   ,
                         duration_minutes   INT         NOT NULL,
                         status             VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS'
                             CHECK ( status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED') )
);