/**
 * Created by Romain on 28/09/2016.
 */
directory.loginView = Backbone.View.extend({

    render:function () {
        console.log("render login");
        this.$el.html(this.template());
        return this;
    }

});