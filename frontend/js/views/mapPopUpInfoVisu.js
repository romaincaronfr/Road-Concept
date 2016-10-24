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
        for(key in this.model.attributes) {
            if (key != "bridge" && key != "id" && key != "geometry" && this.model.attributes[key] ){
                console.log(key+" = "+this.model.attributes[key]);
                var modelSend = new Backbone.Model({'key':key.charAt(0).toUpperCase()+key.slice(1),'info':this.model.attributes[key]});
                new app.mapPopUpInfoVisuEachView({
                    model: modelSend
                });
            }
        }
        return this;
    }

});