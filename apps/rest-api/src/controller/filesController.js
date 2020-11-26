"use strict";

const dbController = require("./databaseController");
const Joi = require("joi");
/*
 * Schemas
 */
const fileUidSchema = Joi.string().min(1).required();
const messageIdSchema = Joi.number().required();
const fileSchema = Joi.object({
  fileUid: fileUidSchema,
  messageId: messageIdSchema,
  cdn_path: Joi.string().required(),
  size: Joi.number().optional(),
  downloaded: Joi.number().required(),
});

module.exports.fileUidSchema = fileUidSchema;
module.exports.messageIdSchema = messageIdSchema;

// GET /files/:messageId/:fileId
module.exports.getFile = async (messageId, fileUid) => {
  const connection = await dbController.pool.getConnection();
  try {
    const rows = await connection.query(
      "SELECT * FROM messages_files WHERE fileUid = ? AND messageId = ?;", 
      [fileUid, parseInt(messageId)]
    );
    if (rows.length < 1) {
      return {};
    } else {
      return rows[0];
    }
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

// GET /files/:messageId
module.exports.getFiles = async (messageId) => {
  const connection = await dbController.pool.getConnection();
  try {
    return await connection.query(
      "SELECT * FROM messages_files WHERE messageId = ?;", 
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

// POST /files
module.exports.addFile = async (file) => {
  // validate user schema
  const { error, value } = fileSchema.validate(file);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  const fileUid = value.fileUid;
  const messageId = parseInt(value.messageId);

  // get a connection from the pool
  const connection = await dbController.pool.getConnection();

  try {
    return await connection.query(
      "INSERT INTO messages_files VALUES (?, ?, ?, ?, ?)",
      [fileUid, messageId, value.cdn_path, value.size, value.downloaded]
    );
  } finally {
    if (connection) {
      connection.close();
    }
  }
};
