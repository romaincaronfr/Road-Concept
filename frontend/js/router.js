/**
 * Created by Romain on 30/09/2016.
 */

app.Router = Backbone.Router.extend({

    routes: {
        "": "home"
    },

    initialize: function () {
        this.$content = $("#content");
    },

    home: function () {
        app.loginView = new app.loginView();
        app.loginView.render();
        console.log('reusing home views');
        app.loginView.delegateEvents(); // delegate events when the views is recycled
        this.$content.html(app.loginView.el);
    }

    

});