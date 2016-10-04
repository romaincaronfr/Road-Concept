/**
 * Created by Romain on 03/10/2016.
 */
app.mapView = Backbone.View.extend({

    el: '#content',

    initialize: function () {
        this.mapCollection = new app.collections.mapCollection;
        var self = this;
        this.mapCollection.fetch({
            success :(function(){
            self.render();
        })
        });
        this.mapCollection.on('add',self.newElement,self);
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

    newElement:function(element){
        console.log("new element");
        new app.mapTableView({
            model: element
        });
    }

});