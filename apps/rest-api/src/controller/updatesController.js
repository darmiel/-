"use strict";

const dbController = require("./databaseController");
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

module.exports.getUpdates = async (
  table,
  what,
  field,
  intVal,
  limit = 200,
  offset = 0
) => {
  const connection = await dbController.pool.getConnection();
  try {
    const res = await connection.query(
      "SELECT " +
        what +
        " FROM " +
        table +
        " WHERE " +
        field +
        " = ? " +
        "ORDER BY `date` DESC " +
        "LIMIT ? OFFSET ?;",
      [parseInt(intVal), parseInt(limit), parseInt(offset)]
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

module.exports.getLastUpdates = async (
  table,
  what,
  limit = 200,
  offset = 0
) => {
  const connection = await dbController.pool.getConnection();
  try {
    const res = await connection.query(
      "SELECT " +
        what +
        " FROM " +
        table +
        " WHERE 1 ORDER BY `date` DESC LIMIT ? OFFSET ?;",
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
