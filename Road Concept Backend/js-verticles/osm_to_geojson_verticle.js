/**
 * Created by paul on 30/01/17.
 */


require("vertx-js/vertx");
var Buffer = require("vertx-js/buffer");
var totalebuffer = Buffer.buffer();

var eb = vertx.eventBus();
eb.consumer("osmtogeojson-from-java", function (message) {

    var client = vertx.createHttpClient();
    var request = client.post(8888, "localhost", "/", function (response) {

        // Send to Vert.x event bus
        response.bodyHandler(function (totalBuffer) {
            var rep = totalBuffer.toString();
            console.log("Response => " + rep);
            eb.send("osmtogeojson-from-js", rep);
        });
    });
    request.putHeader("content-length", "" + message.body().toString().length);
    request.putHeader("content-type", "application/xml");
    request.setChunked(true);
    request.end(message.body());
});