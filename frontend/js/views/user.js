/**
 * Created by paul on 09/10/16.
 */


app.userView = Backbone.View.extend({

    el:'#content',


    events: {
        'click #submitUser' : 'clickOnSubmitUser'
    },

    initialize: function () {
        this.render();
    },

    render:function () {
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

        if (initNom != newNom || initPrenom != newPrenom || initEmail!=newEmail){
            app.router.navBarV.model.save({
                'lastName': newNom,
                'firstName': newPrenom,
                'email': newEmail
            }, {
                success: function(model, response) {
                    app.router.navBarV.render();
                },
                wait: true // Add this
            });
        }else {
            $('#danger-text-modal').html("Aucun changement à enregistrer.");
            $('#modalError').modal('show');
        }

    }

});
