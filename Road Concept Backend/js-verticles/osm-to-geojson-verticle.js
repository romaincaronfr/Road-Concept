require("vertx-js/vertx");

var eb = vertx.eventBus();
eb.consumer("osmtogeojson-from-java", function (message) {
    var client = vertx.createHttpClient();
    var request = client.post(8889, "localhost", "/", function (response) {

        // Send to Vert.x event bus
        response.bodyHandler(function (totalBuffer) {
            var rep = totalBuffer.toString();
            console.log("Response => " + rep);
            eb.send("osmtogeojson-from-js", rep);
        });
    });
    request.putHeader("content-length", "" + message.body().toString().length());
    request.putHeader("content-type", "application/xml");
    request.setChunked(true);
    request.end(message.body());
});