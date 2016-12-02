/**
 * Created by Romain on 23/11/2016.
 */
app.collections.simulationParamsCollection = Backbone.Collection.extend({
    model: app.models.simulationParamsModel,
    initialize: function (options) {
        this.id = options.id;
    },
    url: function () {
        return this.absURL + '/api/maps/' + this.id + '/simulations';
    }
});