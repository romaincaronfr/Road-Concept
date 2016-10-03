/**
 * Created by Romain on 03/10/2016.
 */
app.mapView = Backbone.View.extend({

    el: '#content',

    initialize: function () {
        this.mapCollection = new app.collections.mapCollection;
        var self = this;
        this.mapCollection.fetch().done(function(){
            self.render();
        });
    },

    render:function () {
        this.$el.html(this.template());
        this.mapCollection.each(function(model) {
            console.log(model);
            var mapTableView = new app.mapTableView({
                model: model
            });

            //this.$el.append(mapTableView.render().el);
        }.bind(this));
        return this;
    }

});