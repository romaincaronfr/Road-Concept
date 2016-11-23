/**
 * Created by Romain on 01/11/2016.
 */
app.modalRemoveMapView = Backbone.View.extend({

    el: '#modalRemoveMap',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        $('#modalRemoveMap').modal('show');
        return this;
    }

});