/**
 * Created by Romain on 02/10/2016.
 */


$(document).ready(function () {
    console.log("document ready");
    app.loadTemplates(["loginView","mapView","mapTableView"],
        function () {
            app.router = new app.Router();
            Backbone.history.start();
        });
});


