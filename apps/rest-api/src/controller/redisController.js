"use strict";

const { exit } = require("process");
const config = require("../../config/database.json");

const redis = require("redis");
const client = redis.createClient({
    host: config.redis.host,
    port: config.redis.port,
    // password: config.redis.auth,
    db: config.redis.database
});

client.on("error", error => {
    console.error(error);
    exit(0);
});

// promisify

const { promisify } = require("util");

module.exports.get = promisify(client.get).bind(client);
module.exports.set = promisify(client.set).bind(client);
module.exports.expire = promisify(client.expire).bind(client);
module.exports.exists = promisify(client.exists).bind(client);

module.exports.client = client;