FROM node:latest

RUN npm install osmtogeojson
RUN npm install xmldom
COPY ./buildData/main.js /var/main.js

CMD node --max_old_space_size=4096 --optimize_for_size --max_executable_size=4096 --stack_size=4096 /var/main.js

EXPOSE 8888
