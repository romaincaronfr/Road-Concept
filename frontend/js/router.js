/**
 * Created by Romain on 30/09/2016.
 */



Backbone.sync = (function (syncFn) {
    return function (method, model, options) {
        options = options || {};
        // handle unauthorized error (401)
        options.error = function (xhr, textStatus, errorThrown) {
            console.log("error sync");
            if (xhr.status === 401) {
                console.log('error 401');
                app.router.navigate('login', {trigger: true});
            } else {
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
        "admin": "admin",
        "user": "user",
        "map": "map",
        "logout": "logout",
        "map/:id": "mapDetail",
        "editmap/:id": "mapEdition",
        "simmap/:id/s/:samplingRate/d/:departureLivingS/m/:idMap" : "mapSimulation",
        "users": "users",
        "homeSimulation/:id": "homeSimulation",
        "simulationCreation/:id": "simulationCreation",
        "simmapCar/s/:idSimu/t/:simTime/samp/:sampling/map/:idMap/car/:idCar" : "carSimulation"
    },

    initialize: function () {
        this.loginV = null;
        this.mapV = null;
        this.userV = null;
        this.navBarV = null;
        this.adminV = null;
        this.adminMV = null;
        this.detailPageV = null;
        this.editmapV = null;
        this.simmapView = null;
        this.homeSimuV = null;
        this.simuCreatV = null;
        this.simuCarV = null;
    },

    start: function () {
        new app.waitView();
        if (!this.navBarV) {
            this.navBarV = new app.navBarView(false, true);
        } else {
            if (this.navBarV.model) {
                app.router.navigate('map', {trigger: true});
            } else {
                app.router.navigate('login', {trigger: true});
            }
        }
    },

    login: function () {
        this.checkAndDestroyMap();
        this.checkAndDestroyNavbar();
        if (!this.loginV) {
            this.loginV = new app.loginView();
            //app.loginView.render();
            console.log('reusing home views');
        } else {
            this.loginV.render();
        }
        this.loginV.delegateEvents(); // delegate events when the views is recycled
        //this.$content.html(app.loginView.el);
    },

    map: function () {
        this.checkAndDestroyMap();
        this.checkAndInitNavBar();
        if (!this.mapV) {
            this.mapV = new app.mapView();
        } else {
            this.mapV.render();
        }
    },

    mapDetail: function(id){
        this.checkAndInitNavBar();
        if (!this.detailPageV){
            this.detailPageV = new app.mapDetailsPageView({id:id});
        } else {
            this.detailPageV.changeID(id);
            this.detailPageV.render();
        }

    },

    mapEdition : function(id){
        this.checkAndInitNavBar();
        if (!this.editmapV){
            this.editmapV = new app.mapEditionView({id:id});
        } else {
            this.editmapV.changeID(id);
            this.editmapV.render();
        }

    },

    mapSimulation : function (id,samplingRate,departureLivingS,idMap){
        console.log("mapSimulation")
        console.log(id);
        console.log(samplingRate);
        console.log(departureLivingS);
        console.log(idMap);

        this.checkAndInitNavBar();
        if (!this.simmapView){
            this.simmapView = new app.mapSimulationView({id:id,samplingRate:samplingRate,departureLivingS:departureLivingS,idMap:idMap});
        } else {
            this.simmapView.changeID(id,samplingRate,departureLivingS,idMap);
            this.simmapView.render();
        }
    },

    user: function () {
        this.checkAndDestroyMap();
        if (!this.navBarV) {
            this.navBarV = new app.navBarView(true, false);
        } else {
            this.navBarV.checkUserModelBeforeMyUser();
        }
        $('#mapRow').empty();
    },

    admin: function () {
        this.checkAndDestroyMap();
        this.checkAndInitNavBar();
        if (!this.adminV) {
            this.adminV = new app.adminManaView();
        } else {
            this.adminV.render();
        }
    },

    logout: function () {
        this.checkAndDestroyMap();
        new app.logoutView();
    },

    homeSimulation: function(id){
        this.checkAndDestroyMap();
        this.checkAndInitNavBar();
        if (!this.homeSimuV){
            console.log("homeSimulation null");
            this.homeSimuV = new app.simulationHomeView({id:id});
        } else {
            console.log("homeSimulation non null");
            this.homeSimuV.changeID(id);
            this.homeSimuV.render();
        }
    },

    carSimulation: function(idSimu,simTime,sampling,idMap,idCar){
        this.checkAndInitNavBar();
        if (!this.simuCarV){
            console.log("simCarV null");
            this.simuCarV = new app.mapCarSimulationView({idSimu:idSimu, simTime:simTime,sampling:sampling,idMap:idMap,idCar:idCar});
        } else {
            console.log("simCarV not null");
            this.simuCarV.changeID(idSimu,simTime,sampling,idMap,idCar);
            this.simuCarV.render();
        }
    },

    simulationCreation: function(id){
        this.checkAndInitNavBar();
        if (!this.simuCreatV){
            this.simuCreatV = new app.simulationCreationView({id:id});
        } else {
            this.simuCreatV.changeID(id);
            this.simuCreatV.render();
        }
    },

    checkAndInitNavBar: function () {
        if (!this.navBarV) {
            this.navBarV = new app.navBarView(false, false);
        }
    },

    checkAndDestroyNavbar: function () {
        if (this.navBarV) {
            this.navBarV.cleanHTML();
            this.navBarV = null;
        }
    },
    checkAndDestroyMap: function() {
        $('#mapRow').remove();
    },
    users: function () {
        this.checkAndDestroyMap();
        this.checkAndInitNavBar();
        if (!this.adminMV){
            this.adminMV = new app.adminManaUsersView();
        }else{
            this.adminMV.render();
        }
    }


});