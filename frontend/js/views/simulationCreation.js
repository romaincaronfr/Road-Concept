/**
 * Created by andreas on 12/11/16.
 */

app.simulationCreationView = Backbone.View.extend({
    el: '#content',

    events: {

    },

    initialize: function (options) {
        this.id = options.id;
        this.render();
    },

    render: function () {
        this.$el.html(this.template);
        return this;
    },

    changeID: function (id) {
        this.id = id;
        this.mapDetailsCOllection.id = id;
    }
});