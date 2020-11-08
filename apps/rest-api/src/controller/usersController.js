"use strict";

const dbController = require("./databaseController");
const Joi = require("Joi");

/*
 * Schemas
 */
const userSchema = Joi.object({
  userId: Joi.number().required(),
  username: Joi.string().default(null).optional(),
  first_name: Joi.string().default(null).optional(),
  last_name: Joi.string().default(null).optional(),
  phone_nr: Joi.string().default(null).optional(),

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
  return dbController.add("users", userSchema, user, [
    "userId",
    "username",
    "first_name",
    "last_name",
    "phone_nr",
    "is_verified",
    "is_support",
    "is_scam",
    "last_updated",
    "monitor",
  ]);
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
