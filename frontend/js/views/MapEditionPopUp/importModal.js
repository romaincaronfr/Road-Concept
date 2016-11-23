/**
 * Created by Romain on 26/10/2016.
 */
app.importModalView = Backbone.View.extend({

    el: '#mapRow',

    initialize: function () {
        this.render();
    },

    render: function (){
        console.log("render modal");
        if (!$('#modalImport').length) {
            this.$el.append(this.template);
            $('#modalImport').modal('show');
        }

        return this;
    }


});