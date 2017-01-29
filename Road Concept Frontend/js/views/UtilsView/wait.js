/**
 * Created by Romain on 14/10/2016.
 */

app.waitView = Backbone.View.extend({

    el: '#content',


    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template());
        return this;

    }
});
