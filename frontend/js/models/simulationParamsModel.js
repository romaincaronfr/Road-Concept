/**
 * Created by Romain on 23/11/2016.
 */

app.models.simulationParamsModel = Backbone.Model.extend({

    urlRoot: function() {
        // Backbone adds the model id automatically
        return this.collection.url();
    }

});