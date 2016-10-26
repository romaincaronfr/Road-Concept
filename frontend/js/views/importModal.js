/**
 * Created by Romain on 26/10/2016.
 */
app.importModalView = Backbone.View.extend({

    el: '#mapRow',

    initialize: function () {

        this.render();
    },

    render: function (){

        this.$el.append(this.template);
        $('#modalImport').modal('show');

        //return this;
    }


});