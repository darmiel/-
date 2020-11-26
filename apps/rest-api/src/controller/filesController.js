"use strict";

const dbController = require("./databaseController");
const Joi = require("joi");
/*
 * Schemas
 */
const fileSchema = Joi.object({
  fileUid: Joi.string().min(1).required(),
  messageId: Joi.number().required(),
  cdn_path: Joi.string().required(),
  size: Joi.number().optional(),
  downloaded: Joi.number().required(),
});

// POST /messages
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
