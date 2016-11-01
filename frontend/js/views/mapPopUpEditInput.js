/**
 * Created by Romain on 01/11/2016.
 */
app.mapPopUpInfoEditInputView = Backbone.View.extend({

    el: '#formmodif',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.append(this.template(this.model.attributes));
        return this;
    }

});