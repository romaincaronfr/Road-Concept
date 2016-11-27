/**
 * Created by Romain on 08/11/2016.
 */

app.simulationHomeView = Backbone.View.extend({

    el: '#content',

    events: {

    },


    initialize: function (options) {
        this.id = options.id;
        this.simulationParamsCollection = new app.collections.simulationParamsCollection({id: this.id});
        var self = this;
        this.simulationParamsCollection.on('add', self.onAddElement, self);
        this.render();
    },

    render: function () {
        this.$el.html(this.template(new Backbone.Model({"id":this.id})));
        this.simulationParamsCollection.fetch();
        return this;

    },

    changeID: function (id) {
        console.log("change id = "+id);
        this.id = id;
        this.simulationParamsCollection.id = id;
    },

    onAddElement: function(model) {
        console.log('on add element');
        if (!model.get("finish")){
            if (!$('#noInProgressSimulation').hasClass('hidden')){
                $('#noInProgressSimulation').addClass('hidden');
                $('#tableInProgressSimulation').removeClass('hidden');
            }
            new app.homeSimulationInProgressTableView({
                model: model
            });

        } else {
            if (!$('#noOverSimulation').hasClass('hidden')){
                $('#noOverSimulation').addClass('hidden');
                $('#tableOverSimulation').removeClass('hidden');
            }
            new app.homeSimulationOverTableView({
                model: model
            });
        }
    }
});
