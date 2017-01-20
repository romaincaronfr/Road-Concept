FROM node:latest

RUN npm install osmtogeojson
RUN npm install xmldom
COPY ./buildData/main.js /var/main.js

CMD node /var/main.js

EXPOSE 8888
