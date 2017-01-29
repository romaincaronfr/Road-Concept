/**
 * Created by andreas on 05/01/17.
 */
app.models.carSimulationStats = Backbone.Model.extend({
    initialize: function (option) {
        this.id = option.id;
        this.vehicle_id = option.vehicle_id;
    },
    url: function () {
        return this.absURL + '/api/simulations/' + this.id + '/vehicles/' + this.vehicle_id + '/statistics';
    }
});