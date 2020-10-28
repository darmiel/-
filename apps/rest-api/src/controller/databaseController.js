const { EROFS } = require("constants");
const mariadb = require("mariadb");
const Joi = require("Joi");

const { exit } = require("process");
const config = require("../../config/database.json");

const pool = mariadb.createPool({
  host: config.host,
  port: config.port,
  user: config.user,
  password: config.pass,
  connectionLimit: config.pool_max_conn,
  database: config.database,
});

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
 * Database initialization
 */
async function testConnection() {
  try {
    console.log("👩‍💻 Testing db connection ...");
    const connection = await pool.getConnection();
    console.log("👩‍💻 SELECT 1");
    const rows = await connection.query("SELECT 1");
    console.log("👩‍💻 Result:");
    console.log(rows);
    return rows.length == 1;
  } catch (error) {
    console.error(error);
  }
  return false;
}
if (!testConnection()) {
  console.log("Connection to database failed.");
  exit(1);
}

async function disableForeignKeyChecks() {
  console.log("👩‍💻 Disabling foreign key checks ...");
  const connection = await pool.getConnection();
  await connection.query("SET foreign_key_checks = 0;");
  console.log("👩‍💻 Done!");
}
disableForeignKeyChecks();

/*
 * Query functions
 */
// /messages
module.exports.getLastMessages = async (limit = 200, offset = 0) => {
  const connection = await pool.getConnection();
  return await connection.query(
    "SELECT * FROM messages WHERE 1 LIMIT ? OFFSET ?;",
    [parseInt(limit), parseInt(offset)]
  );
};
module.exports.getMessage = async (messageId) => {
  const connection = await pool.getConnection();
  return await connection.query(
    "SELECT * FROM messages WHERE messageId = ? LIMIT 1;",
    [parseInt(messageId)]
  );
};
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
  }
};
module.exports.updateMessage = async (id, message) => {
  if (message.messageId == undefined) {
    return {
      error: true,
      message: "messageId not in object",
    };
  }

  const connection = await pool.getConnection();

  for (const key in message) {
    console.log(messageSchema.keys());
    console.log(messageSchema[key]);
    const value = message[key];

    // update in db
    try {
      await connection.query("UPDATE messages SET ? = ? WHERE messageId = ?;", [
        key,
        value,
        parseInt(id),
      ]);
    } catch (exception) {
      console.error(exception);
      return {
        error: true,
        message: exception.code,
      };
    }
  }
  return {};
};
