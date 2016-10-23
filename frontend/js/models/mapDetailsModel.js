/**
 * Created by Romain on 16/10/2016.
 */
app.models.mapDetailsModel = Backbone.Model.extend({

    parse: function(response){
        var newResponse = {};
        newResponse.geometry = new Backbone.Model(response.geometry);
        _.keys(response.properties).forEach(function(key){
            newResponse[key] = response.properties[key];
        });
        return newResponse;
    },

    toGeoJSON: function(){
        console.log(this);
        return {
            "type": "Feature",
            "properties": _.omit(this.toJSON(), 'geometry'),
            "geometry": this.get('geometry').toJSON()
        };
    }

});