/**
 * Created by paul on 09/10/16.
 */


app.userInfoView = Backbone.View.extend({

    el:'#UserTable',

    initialize: function () {
        this.render();
    },

    render:function () {
        console.log("pouet");

        this.$el.html(this.template());

        var userInfoView = new app.userInfoView({
            model: email
        });
        
        this.$el.append(this.template(this.model.attributes));
        return this;
    }
});
