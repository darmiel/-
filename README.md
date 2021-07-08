# Schwurbelwatch (school project)
~Due: `31.01.2021`~  
 
 **The project is canceled for an indefinite period of time**
 
Schwurbelwatch is a telegram channel monitoring system for known german telegram conspiracy theory channels.
It analyzes the channels in terms of deletion behavior (which messages are deleted when) and on user behavior (different bubbles / bots).  
It looks whether a pattern is recognizable, or whether there are similarities between the channels (always the same members, how is information scattered etc.).

### Following software/languages will probably be used:

###### Container
* <img src="assets/docker.png" height="32px" align="left">Docker (-Compose) 
  * (for dockerizing the complete application)

###### Languages
* <img src="assets/java.png" height="32px" align="left">Java 
  * (for providing the main telegram api, saving messages, ...)
* <img src="assets/nodejs.png" height="32px" align="left">NodeJS (Internal / External Api) 
  * (connection between message-crawler / frontend <-> database)
* <img src="assets/python.png" height="32px" align="left">Python
  * (For internal tools
  * and data analysis)

###### Web
* <img src="assets/nginx.png" height="32px" align="left">Nginx 
  * (for load balancing)
* <img src="assets/angular.png" height="32px" align="left">Angular 
  * (Frontend)

###### Databases
* <img src="assets/mariadb.png" height="32px" align="left">MariaDB 
  * (as the main database)
* <img src="assets/redis.png" height="32px" align="left">Redis 
  * (caching)
* <img src="assets/mongodb.png" height="32px" align="left">MongoDB
  * (for storing raw messages)

### DB
![img](https://i.imgur.com/YpmQavy.png)

### ToDo (Database & Co.)

-- ToDo locked. See GitHub Projects --

* [X] Domain aliases for `messages_url`
* [X] Build .so telegram client file to run this on a linux server
* [X] Setup CDN for files (& auto download them if <= x MB)

-- ToDo locked. See GitHub Projects --
