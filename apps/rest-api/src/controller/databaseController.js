const mariadb = require("mariadb");

const config = require("../../config/database.json");

const pool = mariadb.createPool({
  host: config.maria.host,
  port: config.maria.port,
  user: config.maria.user,
  password: config.maria.pass,
  connectionLimit: config.maria.pool_max_conn,
  database: config.maria.database,
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
};

module.exports.disableForeignKeyChecks = async () => {
  console.log("📂 Disabling foreign key checks ...");
  const connection = await pool.getConnection();
  await connection.query("SET foreign_key_checks = 0;");
  console.log("📂 Done!");
};

module.exports.pool = pool;

  // Other functions
module.exports.selectPaged = async (table, what, where = 1, limit = 200, offset = 0) => {
  const connection = await pool.getConnection();
  try {
    const res = await connection.query(
      "SELECT " + what + " FROM "  + table + " WHERE " + where + " LIMIT ? OFFSET ?;",
      [parseInt(limit), parseInt(offset)]
    );
    return res.length == 0 ? [] : res;
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
};

module.exports.update = async (table, idField, obj, id, allowedFields) => {
  for (const key in obj) {
    if (!allowedFields.includes(key)) {
      return {
        error: true,
        message: `Illegal field: '${key}'`,
      };
    }
  }

  let affected = 0;

  // update
  const connection = await pool.getConnection();

  try {
    for (const key in obj) {
      const value = obj[key];

      // update in db
      const res = await connection.query(
        "UPDATE " + table + " SET " + key + " = ? WHERE " + idField + " = ?;",
        [value, parseInt(id)]
      );
      affected = res.affectedRows;
      console.log(res);
    }
  } catch (exception) {
    console.error(exception);
    return {
      error: true,
      message: exception.code,
    };
  } finally {
    if (connection) {
      connection.end();
    }
  }

  return {
    error: false,
    affected: affected,
  };
};

module.exports.add = async (table, schema, obj, valueFields) => {
  // validate message
  const { error, value } = schema.validate(obj);
  if (error) {
    return {
      error: true,
      message: error.details[0].message,
    };
  }

  // set to current date
  if (value.last_updated == -1) {
    value.last_updated = Date.now();
    console.log(Date.now());
  }

  const connection = await pool.getConnection();
  try {
    const arr = [];
    let values = "";

    for (let i = 0; i < valueFields.length; i++) {
      const field = valueFields[i];
      
      // console.log(field, value.get(field));
      console.log(field);
      arr.push(value[field]);
      values += "?,";
    }
    values = values.substring(0, values.length - 1);
    console.log(arr, values);

    return await connection.query("INSERT INTO "+ table + " VALUES (" + values + ");", arr);
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
};

module.exports.getSingle = async (table, what, idField, id) => {
  const connection = await pool.getConnection();
  try {
    const res = await connection.query(
      "SELECT " + what + " FROM " + table + " WHERE " + idField + " = ?;",
      [parseInt(id)]
    );
    return res.length == 0 ? [] : res[0];
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
}

module.exports.pool = pool;