/**
 * Created by andreas on 05/11/16.
 */
app.mapPopUpCreateRedlightsView = Backbone.View.extend({

    el: '#osmInfo',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        return this;
    }

});