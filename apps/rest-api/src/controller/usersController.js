"use strict";

const dbController = require("./databaseController");
const Joi = require("joi");

/*
 * Schemas
 */
const userSchema = Joi.object({
  userId: Joi.number().required(),

  username: Joi.string().default(null).allow(null).optional(),
  first_name: Joi.string().default(null).allow(null).optional(),
  last_name: Joi.string().default(null).allow(null).optional(),
  phone_nr: Joi.string().default(null).allow(null).optional(),

  is_verified: Joi.number().min(0).max(1).default(0).optional(),
  is_support: Joi.number().min(0).max(1).default(0).optional(),
  is_scam: Joi.number().min(0).max(1).default(0).optional(),

  last_updated: Joi.number().default(-1).optional(), // -1 = current time
  monitor: Joi.number().min(0).max(1).default(0).optional(),
});

/*
 * Query functions
 */
// GET /users
module.exports.getUsers = async (limit = 200, offset = 0) => {
  return dbController.selectPaged("users", "*", 1, limit, offset);
};

// POST /users
module.exports.addUser = async (user) => {
  // validate user schema
  const { error, value } = userSchema.validate(user);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  // get user id and check
  const userId = parseInt(value.userId);
  if (userId == 0) {
    return {
      error: true,
      message: "Invalid / Special user",
    };
  }

  // current date in milliseconds
  const date = Date.now();

  // get a connection from the database
  const connection = await dbController.pool.getConnection();

  try {
    // get existing users if any exists
    const rows = await connection.query(
      "SELECT * FROM users WHERE userId = ? LIMIT 1;",
      [userId]
    );

    // there is an user existing with the same userId
    if (rows.length >= 1) {
      const oldUser = rows[0];

      // check if we monitor this user, if not, return with an error
      if (oldUser.monitor == 0) {
        return {
          error: true,
          message: "Not monitoring",
        };
      }

      // we'll compare the following mysql & object keys
      const fields = [
        "username",
        "first_name",
        "last_name",
        "phone_nr",
        "is_verified",
        "is_support",
        "is_scam",
      ];

      let update = {
        query: "",
        params: [],
      };

      for (let i = 0; i < fields.length; i++) {
        const field = fields[i];

        if (!(field in user)) {
          continue;
        }

        const _old = oldUser[field];
        const _new = value[field];

        if (_old != _new) {
          console.log(
            "[User Update | " +
              userId +
              "] Updating field " +
              field +
              " from " +
              _old +
              " to " +
              _new
          );

          update.query +=
            (update.query.length == 0 ? "" : ", ") + field + " = ?";
          update.params.push(_new);

          // ignore usernames that were empty before
          if (_old == null || _old == undefined) {
            if (field == "username") {
              continue;
            }
          }

          // add to updates
          await connection.query(
            "INSERT INTO users_updates (`userId`, `key`, `old_value`, `new_value`, `date`) VALUES (?, ?, ?, ?, ?);",
            [userId, field, _old, _new, date]
          );
        }
      }

      // update ?!
      if (update.query.length > 0) {
        update.query += ", last_updated = ?";
        update.params.push(date);

        update.params.push(userId); // this is used for the where clause and should be here.

        console.log("[User Update | " + userId + "] Updated user");

        // update user
        return await connection.query(
          "UPDATE users SET " + update.query + " WHERE userId = ?;",
          update.params
        );
      } else {
        console.log("[User Update | " + userId + "] No need to update");
      }
    } else {
      // insert new user
      return await connection.query(
        "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        [
          value.userId,
          value.username,
          value.first_name,
          value.last_name,
          value.phone_nr,
          value.is_verified,
          value.is_support,
          value.is_scam,
          date,
          1,
        ]
      );
    }

    return {
      error: false,
      message: "Nothing updated.",
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }
};

// GET /users/:id
module.exports.getUser = async (id) => {
  return dbController.getSingleCached("users", "*", "userId", id);
};

// PUT /users/:id
module.exports.updateUser = async (id, user) => {
  return dbController.update("users", "userId", user, id, [
    "username",
    "first_name",
    "last_name",
    "phone_nr",
    "is_verified",
    "is_support",
    "is_scam",
    "monitor",
  ]);
};
