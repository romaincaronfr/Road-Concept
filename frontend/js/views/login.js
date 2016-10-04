/**
 * Created by Romain on 28/09/2016.
 */
app.loginView = Backbone.View.extend({

    el: '#content',

    initialize: function () {
        this.render();
    },

    events:{
        'click #submitLogin':'clickOnSubmitLogin'
    },

    render:function () {
        console.log("render login");
        this.$el.html(this.template());
    },

    clickOnSubmitLogin:function (){
        console.log("click on submit");
        $.ajax({
                url: Backbone.Collection.prototype.absURL+"/login",
                type: "POST",
                headers: {
                    "Content-Type": "application/json; charset=utf-8",
                },
                contentType: "application/json",
                data: JSON.stringify({
                    "email": $('#exampleInputEmail1').val(),
                    "password": $('#exampleInputPassword1').val()
                })
            })
            .done(function (data, textStatus, jqXHR) {
                console.log("HTTP Request Succeeded: " + jqXHR.status);
                app.router.navigate('', { trigger: true });
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.log("HTTP Request Failed");
                $('#alertLogin').removeClass('hidden');
            })
            .always(function () {
                /* ... */
            });
    }

});