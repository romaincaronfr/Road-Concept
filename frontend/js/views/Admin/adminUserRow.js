/**
 * Created by paul on 17/10/16.
 */

app.adminUserRowView = Backbone.View.extend({

    el: '#userTable',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.append(this.template(this.model.attributes));
        return this;
    }

});