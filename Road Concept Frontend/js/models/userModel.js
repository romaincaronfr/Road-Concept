/**
 * Created by Romain on 09/10/2016.
 */
app.models.userModel = Backbone.Model.extend({
    urlRoot: function () {
        return this.absURL + '/api/users';
    }
});