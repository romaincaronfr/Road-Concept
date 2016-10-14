/**
 * Created by Romain on 14/10/2016.
 */

app.adminPanelOptionsView = Backbone.View.extend({

    el: '#menu-dropdown',

    initialize: function () {
        this.render();
    },

    render:function () {
        this.$el.append(this.template());
        return this;
    }

});