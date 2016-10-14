/**
 * Created by Romain on 14/10/2016.
 */
app.logoutView = Backbone.View.extend({

    el:'#content',


    initialize: function () {
        this.render();
    },

    render:function () {
        this.$el.html(this.template());
        $.ajax({
                url: Backbone.Collection.prototype.absURL+"/api/logout",
                type: "POST",
                headers: {
                    "Content-Type": "application/json; charset=utf-8",
                },
                contentType: "application/json"
            })
            .done(function (data, textStatus, jqXHR) {
                $('#waitLogout').addClass('hidden');
                $('#alertLogout').removeClass('hidden');
                app.router.checkAndDestroyNavbar();
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.log("HTTP Request Failed : /api/logout");
                $('#danger-text-modal').html("Erreur lors de la déconnexion. Merci de réessayer.");
                $('#modalError').modal('show');
            })
            .always(function () {
                /* ... */
            });
    }
});
