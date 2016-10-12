/**
 * Created by paul on 09/10/16.
 */


app.userView = Backbone.View.extend({

    el:'#content',


    events: {
        'click #submitMajemail' : 'clickOnMajemail',
        'click #submitMajfirst' : 'clicOnmajfirst',
        'click #submitMajlast' : 'clicOnmajlast'
    },

    initialize: function () {
        this.render();
    },

    render:function () {
        this.$el.append(this.template(userModel.attribute));
        return this;

    },

    clickOnMajemail: function () {
        var newemail = $('#user_email').val();
        userModel.set('email',newemail);
        userModel.save();
    },

    clicOnmajfirst:function () {
        var newfirstname = $('#firstname').val();
        userModel.set('firstname',newfirstname);
        userModel.save();
    },

    clicOnmajlast:function(){
        var newlastname = $('#lastname').val();
        userModel.set('lastname',newlastname);
        userModel.save();
    }

});
