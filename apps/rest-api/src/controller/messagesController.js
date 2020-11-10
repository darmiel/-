"use strict";

const dbController = require("./databaseController");
const Joi = require("Joi");

/*
 * Schemas
 */
const messageSchema = Joi.object({
  messageId: Joi.number().required(),
  chatId: Joi.number().required(),
  userId: Joi.number().required(),
  reply_to: Joi.number().default(0).optional(),
  content_type: Joi.number().min(0).required(),
  content: Joi.string().default(null).allow('').optional(),
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
  return dbController.add("messages", messageSchema, message, [
    "messageId",
    "chatId",
    "userId",
    "reply_to",
    "content_type",
    "content",
    "date",
    "deleted_on",
    "is_channel_post",
  ]);
};

// PUT /messages/:id
module.exports.updateMessage = async (id, message) => {
  return dbController.update("messages", "messageId", message, id, [
    "content_type",
    "content",
    "deleted_on",
  ]);
};
