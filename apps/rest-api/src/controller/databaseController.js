const { EROFS } = require("constants");
const mariadb = require("mariadb");
const { exit } = require("process");
const config = require("../../config/database.json");

const pool = mariadb.createPool({
  host: config.host,
  port: config.port,
  user: config.user,
  password: config.pass,
  connectionLimit: config.pool_max_conn,
  database: config.database,
});

/*
 * Database initialization
 */
async function testConnection() {
  try {
    console.log("👩‍💻 Testing db connection ...");
    const connection = await pool.getConnection();
    console.log("👩‍💻 SELECT 1");
    const rows = await connection.query("SELECT 1");
    console.log("👩‍💻 Result:");
    console.log(rows);
    return rows.length == 1;
  } catch (error) {
    console.error(error);
  }
  return false;
}
if (!testConnection()) {
  console.log("Connection to database failed.");
  exit(1);
}

async function disableForeignKeyChecks() {
  const connection = await pool.getConnection();
  await connection.query("SET foreign_key_checks = 0;");
}
disableForeignKeyChecks();

/*
 * Query functions
 */
// /messages
module.exports.getLastMessages = async function (limit = 200, offset = 0) {
  const connection = await pool.getConnection();
  return await connection.query(
    "SELECT * FROM messages WHERE 1 LIMIT ? OFFSET ?;",
    [parseInt(limit), parseInt(offset)]
  );
};
module.exports.getMessage = async function (messageId) {
  const connection = await pool.getConnection();
  return await connection.query(
    "SELECT * FROM messages WHERE messageId = ? LIMIT 1;", [
    parseInt(messageId),
  ]);
};
module.exports.addMessage = async function (message) {
    const connection = await pool.getConnection();
    
};