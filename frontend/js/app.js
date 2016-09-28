/**
 * Created by Romain on 28/09/2016.
 */
var directory = {

    views: {},

    models: {},

    loadTemplates: function(views, callback) {

        var deferreds = [];

        $.each(views, function(index, view) {
            if (directory[view]) {
                deferreds.push($.get('Templates/' + view + '.html', function(data) {
                    directory[view].prototype.template = _.template(data);
                }, 'html'));
                console.log("loading view : "+view);
            } else {
                console.log(view + " not found");
            }
        });

        $.when.apply(null, deferreds).done(callback);
    }

};

directory.Router = Backbone.Router.extend({

    routes: {
        "": "home"
    },

    initialize: function () {
        this.$content = $("#content");
    },

    home: function () {
            directory.loginView = new directory.loginView();
            directory.loginView.render();
            console.log('reusing home views');
            directory.loginView.delegateEvents(); // delegate events when the views is recycled
        this.$content.html(directory.loginView.el);
    }

});

$(document).ready(function () {
    console.log("document ready");
    directory.loadTemplates(["loginView"],
        function () {
            directory.router = new directory.Router();
            Backbone.history.start();
        });
});