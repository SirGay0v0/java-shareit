DROP TABLE IF EXISTS users, items, booking, comments, requests, responses CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT email_unique UNIQUE (email),
    CONSTRAINT email_format CHECK (email != '' AND email ~ '^.+@.+\..+')
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    author      BIGINT                                  NOT NULL REFERENCES users (id),
    description VARCHAR(512)                            NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    owner_id    BIGINT                                  NOT NULL REFERENCES users (id),
    available   BOOLEAN,
    request_id  BIGINT REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS booking
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE,
    end_time   TIMESTAMP WITHOUT TIME ZONE,
    status     varchar(255)                            NOT NULL,
    booker_id  BIGINT                                  NOT NULL REFERENCES users (id),
    item_id    BIGINT                                  NOT NULL REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    text      VARCHAR(512)                            NOT NULL,
    author_id BIGINT                                  NOT NULL REFERENCES users (id),
    item_id   BIGINT                                  NOT NULL REFERENCES items (id),
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL
);


CREATE TABLE IF NOT EXISTS responses
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    item_id     BIGINT                                  NOT NULL REFERENCES items (id),
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    request_id  BIGINT                                  NOT NULL REFERENCES requests (id),
    available   BOOLEAN
);