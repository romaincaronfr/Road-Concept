/**
 * Created by andreas on 19/11/16.
 */
app.mapPopUpCreateWorkZoneView = Backbone.View.extend({

    el: '#osmInfo',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        return this;
    }
});