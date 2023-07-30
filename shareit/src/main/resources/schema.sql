CREATE TYPE IF NOT EXISTS BOOKING_STATUS AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email   VARCHAR(50) UNIQUE NOT NULL,
    name    VARCHAR(50)        NOT NULL
    );

CREATE TABLE IF NOT EXISTS item
(
    item_id     INTEGER AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    available   BOOLEAN      NOT NULL,
    owner_id    INTEGER      NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS booking
(
    booking_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    start_time TIMESTAMP      NOT NULL,
    end_time   TIMESTAMP      NOT NULL,
    item_id    INTEGER        NOT NULL,
    booker_id  INTEGER        NOT NULL,
    status     BOOKING_STATUS NOT NULL,
    FOREIGN KEY (item_id) REFERENCES item (item_id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comment
(
    comment_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    text       VARCHAR(255) NOT NULL,
    item_id    INTEGER      NOT NULL,
    author_id  INTEGER      NOT NULL,
    created    TIMESTAMP    NOT NULL,
    FOREIGN KEY (item_id) REFERENCES item (item_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS request
(
    request_id  INTEGER AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    created     TIMESTAMP    NOT NULL,
    user_id     INTEGER      NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);