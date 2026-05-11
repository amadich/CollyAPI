CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================
-- ROOMS
-- =========================
CREATE TABLE rooms (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                       name VARCHAR(255) NOT NULL,

                       description TEXT,

                       is_private BOOLEAN NOT NULL DEFAULT false,

                       max_members INTEGER NOT NULL DEFAULT 10,

                       owner_id UUID NOT NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                       CONSTRAINT fk_room_owner
                           FOREIGN KEY (owner_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE
);

-- =========================
-- ROOM MEMBERS
-- =========================
CREATE TABLE room_members (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                              room_id UUID NOT NULL,

                              user_id UUID NOT NULL,

                              joined_at TIMESTAMP NOT NULL DEFAULT NOW(),

                              CONSTRAINT fk_member_room
                                  FOREIGN KEY (room_id)
                                      REFERENCES rooms(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_member_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT unique_room_user
                                  UNIQUE(room_id, user_id)
);

-- =========================
-- MESSAGES
-- =========================
CREATE TABLE messages (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                          room_id UUID NOT NULL,

                          sender_id UUID NOT NULL,

                          content TEXT,

                          message_type VARCHAR(50) NOT NULL,

                          media_url TEXT,

                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                          CONSTRAINT fk_message_room
                              FOREIGN KEY (room_id)
                                  REFERENCES rooms(id)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_message_sender
                              FOREIGN KEY (sender_id)
                                  REFERENCES users(id)
                                  ON DELETE CASCADE
);