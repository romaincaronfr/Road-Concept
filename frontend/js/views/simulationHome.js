/**
 * Created by Romain on 08/11/2016.
 */

app.simulationHomeView = Backbone.View.extend({

    el: '#content',
    id: null,

    events: {

    },


    initialize: function (options) {
        this.id = options.id;
        //TODO quand le backend sera prêt, afficher les simulations déjà existantes.
        //this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id: this.id});
        this.render();
    },

    render: function () {
        this.$el.html(this.template);
        return this;

    },

    changeID: function (id) {
        this.id = id;
        //this.mapDetailsCOllection.id = id;
    }
});
