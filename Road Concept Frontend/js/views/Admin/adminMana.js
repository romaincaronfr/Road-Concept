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

        if (!usermail || !userLname || !usertype || !userPassword || !userFname) {
            $('#adminManaViewDanger').html('Certains de vos champs est/sont vide. Merci de le(s) remplir.');
            $('#adminManaViewDanger').removeClass('hidden');
            $('#adminManaViewSuccess').addClass('hidden');

        } else {

            if (this.validationEmail(usermail) == false) {
                $('#adminManaViewDanger').html('Votre email n\'est pas au bon format. Merci de le modifier.');
                $('#adminManaViewDanger').removeClass('hidden');
                $('#adminManaViewSuccess').addClass('hidden');
            } else {
                user = new app.models.userModel({
                    email: usermail,
                    lastName: userLname,
                    firstName: userFname,
                    password: userPassword,
                    type: usertype
                });
                user.save(null, {
                    success: function () {
                        $('#adminManaViewSuccess').html('L\'utilisateur a bien été ajouté.');
                        $('#adminManaViewSuccess').removeClass('hidden');
                        $('#adminManaViewDanger').addClass('hidden');
                        $('#emailInput').val('');
                        $('#nameInput').val('');
                        $('#userinput').val('');
                        $('#passwordInput').val('');
                        $('#prenomInput').val('');
                    }
                });
            }
        }


    },

    validationEmail: function (newEmail) {
        if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(newEmail)) {
            return (true)
        }
        return (false)
    },
});