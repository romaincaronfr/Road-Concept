/**
 * Created by Romain on 16/10/2016.
 */
app.models.mapDetailsModel = Backbone.Model.extend({

    getCoordinates: function(){
        return this.attributes.geometry.coordinates;
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
    }


});