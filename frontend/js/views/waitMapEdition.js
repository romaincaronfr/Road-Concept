/**
 * Created by Romain on 20/11/2016.
 */


app.waitMapEditionView = Backbone.View.extend({

    el: '#osmInfo',


    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template());
        return this;

    }
});
