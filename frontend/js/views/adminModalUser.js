
/**
 * Created by paul on 20/10/16.
 */

app.adminModalUserView = Backbone.View.extend({

    el: '#modal',

    events: {
        'click .modify_User': 'clickOnModifyUser'
    },

    initialize: function (model) {

        this.render(model);
    },

    render: function (model){

        console.log(model);
        this.$el.append(this.template(model.attributes));
        //$('#submitModifyUserM').modal();
        $('#submitModifyUserM').modal('show');

        //return this;
    },

    clickOnModifyUser: function (event) {

        var firstname = $('#firstname').val();
        var lastname = $('#lastName').val();
        var email = $('#email').val();
        var type = $('#type').val();
        var id = event.currentTarget.id;
        console.log(id);
        id = id.replace('modify_','');
        console.log(id);
        console.log(event.currentTarget.id);
        this.userCollection.get(id).set({'firstName': firstname, 'lastName': lastname, 'email':email, 'type':type});
        this.userCollection.get(id).save();
    }

});
