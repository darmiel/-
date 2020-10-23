const express = require("express");
const winston = require("winston");

const helmet = require("helmet");
const morgan = require("morgan");
const cors = require("cors");

// Logger
const logger = winston.createLogger({
  level: "info",
  format: winston.format.simple(),
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: "app.log" }),
  ],
});

const app = express();
app.use(morgan("common"));
app.use(helmet());
app.use(
  cors({
    origin: "http://localhost:8869",
  })
);

const port = process.env.PORT || 3420;

app.get("/", (req, res) => {
  res.json({
    message: "It works!",
  });
});

app.use((req, res, next) => {
  const error = new Error(`Not found: ${req.originalUrl}`);
  res.status(404);
  next(error);
});

app.use((err, req, res, next) => {
  res.json({
    error: err.message,
    stack: err.stack,
  });
});

app.listen(port, () => {
  logger.info("Ja sollte da sein: http://localhost:" + port);
});
