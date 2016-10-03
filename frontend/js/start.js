/**
 * Created by Romain on 02/10/2016.
 */
var testModel = new app.models.mapModel();
var testCollection = new app.collections.mapCollection();

$(document).ready(function () {
    console.log("document ready");
    app.loadTemplates(["loginView"],
        function () {
            app.router = new app.Router();
            Backbone.history.start();
        });
    testTech();
});

function testTech(){
    testCollection.fetch({success: function(){
        console.log(testCollection.models);
        console.log(testCollection.toJSON());
        console.log(testCollection.at(0));
    }});
    console.log(testCollection.toJSON());
    testCollection.add({"id":1,"name":"Ville de Lannion"});
    console.log(testCollection.toJSON());
}

