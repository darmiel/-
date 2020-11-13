FROM node:15

LABEL maintainter="darmiel <hi@d2a.io>"

# Workdir
WORKDIR /usr/src/app

# Install dependencies
COPY package*.json ./

RUN npm install
RUN npm install joi

# App source
COPY . .

EXPOSE 3420
CMD [ "npm", "run", "start" ]