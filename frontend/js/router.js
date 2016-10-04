/**
 * Created by Romain on 30/09/2016.
 */

var login = null;
var map = null;

Backbone.sync = (function(syncFn) {
    return function(method, model, options) {
        options = options || {};
        // handle unauthorized error (401)
        options.error = function(xhr, textStatus, errorThrown) {
            console.log("error sync");
            if (xhr.status === 401) {
                console.log('error 401');
                app.router.navigate('login', { trigger: true });
            }
        };

        return syncFn.apply(this, arguments);
    };
})(Backbone.sync);

app.Router = Backbone.Router.extend({

    routes: {
        "": "map",
        "login": "login"
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