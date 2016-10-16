/**
 * Created by Romain on 16/10/2016.
 */
app.mapDetailsPageView = Backbone.View.extend({

    el: '#content',


    initialize: function (options) {
        this.id = options.id;
        this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id:this.id});
        this.render();
        var self = this;
        this.mapDetailsCOllection.on('add', self.onAddElement, self);
        this.mapDetailsCOllection.fetch();
    },

    render: function () {
        this.$el.html(this.template());
        return this;

    },

    onAddElement: function(element){
        console.log('add element');
        console.log(element.getCoordinates());
        console.log(element.getGeometryType());
        console.log(element.getName());
        console.log(element.getBridgeProperties());
        console.log(element.getMaxSpeedProperties());
        console.log(element.getTypeProperties());
        console.log(element.getRedLightTimeProperties());
        this.$el.append(element.attributes.toString());
    }
});
