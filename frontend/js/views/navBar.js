/**
 * Created by Romain on 04/10/2016.
 */
app.navBarView = Backbone.View.extend({

    el: '#headerBar',

    model: null,

    initialize: function (redirectToUser) {
        var self = this;
        this.getMe(redirectToUser,this);
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        return this;
    },

    cleanHTML: function () {
        $(this.el).empty();
    },

    checkUserModelBeforeMyUser: function () {
        var self = this;
        if (!this.model) {
            this.getMe(true,self);
        }
        else {
            app.router.userV = new app.userView();
        }
    },

    getMe:function(redirectToUser,self){
        $.ajax({
                url: Backbone.Collection.prototype.absURL + "/api/me",
                type: "GET",
                headers: {
                    "Content-Type": "application/json; charset=utf-8",
                },
                contentType: "application/json"
            })
            .done(function (data, textStatus, jqXHR) {
                self.model = new app.models.userModel(data);
                self.render();
                if (redirectToUser){
                    app.router.userV = new app.userView();
                }
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                //TODO rediriger qu'en cas d'erreur 401, merci :)
                console.log("HTTP Request Failed : /api/me");
                app.router.navigate('login', { trigger: true });
            })
            .always(function () {
                /* ... */
            });
    }

});