CREATE TABLE chats
(
    chatId                       int         NOT NULL
        PRIMARY KEY,
    username                     varchar(32) NULL,
    date                         int         NULL,
    title                        text        NULL,
    description                  text        NULL,
    member_count                 int         NULL,
    is_channel                   tinyint(1)  NULL,
    is_verified                  tinyint(1)  NULL,
    is_scam                      tinyint(1)  NULL,
    monitoring_restricted_reason text        NULL,
    last_updated                 int         NULL
);

CREATE INDEX chats_chatId_index
    ON chats (chatId);

CREATE INDEX chats_username_index
    ON chats (username);

CREATE TABLE chats_monitoring
(
    chatId  int        NOT NULL
        PRIMARY KEY,
    invite  text       NULL,
    enabled tinyint(1) NOT NULL,
    CONSTRAINT chats_monitoring_chats_chatId_fk
        FOREIGN KEY (chatId) REFERENCES chats (chatId)
);

CREATE INDEX chats_monitoring_enabled_index
    ON chats_monitoring (enabled);

CREATE TABLE chats_updates
(
    updateId  int AUTO_INCREMENT
        PRIMARY KEY,
    chatId    int         NOT NULL,
    `key`     varchar(64) NULL,
    old_value text        NULL,
    new_value text        NULL,
    CONSTRAINT chats_updates_chats_chatId_fk
        FOREIGN KEY (chatId) REFERENCES chats (chatId)
);

CREATE TABLE content_types
(
    typeId int AUTO_INCREMENT
        PRIMARY KEY,
    type   varchar(64) NULL
);

CREATE INDEX content_types_typeId_index
    ON content_types (typeId);

CREATE TABLE users
(
    userId      int         NOT NULL
        PRIMARY KEY,
    username    varchar(64) NULL,
    first_name  varchar(64) NULL,
    last_name   varchar(64) NULL,
    phone_nr    varchar(32) NULL,
    is_verified tinyint(1)  NULL,
    is_support  tinyint(1)  NULL,
    is_scam     tinyint(1)  NULL
);

CREATE TABLE messages
(
    messageId       int        NOT NULL
        PRIMARY KEY,
    chatId          int        NOT NULL,
    userId          int        NOT NULL,
    content_type    int        NULL,
    content         text       NULL,
    date            int        NULL,
    deleted_on      int        NULL,
    is_channel_post tinyint(1) NULL,
    CONSTRAINT messages_chats_chatId_fk
        FOREIGN KEY (chatId) REFERENCES chats (chatId),
    CONSTRAINT messages_content_types_typeId_fk
        FOREIGN KEY (content_type) REFERENCES content_types (typeId),
    CONSTRAINT messages_users_userId_fk
        FOREIGN KEY (userId) REFERENCES users (userId)
);

CREATE TABLE messages_edits
(
    editId      int AUTO_INCREMENT
        PRIMARY KEY,
    messageId   int  NOT NULL,
    old_content text NULL,
    new_content text NULL,
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
    `key`     varchar(64) NULL,
    old_value text        NULL,
    new_value text        NULL,
    CONSTRAINT users_updates_users_userId_fk
        FOREIGN KEY (userId) REFERENCES users (userId)
);
