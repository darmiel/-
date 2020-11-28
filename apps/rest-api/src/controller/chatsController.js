"use strict";

const dbController = require("./databaseController");
const Joi = require("joi");

/*
 * Schemas
 */
const chatSchema = Joi.object({
  chatId: Joi.number().default(0).allow("").allow(null).optional(),
  groupId: Joi.number().default(0).allow("").allow(null).optional(),

  username: Joi.string().default(null).allow("").allow(null).optional(),
  date: Joi.number().default(0).allow("").allow(null).optional(),
  title: Joi.string().default(null).allow("").allow(null).optional(),
  description: Joi.string().default(null).allow("").allow(null).optional(),
  member_count: Joi.number().min(0).default(0).allow("").allow(null).optional(),
  type: Joi.string().default("unknown").allow("").allow(null).optional(),
  is_verified: Joi.number()
    .min(0)
    .max(1)
    .default(0)
    .allow("")
    .allow(null)
    .optional(),
  is_scam: Joi.number()
    .min(0)
    .max(1)
    .default(0)
    .allow("")
    .allow(null)
    .optional(),
  last_updated: Joi.number().default(-1).allow("").allow(null).optional(), // -1 = current time
  monitor: Joi.number()
    .min(0)
    .max(1)
    .default(0)
    .allow("")
    .allow(null)
    .optional(),
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
  // validate user schema
  const { error, value } = chatSchema.validate(chat);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  if (value.chatId == null && value.groupId == null) {
    return {
      error: true,
      message: "chatId or groupId missing.",
    };
  }

  // get chat id and check
  const chatId = value.chatId;
  const groupId = value.groupId;

  // current date in milliseconds
  const date = Date.now();

  // get a connection from the database
  const connection = await dbController.pool.getConnection();

  try {
    // get existing chats if any exists
    let rows = [];
    if (value.chatId != null) {
      rows = await connection.query(
        "SELECT * FROM chats WHERE chatId = ? LIMIT 1;",
        [parseInt(chatId)]
      );
    } else if (value.groupId != null) {
      rows = await connection.query(
        "SELECT * FROM chats WHERE groupId = ? LIMIT 1;",
        [groupId]
      );
    }

    // there is an chat existing with the same chatId
    if (rows.length >= 1) {
      const oldChat = rows[0];
      console.log(oldChat);

      // check if we monitor this channel, if not, return with an error
      if (oldChat.monitor == 0) {
        return {
          error: true,
          message: "Not monitoring",
        };
      }

      // we'll compare the following mysql & object keys
      const fields = [
        "groupId",
        "username",
        "date",
        "title",
        "description",
        "member_count",
        "type",
        "is_verified",
        "is_scam",
        "last_updated",
      ];

      const update = {
        query: "",
        params: [],
      };

      for (let i = 0; i < fields.length; i++) {
        const field = fields[i];

        if (!(field in chat)) {
          continue;
        }

        const _old = oldChat[field];
        const _new = value[field];

        if (_new == null) {
          continue;
        }

        if (_old !== _new) {
          console.log(
            `[Chat Update | ${chatId}] Updating field ${field} from ${_old} to ${_new}`
          );

          update.query +=
            (update.query.length == 0 ? "" : ", ") + field + " = ?";
          update.params.push(_new);

          if (field == "last_updated") {
            continue;
          }

          // add to updates
          await connection.query(
            "INSERT INTO chats_updates (`chatId`, `key`, `old_value`, `new_value`, `date`) VALUES (?, ?, ?, ?, ?);",
            [chatId, field, _old, _new, date]
          );
        }
      }

      // update ?!
      if (update.query.length > 0) {
        console.log(value);

        if (!update.query.includes("last_updated")) {
          update.query += ", last_updated = ?";
          update.params.push(date);
        } else if (update.query.length == 1) {
          return {
            error: true,
            message: "Only updating last_updated.",
          };
        }

        console.log(
          "[Chat Update | " +
            chatId +
            "] Update chat " +
            chatId +
            " | " +
            groupId
        );

        // update chat
        if (value.chatId != null) {
          console.log("by chat id");
          console.log(update);
          update.params.push(chatId); // this is used for the where clause and should be here.
          return await connection.query(
            "UPDATE chats SET " + update.query + " WHERE chatId = ?;",
            update.params
          );
        } else if (value.groupId != null) {
          console.log("by group id");
          console.log(update);
          update.params.push(groupId); // this is used for the where clause and should be here.
          return await connection.query(
            "UPDATE chats SET " + update.query + " WHERE groupId = ?;",
            update.params
          );
        }
      } else {
        console.log("[Chat Update | " + chatId + "] No need to update");
      }
    } else {
      // insert new chat
      return await connection.query(
        "INSERT INTO chats VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        [
          value.chatId,
          value.groupId,
          value.username,
          value.date,
          value.title,
          value.description,
          value.member_count,
          value.type,
          value.is_verified,
          value.is_scam,
          value.last_updated,
          1,
        ]
      );
    }

    return {
      error: false,
      message: "Nothing updated.",
    };
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
  return dbController.getSingleCached("chats", "*", "chatId", id);
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

module.exports.updateMemberCount = async (id, count) => {
  // get a connection from the database
  const connection = await dbController.pool.getConnection();

  try {
    const chatId = parseInt(id);
    const memberCount = parseInt(count);
    const date = Date.now();

    return await connection.query(
      "INSERT INTO chats_online_member_count VALUES (?, ?, ?);",
      [chatId, date, memberCount]
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
