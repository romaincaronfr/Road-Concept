/**
 * Created by Romain on 30/09/2016.
 */

var login = null;
var map = null;

app.Router = Backbone.Router.extend({

    routes: {
        "": "login",
        "map": "map"
    },

    initialize: function () {
        this.$content = $("#content");
    },

    login: function () {
        if (!login) {
            login = new app.loginView();
            //app.loginView.render();
            console.log('reusing home views');
        }else {
            login.render();
        }
        login.delegateEvents(); // delegate events when the views is recycled
        //this.$content.html(app.loginView.el);
    },

    map: function() {
        if (!map) {
            map = new app.mapView();
        }else{
            map.render();
        }
    }

    

});