CREATE TABLE chats
(
    chatId       int                  NOT NULL
        PRIMARY KEY,
    username     varchar(32)          NULL,
    date         bigint               NULL,
    title        text                 NULL,
    description  text                 NULL,
    member_count int                  NULL,
    is_channel   tinyint(1) DEFAULT 0 NOT NULL,
    is_verified  tinyint(1) DEFAULT 0 NOT NULL,
    is_scam      tinyint(1) DEFAULT 0 NOT NULL,
    last_updated bigint               NOT NULL,
    monitor      tinyint(1) DEFAULT 0 NOT NULL
);

CREATE INDEX chats_chatId_index
    ON chats (chatId);

CREATE INDEX chats_username_index
    ON chats (username);

CREATE TABLE chats_updates
(
    updateId  int AUTO_INCREMENT
        PRIMARY KEY,
    chatId    int         NOT NULL,
    `key`     varchar(64) NOT NULL,
    old_value text        NULL,
    new_value text        NULL,
    date      bigint      NOT NULL,
    CONSTRAINT chats_updates_chats_chatId_fk
        FOREIGN KEY (chatId) REFERENCES chats (chatId)
);

CREATE TABLE content_types
(
    typeId int AUTO_INCREMENT
        PRIMARY KEY,
    type   varchar(64) NOT NULL
);

CREATE INDEX content_types_typeId_index
    ON content_types (typeId);

CREATE TABLE messages
(
    messageId       int                  NOT NULL
        PRIMARY KEY,
    chatId          int                  NOT NULL,
    userId          int                  NOT NULL,
    content_type    int                  NOT NULL,
    content         text                 NULL,
    date            bigint               NOT NULL,
    deleted_on      bigint               NULL,
    is_channel_post tinyint(1) DEFAULT 0 NULL
);

CREATE INDEX messages_chats_chatId_fk
    ON messages (chatId);

CREATE INDEX messages_content_types_typeId_fk
    ON messages (content_type);

CREATE INDEX messages_users_userId_fk
    ON messages (userId);

CREATE TABLE messages_edits
(
    editId      int AUTO_INCREMENT
        PRIMARY KEY,
    messageId   int      NOT NULL,
    old_content text     NULL,
    new_content text     NULL,
    date        datetime NOT NULL,
    CONSTRAINT messages_edits_messages_messageId_fk
        FOREIGN KEY (messageId) REFERENCES messages (messageId)
);

CREATE TABLE messages_urls
(
    urlId     int AUTO_INCREMENT
        PRIMARY KEY,
    messageId int  NOT NULL,
    url       text NOT NULL,
    domain    text NULL,
    path      text NULL,
    CONSTRAINT messages_urls_messages_messageId_fk
        FOREIGN KEY (messageId) REFERENCES messages (messageId)
);

CREATE TABLE users
(
    userId      int                  NOT NULL
        PRIMARY KEY,
    username    varchar(64)          NULL,
    first_name  varchar(64)          NULL,
    last_name   varchar(64)          NULL,
    phone_nr    varchar(32)          NULL,
    is_verified tinyint(1) DEFAULT 0 NOT NULL,
    is_support  tinyint(1) DEFAULT 0 NOT NULL,
    is_scam     tinyint(1) DEFAULT 0 NOT NULL
);

CREATE INDEX users_first_name_last_name_username_index
    ON users (first_name, last_name, username);

CREATE TABLE users_group_memberships
(
    userId  int NOT NULL,
    groupId int NOT NULL,
    PRIMARY KEY (userId, groupId),
    CONSTRAINT users_group_memberships_chats_chatId_fk
        FOREIGN KEY (groupId) REFERENCES chats (chatId),
    CONSTRAINT users_group_memberships_users_userId_fk
        FOREIGN KEY (userId) REFERENCES users (userId)
);

CREATE TABLE users_updates
(
    updateId  int AUTO_INCREMENT
        PRIMARY KEY,
    userId    int         NOT NULL,
    `key`     varchar(64) NOT NULL,
    old_value text        NULL,
    new_value text        NULL,
    date      bigint      NOT NULL,
    CONSTRAINT users_updates_users_userId_fk
        FOREIGN KEY (userId) REFERENCES users (userId)
);