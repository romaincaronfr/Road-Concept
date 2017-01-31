
//===========================================//
//Lib
var http = require('http');
var os = require('os');
var fs = require('fs');
var osmium = require('osmium');
var _ = require('underscore');
var stream = require('stream');
var geojson = require('geojson-stream');
var path = require('path');

console.log('Démmarage du serveur NodeJS : OK');

//===========================================//


var server = http.createServer(function (request, res) {
    var body = '';
    var wait = true;
    var file_id = os.uptime();
    var ws = fs.createWriteStream(file_id + ".osm");
    console.log(file_id);
    request.on('error', function (err) {
        console.error(err);
    });
    request.on('data', function (data) {
        ws.write(data);
        console.log("writing");
    });

    request.on('end', function() {
        ws.end();
        console.log('waiting');
        ws.on('close', function () {
            console.log('reading');
            try {
                var ws2 = fs.createWriteStream(file_id + '.json');

                osm2geojson(file_id + '.osm')
                    .on('fail', function (err) {
                        throw err;
                    })
                    .on('error', function (err) {
                        throw err;
                    })
                    .pipe(geojson.stringify())
                    .on('error', function (err) {
                        throw err;
                    })
                    .pipe(ws2).on("finish",function () {
                    console.log("restart");

                    res.writeHead(200, {
                        "Content-Type": "text/html"
                    });

                    fs.createReadStream(file_id + '.json').pipe(res);

                    fs.unlink(file_id + '.osm');
                    fs.unlink(file_id + '.json')
                });

            } catch (e) {
                res.writeHead(500, {
                    "Content-Type": "text/html"
                });
                res.end();
            }

        });
    });
});
server.listen(8889);

function osm2geojson(filepath, options) {
    var file = new osmium.File(path.resolve(filepath));
    var locator = new osmium.LocationHandler();
    var osmiumStream = new osmium.Stream(new osmium.Reader(file, locator));
    var toGeoJSON = new stream.Transform({ objectMode: true});
    options = options || {};

    toGeoJSON._transform = function(osm, enc, callback) {
        if (osm.type === 'relation') return callback();
        if (osm.type === 'area'){
            return callback();
        }
        if (osm.type === 'node'){
            return callback();
        }

        var type = osm.type;
        if (type === 'area') type = osm.from_way ? 'way' : 'relation';

        var temptags = osm.tags();

        var feature = {
            type: 'Feature',
            id: [type, osm.id].join('/'),
            properties: {type:type,id:osm.id,tags : temptags}
        };

        if (!options.allNodes && osm.type === 'node') {
            var ignore = ['source', 'created_by'];
            var keys = Object.keys(feature.properties);
            if (_.difference(keys, ignore).length === 0) return callback();
        }

        ['type', 'id', 'version', 'changeset', 'timestamp', 'user', 'uid']
            .forEach(function(key) {
                feature.properties['osm:' + key] = key === 'type' ? type : osm[key];
            });

        feature.properties['osm:timestamp'] = osm.timestamp();

        try {
            feature.geometry = osm.geojson();
        }
        catch (err) {
            err.osmId = feature.id;
            err.message = feature.id + ' | ' + err.message;

            if (options.failEvents) {
                toGeoJSON.emit('fail', err);
                return callback();
            }

            return callback();
        }

        callback(null, feature);
    };

    return osmiumStream.pipe(toGeoJSON);
}
