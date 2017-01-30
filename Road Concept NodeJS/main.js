/**
 * Created by paul on 18/01/17.
 */

//===========================================//
//Lib
var http = require('http');
var os = require('os');
var fs = require('fs');
var osmtogeojson = require('osmtogeojson');
var dOMParser = require('xmldom').DOMParser;
console.log('Démmarage du serveur NodeJS : OK');

//===========================================//


var server = http.createServer(function (request, res) {
    var method = request.method;
    var body = '';
    request.on('error', function (err) {
        console.error(err);
    });
    request.on('data', function (data) {
        body += data
    });

    request.on('end', function () {
        try {
            var parsefile = new dOMParser().parseFromString(body.toString(), "text/xml");
        } catch (error) {
            console.error(error + "parsefiletest")
        }
        parsefile = osmtogeojson(parsefile);
        parsefile = JSON.stringify(parsefile);
        console.log(parsefile.length);
        res.writeHead(200, {
            "Content-Type": "text/html"
        });
        res.end(parsefile);

    });
});
server.listen(8888);