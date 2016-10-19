/**
 * Created by paul on 17/10/16.
 */

app.collections.userCollection = Backbone.Collection.extend({
    model: app.models.userModel,
    url: function () {
        return this.absURL + '/api/users';
    }
});