/**
 * Created by Romain on 16/10/2016.
 */
app.collections.mapDetailsCollection = Backbone.Collection.extend({
    model: app.models.mapDetailsModel,
    initialize: function(options) {
        this.id = options.id;
    },
    url: function () {
        return this.absURL + '/api/maps/' + this.id;
    }
});