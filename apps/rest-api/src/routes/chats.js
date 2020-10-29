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
    return res.status(controllerResult.error ? 400 : 200).json(controllerResult);
});


module.exports = router;