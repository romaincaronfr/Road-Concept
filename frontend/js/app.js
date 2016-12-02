/**
 * Created by Romain on 28/09/2016.
 */
Backbone.Collection.prototype.absURL = "http://localhost:8080";
Backbone.Model.prototype.absURL = "http://localhost:8080";


$.ajaxSetup({
    xhrFields: {
        withCredentials: true
    },
    crossDomain: true
});


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
                //console.log("loading view : " + view);
            } else {
                //console.log(view + " not found");
            }
        });

        $.when.apply(null, deferreds).done(callback);
    }

};

