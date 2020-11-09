"use strict";

const Joi = require("joi");

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/updatesController");
const valid = require("../controller/validationController");

async function byId(req, res, table, field) {
  const id = valid.validate(res, Joi.number(), req.params.id);
  if (id == undefined) {
    return;
  }

  const offset = valid.validate(
    res,
    Joi.number().min(0),
    req.query.offset || 0
  );
  if (offset == undefined) {
    return;
  }

  const rows = await controller.getUpdates(
    table,
    "*",
    field,
    id,
    200,
    offset
  );

  if (rows.length == 1) {
    return res.status(200).json(rows);
  }

  return res.status(404).json([]);
}

async function last(req, res, table) {
  const offset = valid.validate(
    res,
    Joi.number().min(0),
    req.query.offset || 0
  );
  if (offset == undefined) {
    return;
  }

  const rows = await controller.getLastUpdates(
    table,
    "*",
    200,
    offset
  );

  if (rows.length == 1) {
    return res.status(200).json(rows);
  }
  
  return res.status(404).json([]);
}

/*
 * Chats
 */

router.get("/chats/last", async (req, res) => {
  return await last(req, res, "chats_updates");
});


router.get("/chats/:id", async (req, res) => {
  return await byId(req, res, "chats_updates", "chatId");
});

/*
 * Users
 */

router.get("/users/last", async (req, res) => {
  return await last(req, res, "users_updates");
});


router.get("/users/:id", async (req, res) => {
  return await byId(req, res, "users_updates", "userId");
});

module.exports = router;