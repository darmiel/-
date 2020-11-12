# Schwurbelwatch (school project)
Due: `31.01.2021`  
  
Schwurbelwatch is a telegram channel monitoring system for known german telegram conspiracy theory channels.
It analyzes the channels in terms of deletion behavior (which messages are deleted when) and on user behavior (different bubbles / bots).  
It looks whether a pattern is recognizable, or whether there are similarities between the channels (always the same members, how is information scattered etc.).

### Following software will probably be used:
* Docker (-Compose), Kubernetes (for dockerizing the complete application)
* Java (for prividing the main telegram api, saving messages, ...)
* NodeJS (Internal Api)
* Angular (Frontend)
* MariaDB (Database)
* Redis (Caching)

### DB
![img](https://i.imgur.com/YpmQavy.png)

### ToDo (Database & Co.)
* [ ] Domain aliases for `messages_url`
* [ ] Build .so telegram client file to run this on a linux server