/**
 * Created by Romain on 23/10/2016.
 */
app.mapPopUpInfoVisuView = Backbone.View.extend({

    el: '#osmInfo',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template);
        switch (this.model.attributes.type){
            case 1:
            case 2:
            case 3:
                var type = this.getTypeName(this.model.attributes['type']);
                var modelSend = new Backbone.Model({'key':"Type",'info':type});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
                var name;
                if (this.model.attributes['name'] == "Unnamed unit road"){
                    name = "Route sans nom";
                } else {
                    name = this.model.attributes['name'];
                }
                modelSend = new Backbone.Model({'key':"Nom",'info':name});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
                if (this.model.attributes['maxspeed']){
                    modelSend = new Backbone.Model({'key':"Limitation de vitesse",'info':this.model.attributes['maxspeed']+" km/h"});
                    new app.mapPopUpInfoVisuEachView({
                        model: modelSend
                    });
                }
                if (this.model.attributes['oneway']){
                    var result;
                    if (this.model.attributes['oneway'] == "yes" || this.model.attributes['oneway'] == -1 || this.model.attributes['oneway'] == "-1"){
                        result = "Oui";
                    } else {
                        result = "Non";
                    }
                    modelSend = new Backbone.Model({'key':"Sens unique",'info':result});
                    new app.mapPopUpInfoVisuEachView({
                        model: modelSend
                    });
                    if (this.model.attributes['oneway'] == "yes"){
                        modelSend = new Backbone.Model({'key':"Sens",'info':"De A vers B"});
                        new app.mapPopUpInfoVisuEachView({
                            model: modelSend
                        });
                    } else if (this.model.attributes['oneway'] == -1 || this.model.attributes['oneway'] == "-1"){
                        modelSend = new Backbone.Model({'key':"Sens",'info':"De B vers A"});
                        new app.mapPopUpInfoVisuEachView({
                            model: modelSend
                        });
                    }
                } else {
                    modelSend = new Backbone.Model({'key':"Sens unique",'info':"Non"});
                    new app.mapPopUpInfoVisuEachView({
                        model: modelSend
                    });
                }
                break;
            case 4:
                var type = this.getTypeName(this.model.attributes['type']);
                var modelSend = new Backbone.Model({'key':"Type",'info':type});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
                var name;
                if (this.model.attributes['name'] == "Unnamed unit road"){
                    name = "Rond point sans nom";
                } else {
                    name = this.model.attributes['name'];
                }
                modelSend = new Backbone.Model({'key':"Nom",'info':name});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
                if (this.model.attributes['maxspeed']){
                    modelSend = new Backbone.Model({'key':"Limitation de vitesse",'info':this.model.attributes['maxspeed']+" km/h"});
                    new app.mapPopUpInfoVisuEachView({
                        model: modelSend
                    });
                }
                break;
            case 5:
                var type = this.getTypeName(this.model.attributes['type']);
                var modelSend = new Backbone.Model({'key':"Type",'info':type});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
                var name;
                if (this.model.attributes['name'] == "Unnamed unit road"){
                    name = "Feu rouge sans nom";
                } else {
                    name = this.model.attributes['name'];
                }
                modelSend = new Backbone.Model({'key':"Nom",'info':name});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
                if (this.model.attributes['redlighttime']){
                    modelSend = new Backbone.Model({'key':"Durée du feu",'info':this.model.attributes['redlighttime']+" secondes"});
                    new app.mapPopUpInfoVisuEachView({
                        model: modelSend
                    });
                }
                break;
        }
        return this;
    },

    getTypeName: function(type){
        switch (type){
            case 1:
                return "Route simple";
            case 2:
                return "Route double";
            case 3:
                return "Route triple";
            case 4:
                return "Rond point";
            case 5:
                return "Feu rouge";
        }
    }

});