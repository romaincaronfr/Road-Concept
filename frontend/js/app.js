/**
 * Created by Romain on 28/09/2016.
 */
var apiURL = "localhost:8080";
Backbone.Collection.prototype.absURL = "http://localhost:8080";
var loginToken = null;

var setHeader = function (xhr) {
    console.log('call setHeader');
    xhr.setRequestHeader('Cookie', 'vertx-web.session=' + loginToken);
}

$.ajaxSetup({
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true
});
if (!loginToken) {
    $.ajax({
            url: "http://" + apiURL + "/login",
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
        .done(function (data, textStatus, jqXHR) {
            console.log("HTTP Request Succeeded: " + jqXHR.status);
            console.log(data);
            loginToken = data.authenticationToken;
            //document.cookie = "vertx-web.session="+loginToken;
            console.log(loginToken);
            testTech();
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            console.log("HTTP Request Failed");
        })
        .always(function () {
            /* ... */
        });
}


var app = {

    views: {},

    models: {},

    collections: {},

    loadTemplates: function (views, callback) {

        var deferreds = [];

        $.each(views, function (index, view) {
            if (app[view]) {
                deferreds.push($.get('Templates/' + view + '.html', function (data) {
                    app[view].prototype.template = _.template(data);
                }, 'html'));
                console.log("loading view : " + view);
            } else {
                console.log(view + " not found");
            }
        });

        $.when.apply(null, deferreds).done(callback);
    }

};

app.models.mapModel = Backbone.Model.extend({});

app.collections.mapCollection = Backbone.Collection.extend({
    model: app.models.mapModel,
    url: function () {
        return this.absURL + '/api/maps';
    }
});
var testModel = new app.models.mapModel();
var testCollection = new app.collections.mapCollection();



$(document).ready(function () {
    console.log("document ready");
    app.loadTemplates(["loginView"],
        function () {
            app.router = new app.Router();
            Backbone.history.start();
        });
    console.log(testCollection.get());
});

function testTech(){
    testCollection.fetch();
    console.log(testCollection.toJSON());
    testCollection.add({"id":1,"name":"Ville de Lannion"});
    console.log(testCollection.toJSON());
}