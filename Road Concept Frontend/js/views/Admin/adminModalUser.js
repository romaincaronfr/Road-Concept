/**
 * Created by paul on 20/10/16.
 */

app.adminModalUserView = Backbone.View.extend({

    el: '#Modal',

    initialize: function (model) {

        this.render(model);
    },

    render: function (model) {
        this.$el.html(this.template(model.attributes));
        //$('#submitModifyUserM').modal();
        $('#type').val(model.attributes.type);
        $('#submitModifyUserM').modal('show');


        //return this;
    }


});
