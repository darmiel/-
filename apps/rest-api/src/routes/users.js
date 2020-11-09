"use strict";

const Joi = require("joi");
const valid = require("../controller/validationController");

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
  let r = valid.validate(res, Joi.number().min(0), req.params.id);
  if (r == undefined) {
    return;
  }

  const rows = await controller.getUser(r);
  if (rows == []) {
    return res.status(404).json([]);
  }
  return res.status(200).json(rows);
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
  const controllerResult = await controller.updateUser(r, req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

module.exports = router;
