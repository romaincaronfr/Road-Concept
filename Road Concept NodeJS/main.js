/**
 * Created by paul on 18/01/17.
 */

//===========================================//
//Lib
var osmtogeojson = require('osmtogeojson');
var dOMParser = require('xmldom').DOMParser;
var fs = require('fs');
var http = require('http');
var os = require('os');
var cluster = require('cluster');
//===========================================//
//serveur HTTP//
var server = http.createServer(function(request, res) {

    var method = request.method;
    var body ='';

    if (method =='POST') {
        request.on('error', function (err) {
            console.error(err);
        }).on('data', function (data) {
            body = data.toString();
            //var data = fs.readFileSync('./Parsage/map_test.osm','utf8');
            var parsefile = new dOMParser().parseFromString(body, "text/xml");
            parsefile = osmtogeojson(parsefile);
            parsefile = JSON.stringify(parsefile);
            res.writeHead(200, {"Content-Type": "text/html"});
            res.write(parsefile);
            res.end();

        });
    } else if (method =='GET') {
        res.writeHead(200, {"Content-Type": "text/html"});
        res.write("utilisez la méthode POST plutôt que GET");
        res.end();
    }

});
server.listen(8888);