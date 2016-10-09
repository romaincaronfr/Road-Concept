/**
 * Created by Romain on 30/09/2016.
 */

app.models.mapModel = Backbone.Model.extend({
    parse: function (payload) {
        return {
            id: payload.id || "NO_ID_SERVER_IS_TOO_BAD",
            name: payload.name || "MAP_NAME",
            imageURL: payload.imageURL || "assets/img/map-default.png",
            description: payload.description || "Pas de description disponible"
        };
    }
});