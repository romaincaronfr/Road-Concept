FROM node:latest

# Create app directory

RUN apt-get -y update

RUN apt-get -y install software-properties-common
RUN echo "deb http://ftp.debian.org/debian jessie-backports main" | tee -a /etc/apt/sources.list
RUN apt-get update -y
RUN apt-get -y install git gcc-4.8 g++-4.8 build-essential nodejs
RUN apt-get -y install libboost-dev zlib1g-dev libexpat1-dev libsparsehash-dev
RUN apt-get -t jessie-backports install -y libosmium2-dev osmium-tool libprotozero-dev libprotobuf-dev protobuf-compiler

RUN git clone https://github.com/scrosby/OSM-binary.git && \
    cd OSM-binary && \
    make -C src && \
    make -C src install

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Install app dependencies
COPY buildData/package.json /usr/src/app/
RUN npm install

# Bundle app source
COPY buildData/main.js /usr/src/app

EXPOSE 8889
CMD [ "npm", "start" ]
