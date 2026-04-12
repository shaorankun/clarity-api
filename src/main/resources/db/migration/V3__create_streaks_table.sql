CREATE TABLE streaks (
                       id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id       UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE ,
                       current_streak INT DEFAULT 0,
                       longest_streak  INT DEFAULT 0,
                       last_focus_date DATE
);