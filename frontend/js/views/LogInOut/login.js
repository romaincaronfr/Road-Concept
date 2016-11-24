/**
 * Created by Romain on 28/09/2016.
 */
app.loginView = Backbone.View.extend({

    el: '#content',

    initialize: function () {
        this.render();
    },

    events: {
        'click #submitLogin': 'clickOnSubmitLogin',
        'keypress #exampleInputPassword1': 'pressEnter',
        'keypress #exampleInputEmail1': 'pressEnter'
    },

    render: function () {
        console.log("render login");
        this.$el.html(this.template());
    },

    clickOnSubmitLogin: function () {
        console.log("click on submit");
        $('#formDiv').addClass('hidden');
        $('#waitDiv').removeClass('hidden');
        $.ajax({
                url: Backbone.Collection.prototype.absURL + "/login",
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
                $.ajax({
                        url: Backbone.Collection.prototype.absURL + "/api/me",
                        type: "GET",
                        headers: {
                            "Content-Type": "application/json; charset=utf-8",
                        },
                        contentType: "application/json"
                    })
                    .done(function (data, textStatus, jqXHR) {
                        console.log("HTTP Request Succeeded: " + jqXHR.status);
                        userModel = new app.models.userModel(data);
                        console.log(userModel);
                        app.router.navigate('map', {trigger: true});
                    })
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        console.log("HTTP Request Failed : /api/me");
                    })
                    .always(function () {
                        /* ... */
                    });
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.log("HTTP Request Failed");
                $('#alertLogin').removeClass('hidden');
                $('#formDiv').removeClass('hidden');
                $('#waitDiv').addClass('hidden');
            })
            .always(function () {
                /* ... */
            });
    },

    pressEnter: function(event){
        if(event.which === 13 && $('#exampleInputEmail1').val() != '' && $('#exampleInputPassword1').val() !=''){
            this.clickOnSubmitLogin();
        }
    }

});