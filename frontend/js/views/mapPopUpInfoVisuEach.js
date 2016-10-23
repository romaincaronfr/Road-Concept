/**
 * Created by Romain on 23/10/2016.
 */
app.mapPopUpInfoVisuEachView = Backbone.View.extend({

    el: '#listInfoMap',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.append(this.template(this.model.attributes));
        return this;
    }

});