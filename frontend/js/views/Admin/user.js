/**
 * Created by paul on 09/10/16.
 */


app.userView = Backbone.View.extend({

    el: '#content',

    events: {
        'click #submitUser': 'clickOnSubmitUser'
    },

    initialize: function () {
        this.render();

    },

    render: function () {
        this.$el.html(this.template(app.router.navBarV.model.attributes));
        return this;

    },

    clickOnSubmitUser: function () {
        var initEmail = app.router.navBarV.model.get('email');
        var initNom = app.router.navBarV.model.get('lastName');
        var initPrenom = app.router.navBarV.model.get('firstName');
        var newEmail = $('#emailInput').val();
        var newNom = $('#nameInput').val();
        var newPrenom = $('#prenomInput').val();

        if (newEmail == "" || newNom == "" || newPrenom == "") {

            $('#info-text-modal').html("Certains de vos champs n'ont pas été indiqué. Merci de le(s) remplir.");
            $('#modalInfo').modal('show');

        } else {
            if (initNom != newNom || initPrenom != newPrenom || initEmail != newEmail) {

                if (this.validationEmail(newEmail) == true) {

                        app.router.navBarV.model.save({
                            'lastName': newNom,
                            'firstName': newPrenom,
                            'email': newEmail
                        }, {
                            success: function (model, response) {
                                app.router.navBarV.render();
                                $('#info-text-modal').html("Votre compte a bien été mis à jour.");
                                $('#modalInfo').modal('show');
                            },
                            wait: true // Add this
                        });
                } else {
                    $('#info-text-modal').html("Votre adresse email n'est pas au bon format.");
                    $('#modalInfo').modal('show');
                }
            } else {
                $('#info-text-modal').html("Aucun changement à enregistrer.");
                $('#modalInfo').modal('show');
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
