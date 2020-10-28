"use strict";

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/databaseController");

// Joi
const contentType = {
  text: 1,
};



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
  const id = req.params.id;
  const rows = await controller.getMessage(id);
  if (rows.length == 1) {
    return res.status(200).json(rows);
  }
  return res.status(404).json([]);
});

/**
 * POST /messages
 * Creates a new message
 */
router.post("/", async (req, res) => {
  const controllerResult = await controller.addMessage(req.body);
  res.status(200).json(controllerResult);
});

router.put("/:id", async (req, res) => {
  const controllerResult = await controller.updateMessage(req.params.id, req.body);
  res.status(200).json(controllerResult);
});

module.exports = router;