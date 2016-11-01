/**
 * Created by Romain on 01/11/2016.
 */
app.mapPopUpEditRedlightsView = Backbone.View.extend({

    el: '#osmInfo',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        return this;
    }

});