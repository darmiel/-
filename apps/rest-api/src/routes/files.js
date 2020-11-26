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

/**
 * GET /files/:messageId
 * Returns the files associated with the messageId
 */
router.get("/:messageId", async (req, res) => {
  let r = valid.validate(res, Joi.number().min(0), req.params.messageId);
  if (r == undefined) {
    return;
  }
  const rows = await controller.getFiles(r);
  if (rows == []) {
    return res.status(404).json([]);
  }
  return res.status(200).json(rows);
});

/**
 * GET /files/:messageId/:fileId
 * Returns the files associated with the messageId
 */
router.get("/:messageId/:fileUid", async (req, res) => {
  const messageId = valid.validate(res, controller.messageIdSchema, req.params.messageId);
  if (messageId == undefined) {
    return;
  }
  const fileUid = valid.validate(res, controller.fileUidSchema, req.params.fileUid);
  if (fileUid == undefined) {
    return;
  }

  const rows = await controller.getFile(messageId, fileUid);
  if (rows == []) {
    return res.status(404).json([]);
  }
  return res.status(200).json(rows);
});

module.exports = router;