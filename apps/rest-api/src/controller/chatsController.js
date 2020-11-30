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
  type: Joi.string().default(null).allow("").allow(null).optional(),
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

// Template
async function updateChatByX(idField, chat, additionalFields) {
  const { error, value } = chatSchema.validate(chat);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  if (chat[idField] == null) {
    return {
      error: true,
      message: idField + " required.",
    };
  }

  const fields = [
    "username",
    "date",
    "title",
    "description",
    "member_count",
    "type",
    "is_verified",
    "is_scam",
  ];
  // add additional fields
  for (const idx in additionalFields) {
    fields.push(additionalFields[idx]);
  }

  const date = Date.now();
  const connection = await dbController.pool.getConnection();

  try {
    const rows = await connection.query(
      `SELECT * FROM chats WHERE \`${idField}\` = ? LIMIT 1;`,
      [parseInt(value[idField])]
    );

    // no old chat found -> insert
    if (rows.length !== 1) {
      return await connection.query(
        "INSERT INTO chats VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
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
          date,
          1,
        ]
      );
      // old chat found -> update
    } else {
      const old = rows[0];
      const update = {
        // default: last_updated
        last_updated: {
          param: date, // new value
          store: false, // save to update table
          old: null, // old value
        },
      };

      for (const indx in fields) {
        const field = fields[indx];
        if (!(field in old) || !(field in value)) {
          continue;
        }
        // check for null values
        const val = value[field];
        if (val == null || val == undefined) {
          continue;
        }
        // check for equal values
        const oldval = old[field];
        if (oldval === val) {
          continue;
        }
        // mark field as updated
        update[field] = {
          param: val,
          old: oldval,
          store: oldval != null
        };
      }

      // check if something was updated
      if (Object.keys(update).length === 1) {
        return {
          error: false,
          message: "Nothing updated.",
        };
      }

      console.log(update);

      let result = {};

      for (const key in update) {
        const param = update[key]["param"];
        const store = update[key]["store"];
        const oldParam = update[key]["old"];

        result = await connection.query(
          `UPDATE chats SET \`${key}\` = ? WHERE \`${idField}\` = ?;`,
          [param, value[idField]]
        );

        if (store) {
          // add to updates
          await connection.query(
            "INSERT INTO chats_updates (`" + idField + "`, `key`, `old_value`, `new_value`, `date`) VALUES (?, ?, ?, ?, ?);",
            [parseInt(value[idField]), key, oldParam, param, date]
          );
        }
      }

      return result;
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
}

module.exports.addChatChatId = async (chat) => {
  return updateChatByX("chatId", chat, ["groupId"]);
};

module.exports.addChatGroupId = async (chat) => {
  return updateChatByX("groupId", chat, []);
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
