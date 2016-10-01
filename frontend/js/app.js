/**
 * Created by Romain on 28/09/2016.
 */

var loginToken = null;

if (!loginToken){
    $.ajax({
            url: "http://localhost:8080/login",
            type: "POST",
            headers: {
                "Content-Type": "application/json; charset=utf-8",
            },
            contentType: "application/json",
            data: JSON.stringify({
                "username": "efzrfz",
                "password": "fgreger"
            })
        })
        .done(function(data, textStatus, jqXHR) {
            console.log("HTTP Request Succeeded: " + jqXHR.status);
            console.log(data);
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            console.log("HTTP Request Failed");
        })
        .always(function() {
            /* ... */
        });


}

var app = {

    views: {},

    models: {},

    loadTemplates: function(views, callback) {

        var deferreds = [];

        $.each(views, function(index, view) {
            if (app[view]) {
                deferreds.push($.get('Templates/' + view + '.html', function(data) {
                    app[view].prototype.template = _.template(data);
                }, 'html'));
                console.log("loading view : "+view);
            } else {
                console.log(view + " not found");
            }
        });

        $.when.apply(null, deferreds).done(callback);
    }

};


$(document).ready(function () {
    console.log("document ready");
    app.loadTemplates(["loginView"],
        function () {
            app.router = new app.Router();
            Backbone.history.start();
        });
});