/**
 * Created by andreas on 05/11/16.
 */
app.mapPopUpCreateRoadsView = Backbone.View.extend({

    el: '#osmInfo',

    initialize: function () {
        this.render();
    },

    render: function () {
        this.$el.html(this.template(this.model.attributes));
        $('#selectTypeRoad').val(this.model.attributes.type);
        if (this.model.attributes.oneway == "yes" || this.model.attributes.oneway == "-1" || this.model.attributes.oneway == -1){
            console.log("yes");
            console.log(this.model.attributes.oneway);
            $('#onwayRoad').val("yes");
            $('#wayDiv').removeClass('hidden');
            if (this.model.attributes.oneway == "yes"){
                $('#wayRoad').val("yes");
            } else {
                $('#wayRoad').val("-1");
            }
        } else {
            console.log(this.model.attributes.oneway);
            $('#onwayRoad').val("no");
        }
        return this;
    }

});