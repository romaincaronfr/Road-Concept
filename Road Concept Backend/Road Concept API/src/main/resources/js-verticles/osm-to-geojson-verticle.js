require("vertx-js/vertx");

console.log("@@@ JS VERTICLE CALLED");
var eb = vertx.eventBus();
eb.consumer("osmtogeojson-from-java", function (message) {
    var client = vertx.createHttpClient();
    var request = client.post(8888, "node", "/", function (response) {

        // Send to Vert.x event bus
        response.bodyHandler(function (totalBuffer) {
            console.log("@@@ JS VERTICLE RECEIVED FROM NODE");

            var rep = totalBuffer.toString();
            //console.log("Response => " + rep);
            eb.send("osmtogeojson-from-js", rep);
            rep = null;
        });
    });
    request.putHeader("content-length", "" + message.body().toString().length());
    request.putHeader("content-type", "application/xml");
    request.setChunked(true);
    request.end(message.body());
    console.log("@@@ SEND TO NODE OK");

});