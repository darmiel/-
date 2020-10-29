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
  is_channel: Joi.number().default(0).optional(),
  is_verified: Joi.number().default(0).optional(),
  is_scam: Joi.number().default(0).optional(),
  monitoring_restricted_reason: Joi.string().default(null).optional(),
  last_updated: Joi.number().default(-1).optional() // -1 = current time
});

/*
 * Query functions
 */
// GET /chats
module.exports.getChats = async () => {
  const connection = await pool.getConnection();
  try {
    const res = await connection.query("SELECT * FROM chats WHERE 1");
    console.log(res);
    console.log(res.length);
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