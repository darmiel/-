"use strict";

const dbController = require("./databaseController");
const Joi = require("joi");
/*
 * Schemas
 */
const messageSchema = Joi.object({
  messageId: Joi.number().required(),
  chatId: Joi.number().required(),
  userId: Joi.number().required(),
  reply_to: Joi.number().default(0).optional(),
  content_type: Joi.number().min(0).required(),
  content: Joi.string().default(null).allow("").allow(null).optional(),
  date: Joi.number().required(),
  deleted_on: Joi.number().default(null).optional(),
  is_channel_post: Joi.number().min(0).max(1).default(0).optional(),
});

/*
 * Query functions
 */
// GET /messages
module.exports.getLastMessages = async (limit = 200, offset = 0) => {
  return dbController.selectPaged(
    "messages",
    "*",
    1,
    limit,
    offset,
    " ORDER BY `date` DESC"
  );
};

// GET /messages/:id
module.exports.getMessage = async (messageId) => {
  return dbController.getSingleCached("messages", "*", "messageId", messageId);
};

// POST /messages
module.exports.addMessage = async (message) => {
  // validate user schema
  const { error, value } = messageSchema.validate(message);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  const messageId = parseInt(value.messageId);
  const userId = parseInt(value.userId);
  const date = Date.now();

  // get a connection from the pool
  const connection = await dbController.pool.getConnection();

  // results
  let content = "";
  let res = {
    error: true,
    message: "unexpected error",
  };

  try {
    const rows = await connection.query(
      "SELECT messageId, content FROM messages WHERE messageId = ? LIMIT 1;",
      [messageId]
    );

    // there is a message existing with the id
    // -> check content and update, if neccesarily
    if (rows.length == 1) {
      const oldMessage = rows[0];

      const _old = oldMessage.content;
      const _new = value.content;

      if (_old == _new) {
        return {
          error: true,
          message: "Message with same content already exists!",
        };
      }

      content = _new;

      // add to edits
      await connection.query(
        "INSERT INTO messages_edits (`messageId`, `old_content`, `new_content`, `date`) VALUES (?, ?, ?, ?);",
        [messageId, _old, _new, date]
      );

      // update in db
      res = await connection.query(
        "UPDATE messages SET content = ? WHERE messageId = ?;",
        [_new, messageId]
      );
    } else {
      content = value.content;

      // normal insert
      res = await connection.query(
        "INSERT INTO messages VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
        [
          messageId,
          value.chatId,
          value.userId,
          value.reply_to,
          value.content_type,
          value.content,
          value.date,
          value.deleted_on,
          value.is_channel_post,
        ]
      );
    }

    // Add user membership
    await connection.query(
      "INSERT IGNORE INTO users_group_memberships VALUES (?, ?)",
      [userId, parseInt(message.chatId)]
    );

    // check content for any links
    if (content) {

      const urlRegex = /https?:\/\/(www\.)?([-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,4})\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;
      var matches = [], found;
      while ((found = urlRegex.exec(content))) {
        matches.push(found);
        urlRegex.lastIndex -= found[0].split(":")[1].length;
      }
      for (let i = 0; i < matches.length; i++) {
        const match = matches[i];

        const url = String(match[0]);
        const domain = String(match[2]);
        const path = String(match[3]);

        console.log(url, domain, path);

        // check if link already exists
        const linkRows = await connection.query(
          "SELECT urlId FROM messages_urls WHERE messageId = ? AND url = ?;",
          [messageId, url]
        );
        if (linkRows.length < 1) {
          // add link
          await connection.query(
            "INSERT INTO messages_urls (messageId, url, domain, path) VALUES (?, ?, ?, ?);",
            [messageId, url, domain, path]
          );
        }
      }
    }

    return res;
  } finally {
    if (connection) {
      connection.close();
    }
  }
};

// PUT /messages/:id
module.exports.updateMessage = async (id, message) => {
  return dbController.update("messages", "messageId", message, id, [
    "content_type",
    "content",
    "deleted_on",
  ]);
};
