"use strict";

const Joi = require("joi");
const valid = require("../controller/validationController");

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/messagesController");

/**
 * GET /messages
 * Returns last 200 messages
 */
router.get("/", async (req, res) => {
  const offset = req.query.offset || 0;
  const rows = await controller.getLastMessages(200, offset);
  res.status(200).json(rows);
});

/**
 * GET /messages/:id
 * Returns the message with the id :id
 */
router.get("/:id", async (req, res) => {
  let r = valid.validate(res, Joi.number().min(0), req.params.id);
  if (r == undefined) {
    return;
  }
  const rows = await controller.getMessage(r);
  if (rows == []) {
    return res.status(404).json([]);
  }
  return res.status(200).json(rows);
});

/**
 * POST /messages
 * Creates a new message
 */
router.post("/", async (req, res) => {
  const controllerResult = await controller.addMessage(req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

router.put("/:id", async (req, res) => {
  let r = valid.validate(res, Joi.number().min(0), req.params.id);
  if (r == undefined) {
    return;
  }
  const controllerResult = await controller.updateMessage(
    r,
    req.body
  );
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

module.exports = router;