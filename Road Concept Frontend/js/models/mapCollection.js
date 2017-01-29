/**
 * Created by Romain on 30/09/2016.
 */

app.collections.mapCollection = Backbone.Collection.extend({
    model: app.models.mapModel,
    url: function () {
        return this.absURL + '/api/maps';
    }
});