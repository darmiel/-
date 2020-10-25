const express = require("express");
const router = express.Router();

const messages = [
  {
    messageId: 123456789,
    chatId: -987654321,
    senderId: 91827162,
    body: "Das ist ein Test",
  },
  {
    messageId: 223456789,
    chatId: -987654321,
    senderId: 91827162,
    body: "Das ist noch ein Test",
  },
  {
    messageId: 323456789,
    chatId: -987654321,
    senderId: 91827162,
    body: "Das ist ein dritter Test",
  }
];

router.use(express.json());

/**
 * GET /messages
 * Returns all messages
 */
router.get("/", (req, res) => {
  res.status(200).json(messages);
});

/**
 * POST /messages
 * Creates a new message
 */
router.post("/", (req, res) => {
  messages.push(req.body);
  res.status(200).json({ success: true, size: messages.length });
});

module.exports = router;