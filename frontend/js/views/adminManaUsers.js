/**
 * Created by paul on 16/10/16.
 */

app.adminManaUsersView = Backbone.View.extend({

    el: '#content',

    events: {
        'click .affichage_mod': 'clickOnModifyUser',
        'click .remove_User': 'clickOnRemove'
    },

    initialize: function () {
        this.userCollection = new app.collections.userCollection;
        var self = this;
        this.render();
        this.userCollection.on('change', self.newChange, self);
        this.userCollection.on('reset', self.newReset, self);
        this.userCollection.on('sync', self.newSync, self);
        this.userCollection.on('destroy', self.newDestroy, self);
        this.userCollection.on('add',self.newElement,self);
        console.log("admin mana user initialize");
    },

    render: function () {
        this.$el.html(this.template());
        this.userCollection.each(function (model) {
            var adminUserRow = new app.adminUserRowView({
                model: model
            });

        });
        console.log("admin mana user render bfetch");
        this.userCollection.fetch();
        console.log("admin mana user render afetch");
        return this;

    },

    clickOnRemove: function (event) {
        console.log('click on remove');
        var id = event.currentTarget.id;
        id = id.replace('remove_User_', '');
        console.log(id);
        this.userCollection.get(id).destroy({wait: true});
        console.log('click on remove def');

    },


    clickOnModifyUser: function (event) {
        console.log("admin mana user clickOnModify bnewmodalUSer");
        var id = event.currentTarget.id;
        id = id.replace('affichage_modal_', '');
        var modalview = new app.adminModalUserView(this.userCollection.get(id));
        console.log("admin mana user clickOnModify anewmodalUSer");
    },


    newElement: function (element) {
        new app.adminUserRowView({
            model: element
        });
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
        var divName = '#user_Id_' + element.attributes.id;
        $(divName).remove();
    }
});
