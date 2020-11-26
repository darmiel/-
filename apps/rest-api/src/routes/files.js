"use strict";

const Joi = require("joi");
const valid = require("../controller/validationController");

const express = require("express");

const router = express.Router();
router.use(express.json());

const controller = require("../controller/filesController");

/**
 * POST /files
 * Creates a new message
 */
router.post("/", async (req, res) => {
  const controllerResult = await controller.addFile(req.body);
  return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});

module.exports = router;