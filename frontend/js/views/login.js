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
    }

});