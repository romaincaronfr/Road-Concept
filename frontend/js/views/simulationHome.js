/**
 * Created by Romain on 08/11/2016.
 */

app.simulationHomeView = Backbone.View.extend({

    el: '#content',

    events: {

    },


    initialize: function (options) {
        this.id = options.id;
        //TODO quand le backend sera prêt, afficher les simulations déjà existantes.
        this.simulationParamsCollection = new app.collections.simulationParamsCollection({id: this.id});
        this.render();
    },

    render: function () {
        this.$el.html(this.template);
        var self = this;
        this.simulationParamsCollection.fetch({
            success: function(){
                console.log(self.simulationParamsCollection);
            }
        });
        return this;

    },

    changeID: function (id) {
        console.log("change id = "+id);
        this.id = id;
        this.simulationParamsCollection.id = id;
    }
});
