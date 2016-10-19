/**
 * Created by paul on 17/10/16.
 */

app.adminUserRowView = Backbone.View.extend({

    el: '#userTable',

    initialize: function () {
        this.render();
        console.log("render Row");
    },

    render: function () {
        console.log("render Row");
        this.$el.append(this.template(this.model.attributes));
        console.log("render Row1");
        return this;
    }

});