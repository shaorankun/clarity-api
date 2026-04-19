CREATE TABLE room_sessions (
                             id                 UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                             room_id            UUID        NOT NULL REFERENCES study_rooms(id) ON DELETE CASCADE UNIQUE,
                             status             VARCHAR     NOT NULL DEFAULT 'IDLE'
                                                CHECK (status IN ('FOCUSING', 'BREAK', 'IDLE')),
                             started_at         TIMESTAMP   ,
                             duration_minutes   INT
);