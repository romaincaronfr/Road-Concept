/**
 * Created by anais on 01/11/16.
 */
app.collections.mapDetailsCollectionSimulation = Backbone.Collection.extend({
    model: app.models.mapDetailsModel,
    initialize: function(option) {
        this.id = option.id;
        this.timestamp = option.timestamp;
    },
    url: function () {
        //return this.absURL + '/api/maps/' + this.id;
        return this.absURL + '/api/simulations/'+this.id+'/results?timestamp='+this.timestamp;
    },
    parse: function(response){
        return response.features;
    },
    toGeoJSON: function(){
        var features = [];
        this.models.forEach(function(model){
            var feature = model.toGeoJSON();
            if (! _.isEmpty(feature.geometry)) {
                features.push(feature);
            }
        });
        return {
            'type': 'FeatureCollection',
            'features': features
        };
    }
});