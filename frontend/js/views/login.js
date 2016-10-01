/**
 * Created by Romain on 28/09/2016.
 */
app.loginView = Backbone.View.extend({

    event:{
        "click #submitLogin":"clickOnSubmitLogin"
    },

    render:function () {
        console.log("render login");
        this.$el.html(this.template());
        return this;
    },

    clickOnSubmitLogin:function (){
        console.log("click on submit");
    }

});