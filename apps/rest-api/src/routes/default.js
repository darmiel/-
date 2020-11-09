"use strict";

const express = require("express");

const router = express.Router();
router.use(express.json());

// Redis
const redis = require("../controller/redisController");
const maria = require("../controller/databaseController");

/**
 * GET /messages
 * Returns last 200 messages
 */
router.get("/", async (req, res) => {
  res.status(200).json({ hello: "world" });
});

/**
 * GET /contenttypes
 * Returns all valid content types
 */
router.get("/contenttypes", async (req, res) => {
  const redisKey = "cache:contenttypes";

  // check redis
  if (await redis.exists(redisKey)) {
    return res.status(200).json(JSON.parse(await redis.get(redisKey)));
  }

  // from mysql
  const connection = await maria.pool.getConnection();
  try {
    const types = await connection.query("SELECT * FROM content_types WHERE 1");
    console.log(types);

    // update redis
    await redis.set(redisKey, JSON.stringify(types));
    await redis.expire(redisKey, 5);

    // out
    return res.status(200).json(types);
  } catch (exception) {
    return {
      error: true,
      message: exception.code,
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }
});

module.exports = router;
