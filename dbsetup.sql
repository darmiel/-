CREATE TABLE chats
(
    chatId       bigint      NOT NULL
        PRIMARY KEY,
    groupId      int         NULL,
    username     tinytext    NULL,
    date         bigint      NULL,
    title        text        NULL,
    description  text        NULL,
    member_count int         NULL,
    type         varchar(16) NULL,
    is_verified  tinyint(1)  NULL,
    is_scam      tinyint(1)  NULL,
    last_updated bigint      NULL,
    monitor      tinyint(1)  NULL
);

CREATE TABLE chats_online_member_count
(
    chatId       bigint NOT NULL,
    date         bigint NOT NULL,
    member_count int    NOT NULL,
    PRIMARY KEY (chatId, date)
);

CREATE TABLE chats_updates
(
    updateId  int AUTO_INCREMENT
        PRIMARY KEY,
    chatId    bigint        NULL,
    groupId   bigint        NULL,
    `key`     tinytext      NULL,
    old_value varchar(4369) NULL,
    new_value varchar(4369) NULL,
    date      bigint        NULL
);

CREATE TABLE content_types
(
    typeId int      NULL,
    type   tinytext NULL
);

CREATE TABLE messages
(
    messageId       bigint        NULL,
    chatId          bigint        NULL,
    userId          bigint        NULL,
    reply_to        bigint        NULL,
    content_type    int           NULL,
    content         varchar(4369) NULL,
    date            bigint        NULL,
    deleted_on      bigint        NULL,
    is_channel_post tinyint       NULL,
    extra           text          NULL
);

CREATE TABLE messages_edits
(
    editId      int           NULL,
    messageId   bigint        NULL,
    old_content varchar(4369) NULL,
    new_content varchar(4369) NULL,
    date        bigint        NULL
);

CREATE TABLE messages_files
(
    fileUid    varchar(256) NOT NULL,
    messageId  bigint       NOT NULL,
    cdn_path   text         NOT NULL,
    size       bigint       NULL,
    downloaded bigint       NULL,
    PRIMARY KEY (messageId, fileUid)
);

CREATE TABLE messages_urls
(
    urlId     int           NULL,
    messageId bigint        NULL,
    url       varchar(4369) NULL,
    domain    varchar(4369) NULL,
    path      varchar(4369) NULL
);

CREATE TABLE users
(
    userId       bigint   NULL,
    username     tinytext NULL,
    first_name   tinytext NULL,
    last_name    tinytext NULL,
    phone_nr     tinytext NULL,
    is_verified  tinyint  NULL,
    is_support   tinyint  NULL,
    is_scam      tinyint  NULL,
    last_updated bigint   NULL,
    monitor      tinyint  NULL
);

CREATE TABLE users_group_memberships
(
    userId  bigint NULL,
    groupId bigint NULL
);

CREATE TABLE users_updates
(
    updateId  int           NULL,
    userId    bigint        NULL,
    `key`     tinytext      NULL,
    old_value varchar(4369) NULL,
    new_value varchar(4369) NULL,
    date      bigint        NULL
);
