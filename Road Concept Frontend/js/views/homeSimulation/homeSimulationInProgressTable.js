/**
 * Created by Romain on 27/11/2016.
 */
app.homeSimulationInProgressTableView = Backbone.View.extend({

    el: '#inProgressSimulationTable',


    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.append(this.template(this.model.attributes));
        return this;

    }
});
