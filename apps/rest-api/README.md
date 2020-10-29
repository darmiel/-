# rest-api
## /messages
### GET /messages?offset=0
Returns last 200, offset messages

### POST /messages
Creates a new message and stores it to the database

**Input:**
```javascript
{
    messageId: "int required",
    chatId: "int required",
    userId: "int required",
    content_type: "int required",
    content: "text optional",
    date: "int required",
    deleted_on: "int optional",
    is_channel_post: "tinyint(1) optional, def: 0",
}
```

### GET /messages/:id
Returns the message with the id `:id`

| Code | Description       |
|------|-------------------|
| 200  | Message found     |
| 404  | Message not found |

### PUT /messages/:id
Update a message with the id `:id`

**Input example:**
```javascript
{
    content: "new content"
}
```

| Code | Description       |
|------|-------------------|
| 200  | Message updated   |
| 404  | Message not found |

## /chats
### GET /chats
Gets all chats

### POST /chats
Stores a new chat to the database.
The meta of the chat will be resolved later by an update worker.
You'll need only `chatId´

**Input:**
```javascript
{
    chatId: "int required"
}
```

But you CAN also pass:
```sql
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
```

| Code | Description         |
|------|---------------------|
| 200  | Chat stored         |
| 400  | Chat already exists |

### GET /chats/:id
Returns the chat with the id `:id`

| Code | Description    |
|------|----------------|
| 200  | Chat found     |
| 404  | Chat not found |

### PUT /chats/:id
Udates a chat with the id `:id`

**Input example:**
```javascript
{
    member_count: 1337420
}
```

| Code | Description       |
|------|-------------------|
| 200  | Chat updated      |
| 404  | Chat not found    |