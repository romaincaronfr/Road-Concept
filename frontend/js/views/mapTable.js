/**
 * Created by Romain on 03/10/2016.
 */
app.mapTableView = Backbone.View.extend({

    el: '#mapTable',
    
    initialize: function () {
        this.render();
    },

    render:function () {
        this.$el.append(this.template(this.model.attributes));
        return this;
    }

});