/**
 * Created by andreas on 20/11/16.
 */
app.mapPopUpCreateHabitationZoneView = Backbone.View.extend({

    el: '#osmInfo',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        return this;
    }
});