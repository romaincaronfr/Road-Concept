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
        this.$el.html(this.template());
    },

    clickOnSubmitLogin: function () {
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
                $.ajax({
                        url: Backbone.Collection.prototype.absURL + "/api/me",
                        type: "GET",
                        headers: {
                            "Content-Type": "application/json; charset=utf-8",
                        },
                        contentType: "application/json"
                    })
                    .done(function (data, textStatus, jqXHR) {
                        userModel = new app.models.userModel(data);
                        app.router.navigate('map', {trigger: true});
                    })
                    .fail(function (jqXHR, textStatus, errorThrown) {
                    })
                    .always(function () {
                        /* ... */
                    });
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
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