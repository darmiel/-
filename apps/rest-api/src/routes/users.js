"use strict";

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/usersController");

/**
 * GET /users
 * Returns all users (200)
 */
router.get("/", async (req, res) => {
  const offset = req.query.offset || 0;
  const controllerResult = await controller.getUsers(200, offset);
  return res.status(controllerResult.error ? 500 : 200).json(controllerResult);
});

/**
 * POST /chats
 * Stores a new chat
 */
router.post("/", async (req, res) => {
  const controllerResult = await controller.addUser(req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

/**
 * GET /chats/:id
 * Returns a specific chat
 */
router.get("/:id", async (req, res) => {
  const controllerResult = await controller.getUser(req.params.id);
  return res.status(controllerResult.error ? 404 : 200).json(controllerResult);
});

/**
 * PUT /chats/:id
 * Updates a specific chat
 */
router.put("/:id", async (req, res) => {
  const controllerResult = await controller.updateUser(req.params.id, req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

module.exports = router;
