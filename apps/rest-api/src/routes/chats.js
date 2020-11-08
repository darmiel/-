"use strict";

const Joi = require("joi");
const valid = require("../controller/validationController");

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/chatsController");

/**
 * GET /chats
 * Returns all chats
 */
router.get("/", async (req, res) => {
  const offset = req.query.offset || 0;

  // validate params
  let r = valid.validate(res, Joi.number().min(0), offset);
  if (r == undefined) {
    return;
  }
  
  const controllerResult = await controller.getChats(200, r);
  return res.status(controllerResult.error ? 500 : 200).json(controllerResult);
});

/**
 * POST /chats
 * Stores a new chat
 */
router.post("/", async (req, res) => {
  const controllerResult = await controller.addChat(req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

/**
 * GET /chats/:id
 * Returns a specific chat
 */
router.get("/:id", async (req, res) => {
  let r = valid.validate(res, Joi.number().min(0), req.params.id);
  if (r == undefined) {
    return;
  }

  const controllerResult = await controller.getChat(require);
  return res.status(controllerResult.error ? 404 : 200).json(controllerResult);
});

/**
 * PUT /chats/:id
 * Updates a specific chat
 */
router.put("/:id", async (req, res) => {
  let r = valid.validate(res, Joi.number().min(0), req.params.id);
  if (r == undefined) {
    return;
  }
  const controllerResult = await controller.updateChat(r, req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

module.exports = router;
