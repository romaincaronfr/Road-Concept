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
                $('#danger-text-modal').html("<strong>Erreur ! </strong> Désolé, quelque chose s'est mal passée. Veuillez réessayer. (C'est encore le dev qui a du mal bosser...)");
                $('#modalError').modal('show');
            }
        };

        return syncFn.apply(this, arguments);
    };
})(Backbone.sync);

app.Router = Backbone.Router.extend({

    routes: {
        "": "start",
        "login": "login",
        "admin" : "admin"
        "user": "user",
        "map": "map"
    },

    initialize: function () {
        this.loginV = null;
        this.mapV = null;
        this.userV = null;
        this.navBarV = null;
        this.adminV = null;
    },

    start: function(){
        new app.waitView();
        if (!this.navBarV){
            this.navBarV = new app.navBarView(false,true);
        }else {
            if (this.navBarV.model){
                app.router.navigate('map', { trigger: true });
            }else {
                app.router.navigate('login', { trigger: true });
            }
        }
    },

    login: function () {
        this.checkAndDestroyNavbar();
        if (!this.loginV) {
            this.loginV = new app.loginView();
            //app.loginView.render();
            console.log('reusing home views');
        }else {
            this.loginV.render();
        }
        this.loginV.delegateEvents(); // delegate events when the views is recycled
        //this.$content.html(app.loginView.el);
    },

    map: function() {
        this.checkAndInitNavBar();
        if (!this.mapV) {
            this.mapV = new app.mapView();
        }else{
            this.mapV.render();
        }
    },

    user: function(){
        if (!this.navBarV){
            this.navBarV = new app.navBarView(true,false);
        }else {
            this.navBarV.checkUserModelBeforeMyUser();
        }
    },

    admin: function(){
        if(!this.adminV){
            this.adminV = new app.adminManaView();
        }else{
            this.adminV.render();
        }

    },

    checkAndInitNavBar: function(){
        if (!this.navBarV){
            this.navBarV = new app.navBarView(false,false);
        }
    },

    checkAndDestroyNavbar: function(){
        if (this.navBarV){
            this.navBarV.cleanHTML();
            this.navBarV = null;
        }
    }
});