/**
 * Created by Romain on 16/10/2016.
 */
app.models.mapDetailsModel = Backbone.Model.extend({

    urlRoot: function() {
        // Backbone adds the model id automatically
        return this.collection.url()+"/features";
    },

    parse: function(response){
        var newResponse = {};
        newResponse.geometry = new Backbone.Model(response.geometry);
        _.keys(response.properties).forEach(function(key){
            newResponse[key] = response.properties[key];
        });
        return newResponse;
    },

    toGeoJSON: function(){
        return {
            "type": "Feature",
            "id": this.attributes.id,
            "properties": _.omit(this.attributes, 'geometry'),
            "geometry": this.get('geometry').toJSON()
        };
    },

    toJSON: function(){
        return {
            "type": "Feature",
            "properties": _.omit(this.attributes, 'geometry'),
            "geometry": this.get('geometry').toJSON()
        };
    }

});