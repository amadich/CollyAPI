CREATE TABLE friend_requests (

                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                 sender_id UUID NOT NULL,
                                 receiver_id UUID NOT NULL,

                                 status VARCHAR(20) NOT NULL,

                                 CONSTRAINT fk_sender
                                     FOREIGN KEY (sender_id)
                                         REFERENCES users(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_receiver
                                     FOREIGN KEY (receiver_id)
                                         REFERENCES users(id)
                                         ON DELETE CASCADE
);