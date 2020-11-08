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
// GET /chats
module.exports.getChats = async (limit = 200, offset = 0) => {
  return dbController.selectPaged("chats", "*", 1, limit, offset);
};

// POST /chats
module.exports.addChat = async (chat) => {
  return dbController.add("chats", chatSchema, chat, [
    "chatId",
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
  ]);
};

// GET /chats/:id
module.exports.getChat = async (id) => {
  return dbController.getSingle("chats", "*", "chatId", id);
};

// PUT /chats/:id
module.exports.updateChat = async (id, chat) => {
  return dbController.update("chats", "chatId", chat, id, [
    "username",
    "date",
    "title",
    "description",
    "member_count",
    "is_channel",
    "is_verified",
    "is_scam",
    "monitor",
  ]);
};