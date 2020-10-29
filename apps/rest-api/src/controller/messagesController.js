"use strict";

const pool = require("./databaseController").pool;
const Joi = require("Joi");

/*
 * Schemas
 */
const messageSchema = Joi.object({
  messageId: Joi.number().required(),
  chatId: Joi.number().required(),
  userId: Joi.number().required(),
  content_type: Joi.number().min(0).max(5).required(),
  content: Joi.string().default(null).optional(),
  date: Joi.number().required(),
  deleted_on: Joi.number().default(null).optional(),
  is_channel_post: Joi.number().min(0).max(1).default(0).optional(),
});

/*
 * Query functions
 */
// GET /messages
module.exports.getLastMessages = async (limit = 200, offset = 0) => {
  const connection = await pool.getConnection();
  try {
    const res = await connection.query(
      "SELECT * FROM messages WHERE 1 LIMIT ? OFFSET ?;",
      [parseInt(limit), parseInt(offset)]
    );
    return res.length == 0 ? [] : res;
  } catch (exception) {
    return {
      error: true,
      message: exception.code,
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }
};

// GET /messages/:id
module.exports.getMessage = async (messageId) => {
  const connection = await pool.getConnection();
  try {
    return await connection.query(
      "SELECT * FROM messages WHERE messageId = ? LIMIT 1;",
      [parseInt(messageId)]
    );
  } catch (exception) {
    return {
      error: true,
      message: exception.code,
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }
};

// POST /messages
module.exports.addMessage = async (message) => {
  // validate message
  const { error, value } = messageSchema.validate(message);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  const connection = await pool.getConnection();
  try {
    return await connection.query(
      "INSERT INTO messages VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
      [
        value.messageId,
        value.chatId,
        value.userId,
        value.content_type,
        value.content,
        value.date,
        value.deleted_on,
        value.is_channel_post,
      ]
    );
  } catch (exception) {
    return {
      error: true,
      message: exception.code,
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }
};

// PUT /messages/:id
module.exports.updateMessage = async (id, message) => {
  const allowedFields = ["content_type", "content", "deleted_on"];

  for (const key in message) {
    if (!allowedFields.includes(key)) {
      return {
        error: true,
        message: `Illegal field: '${key}'`,
      };
    }
  }

  let affected = 0;

  // update
  const connection = await pool.getConnection();

  try {
    for (const key in message) {
      const value = message[key];

      // update in db
      const res = await connection.query(
        `UPDATE messages SET ${key} = ? WHERE messageId = ?;`,
        [value, parseInt(id)]
      );
      affected = res.affectedRows;
      console.log(res);
    }
  } catch (exception) {
    console.error(exception);
    return {
      error: true,
      message: exception.code,
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }

  return {
    error: false,
    affected: affected,
  };
};