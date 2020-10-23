import express from "express";
import winston from "winston";

import helmet from "helmet";
import morgan from "morgan";

// Logger
const logger = winston.createLogger({
    level: 'info',
    format: winston.format.simple(),
    transports: [
        new winston.transports.Console(),
        new winston.transports.File({ filename: 'app.log' })
    ]
});

const app = express();
app.use(morgan("common"));
app.use(helmet());

const port = process.env.PORT || 8869;

app.get("/", (req: any, res: any) => {
    res.send("It works!");
});

app.listen(port, () => {
    logger.info("Ja sollte da sein: http://localhost:" + port);
});