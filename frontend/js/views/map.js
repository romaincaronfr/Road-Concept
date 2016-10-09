/**
 * Created by Romain on 03/10/2016.
 */
app.mapView = Backbone.View.extend({

    el: '#content',

    events: {
        'click #submitCreateMap':'clickOnCreateNewMap'
    },

    initialize: function () {
        this.mapCollection = new app.collections.mapCollection;
        var self = this;
        this.mapCollection.fetch({
            success :(function(){
            self.render();
        })
        });
        this.mapCollection.on('add',self.newElement,self);
        this.mapCollection.on('change',self.newChange,self);
        this.mapCollection.on('reset',self.newReset,self);
        this.mapCollection.on('sync',self.newSync,self);
    },

    render:function () {
        this.$el.html(this.template());
        this.mapCollection.each(function(model) {
            var mapTableView = new app.mapTableView({
                model: model
            });

            //this.$el.append(mapTableView.render().el);
        }.bind(this));
        return this;
    },

    reloadCollection:function(){
        var self = this;
        this.mapCollection.fetch({
            success :(function(){
                self.render();
            })
        });
    },

    clickOnCreateNewMap: function(){
        var self = this;
        var name = $('#mapName').val();
        var description = $('#mapDescription').val();
        var urlImg = $('#mapImgURL').val();
        var newMapModel = new app.models.mapModel({'name':name,'description':description,'imageURL':urlImg});
        newMapModel.save({
            success : (function(){
                self.mapCollection.add(newMapModel);
            }),
            error : (function(){
                console.error('error during saving new map');
            })
        });
    },

    newElement:function(element){
        console.log("new element");
        var index = this.mapCollection.indexOf(element);
        console.log('modulo : '+(index+1)%4);
        if (index == 0){
            console.log("first element");
        } else if ((index+1)%4 == 0){
            console.log('je dois ajouter une div');
        }
        new app.mapTableView({
            model: element
        });
    },

    newChange:function(element){
        console.log("change collection");
        console.log(element);
    },

    newReset:function(element){
        console.log("reset collection");
        console.log(element);
    },

    newSync:function(element){
        console.log("sync collection");
        console.log(element);
    }

});