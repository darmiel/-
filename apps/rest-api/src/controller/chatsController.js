"use strict";

const pool = require("./databaseController").pool;
const Joi = require("Joi");

/*
 * Schemas
 */
const chatSchema = Joi.object({
  chatId: Joi.number().required(),
  username: Joi.string().default(null).optional(),
  date: Joi.number().default(null).optional(),
  title: Joi.string().default(null).optional(),
  description: Joi.string().default(null).optional(),
  member_count: Joi.number().min(0).default(0).optional(),
  is_channel: Joi.number().min(0).max(1).default(0).optional(),
  is_verified: Joi.number().min(0).max(1).default(0).optional(),
  is_scam: Joi.number().min(0).max(1).default(0).optional(),
  last_updated: Joi.number().default(-1).optional(), // -1 = current time
  monitor: Joi.number().min(0).max(1).default(0).optional(),
});

/*
 * Query functions
 */
// GET /chats
module.exports.getChats = async () => {
  const connection = await pool.getConnection();
  try {
    const res = await connection.query("SELECT * FROM chats WHERE 1");
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

// POST /chats
module.exports.addChat = async (chat) => {
  // validate message
  const { error, value } = chatSchema.validate(chat);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  // set to current date
  if (value.last_updated == -1) {
    value.last_updated = Date.now();
    console.log(Date.now());
  }

  const connection = await pool.getConnection();
  try {
    return await connection.query(
      "INSERT INTO chats VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
      [
        value.chatId,
        value.username,
        value.date,
        value.title,
        value.description,
        value.member_count,
        value.is_channel,
        value.is_verified,
        value.is_scam,
        value.last_updated,
        value.monitor,
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

// GET /chats/:id
module.exports.getChat = async (id) => {
  const connection = await pool.getConnection();
  try {
    const res = await connection.query(
      "SELECT * FROM chats WHERE chatId = ?;",
      [parseInt(id)]
    );
    return res.length == 0 ? {} : res;
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

// PUT /chats/:id
module.exports.updateChat = async (id, chat) => {
  const allowedFields = [
    "username",
    "date",
    "title",
    "description",
    "member_count",
    "is_channel",
    "is_verified",
    "is_scam",
    "last_updated",
    "monitor",
  ];

  for (const key in chat) {
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
    for (const key in chat) {
      const value = chat[key];

      // update in db
      const res = await connection.query(
        `UPDATE chats SET ${key} = ? WHERE chatId = ?;`,
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
