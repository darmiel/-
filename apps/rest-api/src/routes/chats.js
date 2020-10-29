"use strict";

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/chatsController");

/**
 * GET /chats
 * Returns all chats
 */
router.get("/", async (req, res) => {
  const controllerResult = await controller.getChats();
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
  const controllerResult = await controller.getChat(req.params.id);
  return res.status(controllerResult.error ? 404 : 200).json(controllerResult);
});

/**
 * PUT /chats/:id
 * Updates a specific chat
 */
router.put("/:id", async (req, res) => {
  const controllerResult = await controller.updateChat(
    req.params.id,
    req.body
  );
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

module.exports = router;
