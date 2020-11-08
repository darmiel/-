# rest-api

**ToC:**
- [rest-api](#rest-api)
  - [ToDo](#todo)
  - [Routes](#routes)
    - [/messages](#messages)
      - [GET /messages?offset=0](#get-messagesoffset0)
      - [POST /messages](#post-messages)
      - [GET /messages/:id](#get-messagesid)
      - [PUT /messages/:id](#put-messagesid)
    - [/chats](#chats)
      - [GET /messages?offset=0](#get-messagesoffset0-1)
      - [POST /chats](#post-chats)
      - [GET /chats/:id](#get-chatsid)
      - [PUT /chats/:id](#put-chatsid)
    - [/users](#users)
      - [GET /users?offset=0](#get-usersoffset0)
      - [POST /users](#post-users)
      - [GET /user/:id](#get-userid)
      - [PUT /users/:id](#put-usersid)

---

## ToDo
- [x] Messages Route
  - [x] GET /messages?offset=0
  - [x] POST /chats
  - [x] GET /messages/:id
  - [x] PUT /messages/:idx
- [x] Chats Route
  - [x] GET /chats?offset=0
  - [x] POST /chats
  - [x] GET /chats/:id
  - [x] PUT /chats/:id
- [x] Users Route
  - [x] GET /users?offset=0
  - [x] POST /users
  - [x] GET /users/:id
  - [x] PUT /users/:id
- [x] Deduplicate chats, messages and users controller
- [x] Validate every query parameter
- [x] GET /contentTypes
- [ ] Redisfy Routes:
  - [ ] GET /messages/:id
  - [ ] GET /chats/:id
  - [ ] GET /users/:id
  - [ ] GET /messages?offset=0
  - [ ] GET /chats?offset=0
  - [ ] GET /users?offset=0
- [ ] Updates Route
  - [ ] GET /updates/chats/:id
  - [ ] GET /updates/chats/last
  - [ ] GET /updates/users/:id
  - [ ] GET /updates/users/last

## Routes
### /messages
#### GET /messages?offset=0
Returns last 200, offset messages

#### POST /messages
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

#### GET /messages/:id
Returns the message with the id `:id`

| Code | Description       |
|------|-------------------|
| 200  | Message found     |
| 404  | Message not found |

#### PUT /messages/:id
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

### /chats
#### GET /messages?offset=0
Returns last 200, offset chats

#### POST /chats
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

#### GET /chats/:id
Returns the chat with the id `:id`

| Code | Description    |
|------|----------------|
| 200  | Chat found     |
| 404  | Chat not found |

#### PUT /chats/:id
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

### /users
#### GET /users?offset=0
Returns last 200, offset users

#### POST /users
Stores a new user to the database.

**Input:**
```javascript
{
    userIdId: "int required"
}
```

| Code | Description         |
|------|---------------------|
| 200  | User stored         |
| 400  | User already exists |

#### GET /user/:id
Returns the user with the id `:id`

| Code | Description    |
|------|----------------|
| 200  | User found     |
| 404  | User not found |

#### PUT /users/:id
Udates a user with the id `:id`

**Input example:**
```javascript
{
    phone_nr: "+4917634512124"
}
```

| Code | Description       |
|------|-------------------|
| 200  | User updated      |
| 404  | User not found    |