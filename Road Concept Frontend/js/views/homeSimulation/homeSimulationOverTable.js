/**
 * Created by Romain on 27/11/2016.
 */
app.homeSimulationOverTableView = Backbone.View.extend({

    el: '#overSimulationTable',


    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.append(this.template(this.model.attributes));
        return this;

    }
});
