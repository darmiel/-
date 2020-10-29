const main = async () => {
  const { exit } = require("process");

  const express = require("express");

  const helmet = require("helmet");
  const morgan = require("morgan");
  const cors = require("cors");

  const app = express();
  app.use(morgan("common"));
  app.use(helmet());
  app.use(
    cors({
      origin: "http://localhost:3420",
    })
  );

  /**
   * Port used for the express app
   */
  const port = process.env.PORT || 3420;


  // Database Initialization :::
  const dbController = require("./controller/databaseController");
  if (!(await dbController.testConnection())) {
    console.log("Connection to database failed.");
    exit(1);
    return;
  }
  await dbController.disableForeignKeyChecks();
  // :::


  // Some test routes
  app.get("/", (req, res) => {
    res.json({
      message: "It works!",
    });
  });

  /*
   * Routes
   */
  app.use("/messages", require("./routes/messages"));
  app.use("/chats", require("./routes/chats"));
  app.use("/users", require("./routes/users"));

  /*
   * Other
   */
  app.use((req, res, next) => {
    const error = new Error(`Not found: ${req.originalUrl}`);
    res.status(404);
    next(error);
  });

  app.use((err, req, res, next) => {
    res.json({
      error: err.message,
      stack: process.env.NODE_ENV == "production" ? ":)" : err.stack,
    });
  });

  app.listen(port, () => {
    console.log("✅ Ja sollte da sein :) http://localhost:" + port);
  });
};

main();