/**
 * Created by Romain on 16/10/2016.
 */
app.models.mapDetailsModel = Backbone.Model.extend({


    getGPSCoordinates: function(){
        if (this.attributes.geometry.coordinates.length == 1){
            return this.attributes.geometry.coordinates[0];
        } else {
            return this.attributes.geometry.coordinates;
        }
    },

    getGeometryType : function(){
        return this.attributes.geometry.type;
    },

    getName : function(){
        return this.attributes.properties.name;
    },

    getBridgeProperties: function(){
        if (this.attributes.properties.bridge == "false"){
            return false;
        }else {
            return true;
        }
    },

    getMaxSpeedProperties: function(){
        return this.attributes.properties.maxSpeed;
    },

    getTypeProperties: function(){
        return this.attributes.properties.type;
    },

    getRedLightTimeProperties: function(){
        return this.attributes.properties.redLightTime;
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
            "properties": _.omit(this.toJSON(), 'geometry'),
            "geometry": this.get('geometry').toJSON()
        };
    }


});