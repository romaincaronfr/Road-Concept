/**
 * Created by paul on 09/10/16.
 */


app.userView = Backbone.View.extend({

    el: '#content',
    users: null,


    events: {
        'click #submitUser': 'clickOnSubmitUser'
    },

    initialize: function () {
        this.render();
        var self = this;

        $.ajax({
            url: Backbone.Collection.prototype.absURL + "/api/users",
            type: "GET",
            headers: {
                "Content-Type": "application/json; charset=utf-8",
            },
            contentType: "application/json"
        })
            .done(function (data, textStatus, jqXHR) {
                self.users = data;
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.log('fail');
            });
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

                    if (this.emailContainOnceInDatabase(newEmail) == true) {
                        app.router.navBarV.model.save({
                            'lastName': newNom,
                            'firstName': newPrenom,
                            'email': newEmail
                        }, {
                            success: function (model, response) {
                                app.router.navBarV.render();
                            },
                            wait: true // Add this
                        });
                        $('#info-text-modal').html("Votre compte a bien été mis à jour.");
                        $('#modalInfo').modal('show');
                    } else {
                        $('#info-text-modal').html("L'email que vous avez renseigné est déjà utilisé. Merci de bien vouloir le changer.");
                        $('#modalInfo').modal('show');
                    }
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

    emailContainOnceInDatabase: function (newEmail) {
        var once = true;
        for (var i = 0; i < this.users.length; i++) {
            console.log(this.users[i].email);
            if (this.users[i].email == newEmail) {
                once = false;
            }
        }
        return once;
    }

});
