/**
 * Created by Romain on 02/12/2016.
 */
//modalRemoveSimuView

app.modalRemoveSimuView = Backbone.View.extend({

    el: '#modalRemoveSimu',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        $('#modalRemoveSimu').modal('show');
        return this;
    }

});