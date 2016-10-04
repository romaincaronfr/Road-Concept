/**
 * Created by Romain on 04/10/2016.
 */
app.navBarView = Backbone.View.extend({

    el: '#headerBar',

    initialize: function () {
        this.render();
    },

    render:function () {
        this.$el.html(this.template());
        return this;
    }

});