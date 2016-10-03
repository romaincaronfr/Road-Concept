/**
 * Created by Romain on 30/09/2016.
 */

app.Router = Backbone.Router.extend({

    routes: {
        "": "login",
        "map": "map"
    },

    initialize: function () {
        this.$content = $("#content");
    },

    login: function () {
        app.loginView = new app.loginView();
        //app.loginView.render();
        console.log('reusing home views');
        app.loginView.delegateEvents(); // delegate events when the views is recycled
        //this.$content.html(app.loginView.el);
    },

    map: function() {
        app.mapView = new app.mapView();
    }

    

});