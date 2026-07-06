CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      userName VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL
);

CREATE TABLE transactions (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             sender_id INT NOT NULL,
                             receiver_id INT NOT NULL,
                             description VARCHAR(255),
                             amount DOUBLE NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_transaction_sender
                                 FOREIGN KEY (sender_id)
                                     REFERENCES user(id),

                             CONSTRAINT fk_transaction_receiver
                                 FOREIGN KEY (receiver_id)
                                     REFERENCES user(id)
);

CREATE TABLE user_connection (
                                 user_id INT NOT NULL,
                                 connection_id INT NOT NULL,

                                 PRIMARY KEY (user_id, connection_id),

                                 CONSTRAINT fk_connection_user
                                     FOREIGN KEY (user_id)
                                         REFERENCES user(id),

                                 CONSTRAINT fk_connection_friend
                                     FOREIGN KEY (connection_id)
                                         REFERENCES user(id)
);