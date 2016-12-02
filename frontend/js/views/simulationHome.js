/**
 * Created by Romain on 08/11/2016.
 */

app.simulationHomeView = Backbone.View.extend({

    el: '#content',
    simulationParamsCollection: null,

    events: {
        'click .visu_simu_button': 'goVisu',
        'click .remove_simu_button': 'goRemoveSimu',
        'click .remove_simu_confirm': 'goConfirmRemoveSimu'
    },


    initialize: function (options) {
        this.id = options.id;
        this.simulationParamsCollection = new app.collections.simulationParamsCollection({id: this.id});
        var self = this;
        //this.simulationParamsCollection.on('add', self.onAddElement, self);
        this.render();
    },

    render: function () {
        var self = this;
        this.$el.html(this.template(new Backbone.Model({"id": this.id})));
        this.simulationParamsCollection.fetch({
            success: function () {
                for (var i = 0; i < self.simulationParamsCollection.length; i++) {
                    var model = self.simulationParamsCollection.models[i];
                    if (!model.get("finish")) {
                        if (!$('#noInProgressSimulation').hasClass('hidden')) {
                            $('#noInProgressSimulation').addClass('hidden');
                            $('#tableInProgressSimulation').removeClass('hidden');
                        }
                        new app.homeSimulationInProgressTableView({
                            model: model
                        });

                    } else {
                        if (!$('#noOverSimulation').hasClass('hidden')) {
                            $('#noOverSimulation').addClass('hidden');
                            $('#tableOverSimulation').removeClass('hidden');
                        }
                        new app.homeSimulationOverTableView({
                            model: model
                        });
                    }
                }
            }
        });
        return this;

    },

    changeID: function (id) {
        this.id = id;
        this.simulationParamsCollection.id = id;
    },

    goVisu: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('visu_simu_button_', '');
        var model = this.simulationParamsCollection.get(id);
        app.router.navigate('simmap/' + id + '/s/' + model.attributes.samplingRate + '/d/' + model.attributes.departureLivingS + '/m/' + this.id, {trigger: true});
    },

    goRemoveSimu: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('remove_simu_button_', '');
        var model = this.simulationParamsCollection.get(id);
        new app.modalRemoveSimuView({
            model: model
        });
    },

    goConfirmRemoveSimu: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('confirmRemoveSimu_', '');
        var model = this.simulationParamsCollection.get(id);
        model.destroy({
            success: function () {
                var divName = '#sim_' + id;
                $(divName).remove();
                $('#modalRemoveSimu').modal('hide');
            }
        });
    }
});
