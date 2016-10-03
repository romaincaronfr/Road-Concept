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
    crossDomain: true,
    error : function(jqXHR, textStatus, errorThrown) {
        if (jqXHR.status == 401) {
            app.router.navigate('', { trigger: true });
        }
    }
});
if (loginToken) {
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
            //loginToken = data.authenticationToken;
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

