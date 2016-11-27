/**
 * Created by paul on 16/10/16.
 */

app.adminManaUsersView = Backbone.View.extend({

    el: '#content',
    myuserId: null,

    events: {
        'click .affichage_mod': 'clickOnModifyUser',
        'click .remove_User': 'clickOnRemove',
        'click .modify_User': 'clickOnValidModifyUser',
        'click #buttonModalInfoCancel': 'clickOnCancelDelete',
    },

    initialize: function () {
        this.userCollection = new app.collections.userCollection;
        var self = this;
        $.ajax({
            url: Backbone.Collection.prototype.absURL + "/api/me",
            type: "GET",
            headers: {
                "Content-Type": "application/json; charset=utf-8",
            },
            contentType: "application/json"
        })
            .done(function (data, textStatus, jqXHR) {
                console.log(data.id);
                self.myuserId = data.id;
                self.render();
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.log('fail');
            })
            .always(function () {
                /* ... */
            });
        this.userCollection.on('change', self.newChange, self);
        this.userCollection.on('reset', self.newReset, self);
        this.userCollection.on('sync', self.newSync, self);
        this.userCollection.on('destroy', self.newDestroy, self);
        this.userCollection.on('add', self.newElement, self);
    },

    render: function () {
        this.$el.html(this.template());
        var self = this;
        this.userCollection.each(function (model) {
            if (model.attributes.id != self.myuserId) {
                console.log("if render ok. ID = " + model.attributes.id);
                var adminUserRow = new app.adminUserRowView({
                    model: model
                });
            }
        });
        this.userCollection.fetch();
        return this;

    },

    clickOnRemove: function (event) {
        console.log('click on remove');
        var id = event.currentTarget.id;
        id = id.replace('remove_User_', '');

        this.userCollection.get(id).destroy({wait: true});

        $('#adminManaUsersViewSuccess').html("Suppression effectuée.");
        $('#adminManaUsersViewSuccess').removeClass("hidden");
    },


    clickOnModifyUser: function (event) {
        console.log("admin mana user clickOnModify bnewmodalUSer");
        var id = event.currentTarget.id;
        id = id.replace('affichage_modal_', '');
        var modalview = new app.adminModalUserView(this.userCollection.get(id));
        console.log("admin mana user clickOnModify anewmodalUSer");
    },

    clickOnValidModifyUser: function (event) {
        console.log('click on modify');
        var firstname = $('#firstName').val();
        var lastname = $('#lastName').val();
        var email = $('#email').val();
        var type = parseInt($('#type').val());
        var id = event.currentTarget.id;
        console.log(id);
        id = id.replace('modify_', '');
        console.log(id);
        console.log(event.currentTarget.id);

        if(!firstname || !lastname || !email || !type){
            $('#adminModalUserViewDanger').html('Un ou plusieurs des champs sont vide, merci de le(s) remplir.');
            $('#adminModalUserViewDanger').removeClass('hidden');
        } else {

            if(this.validationEmail(email) == true){
                console.log('email ok :'+email);
                this.userCollection.get(id).set({'firstName': firstname, 'lastName': lastname, 'email': email, 'type': type});
                var model = this.userCollection.get(id);

                model.save(null, {
                    success: function () {
                        $('#submitModifyUserM').modal('hide');
                        $('#firtName_' + model.attributes.id).html(model.attributes.firstName);
                        $('#lastName_' + model.attributes.id).html(model.attributes.lastName);
                        $('#email_' + model.attributes.id).html(model.attributes.email);
                        $('#type_' + model.attributes.id).html(model.attributes.type);
                    }
                });
                $('#adminManaUsersViewSuccess').html('Les modifications ont été effectuées.');
                $('#adminManaUsersViewSuccess').removeClass('hidden');
            } else {
                console.log("bug");
                $('#adminModalUserViewDanger').html('Votre email n\'est pas au bon format. Veuillez modifier ce champ.');
                $('#adminModalUserViewDanger').removeClass('hidden');
            }


        }

    },

    clickOnCancelDelete: function () {
        console.log('canceldelete');
    },

    newElement: function (element) {
        if (element.attributes.id != this.myuserId) {
            new app.adminUserRowView({
                model: element
            });
        }
    },

    newChange: function (element) {
        console.log(element);
    },

    newReset: function () {
        this.$el.html(this.template());
    },

    newSync: function (element) {
        console.log(element);
    },

    newDestroy: function (element) {
        console.log(element.attributes.id);
        console.log("remove");
        var divName = '#user_Id_' + element.attributes.id;
        $(divName).remove();
    },

    validationEmail: function (newEmail) {
        if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(newEmail)) {
            return (true)
        }
        return (false)
    },
});
