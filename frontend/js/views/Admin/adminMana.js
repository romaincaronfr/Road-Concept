/**
 * Created by paul on 13/10/16.
 */

app.adminManaView = Backbone.View.extend({

    el: "#content",

    events: {
        'click #AddUser': 'clickOnAjoutUser'
    },

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template());
        return this;
    },

    clickOnAjoutUser: function () {
        var usermail = $('#emailInput').val();
        var userLname = $('#nameInput').val();
        var usertype = parseInt($('#userinput').val());
        var userPassword = $('#passwordInput').val()
        var userFname = $('#prenomInput').val();

        user = new app.models.userModel({
            email: usermail,
            lastName: userLname,
            firstName: userFname,
            password: userPassword,
            type: usertype
        });
        console.log(user);
        user.save(null,{
            success: function(){
                $('#alertAddUser').removeClass('hidden');
                $('#emailInput').val('');
                $('#nameInput').val('');
                $('#userinput').val('');
                $('#passwordInput').val('');
                $('#prenomInput').val('');
            }
        });
    },
});