/**
 * Created by Romain on 30/09/2016.
 */



Backbone.sync = (function(syncFn) {
    return function(method, model, options) {
        options = options || {};
        // handle unauthorized error (401)
        options.error = function(xhr, textStatus, errorThrown) {
            console.log("error sync");
            if (xhr.status === 401) {
                console.log('error 401');
                app.router.navigate('login', { trigger: true });
            }else {
                $('#modalError').modal('show');
            }
        };

        return syncFn.apply(this, arguments);
    };
})(Backbone.sync);

app.Router = Backbone.Router.extend({

    routes: {
        "": "map",
        "login": "login",
        "user": "user"
    },

    initialize: function () {
        this.login = null;
        this.map = null;
        this.user = null;
        this.navBar = new app.navBarView();
    },

    login: function () {
        if (!this.login) {
            this.login = new app.loginView();
            //app.loginView.render();
            console.log('reusing home views');
        }else {
            this.login.render();
        }
        this.login.delegateEvents(); // delegate events when the views is recycled
        //this.$content.html(app.loginView.el);
    },

    map: function() {
        if (!this.map) {
            this.map = new app.mapView();
        }else{
            this.map.render();
        }
    },

    user: function(){
        if (!this.user){
            console.log("Router : user");
            this.user = new app.userView();
        }else{
            this.user.render();
        }
    }
});