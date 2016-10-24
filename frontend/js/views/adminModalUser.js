
/**
 * Created by paul on 20/10/16.
 */

app.adminModalUserView = Backbone.View.extend({

    el: '#Modal',

    initialize: function (model) {

        this.render(model);
    },

    render: function (model){

        console.log(model);
        this.$el.append(this.template(model.attributes));
        //$('#submitModifyUserM').modal();
        $('#submitModifyUserM').modal('show');

        //return this;
    }


});
