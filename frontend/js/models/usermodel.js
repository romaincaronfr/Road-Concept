/**
 * Created by Romain on 09/10/2016.
 */
app.models.userModel = Backbone.Model.extend({
    url: function () {
        return this.absURL + '/api/users';
    }
});