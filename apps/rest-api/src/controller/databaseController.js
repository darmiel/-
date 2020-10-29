const mariadb = require("mariadb");

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
module.exports.testConnection = async () => {
  try {
    console.log("📂 Testing db connection ...");
    const connection = await pool.getConnection();
    const rows = await connection.query("SELECT 1");
    return rows.length == 1;
  } catch (error) {
    console.error(error);
  }
  return false;
}

module.exports.disableForeignKeyChecks = async () => {
  console.log("📂 Disabling foreign key checks ...");
  const connection = await pool.getConnection();
  await connection.query("SET foreign_key_checks = 0;");
  console.log("📂 Done!");
}

module.exports.pool = pool;