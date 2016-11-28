/**
 * Created by Romain on 08/11/2016.
 */

app.simulationHomeView = Backbone.View.extend({

    el: '#content',
    simulationParamsCollection: null,

    events: {
        'click .visu_simu_button': 'goVisu'
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
        /*if (!model.get("finish")){
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
         }*/

        //TODO : supprimer cette partie quand le backend sera ok avec le false
        $('#noOverSimulation').addClass('hidden');
        $('#tableOverSimulation').removeClass('hidden');
        new app.homeSimulationOverTableView({
            model: model
        });
    },

    goVisu: function (event) {
        console.log('click on visualiser');
        var id = event.currentTarget.id;
        id = id.replace('visu_simu_button_', '');
        console.log(id);
        console.log(this.simulationParamsCollection);
        var model = this.simulationParamsCollection.get(id);
        console.log(model);
        app.router.navigate('simmap/'+id+'/s/'+model.attributes.samplingRate+'/d/'+model.attributes.departureLivingS, {trigger: true});
    }
});
