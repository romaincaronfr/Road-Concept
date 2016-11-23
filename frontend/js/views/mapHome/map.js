/**
 * Created by Romain on 03/10/2016.
 */
app.mapView = Backbone.View.extend({

    el: '#content',

    events: {
        'click #submitCreateMap': 'clickOnCreateNewMap',
        'click .remove_map': 'clickOnRemove',
        'click .print_map': 'clickOnPrintMap',
        'click .remove_map_confirm': 'clickOnremove_map_confirm'
    },

    initialize: function () {
        this.mapCollection = new app.collections.mapCollection;
        var self = this;
        this.render();
        this.mapCollection.on('add', self.newElement, self);
        this.mapCollection.on('change', self.newChange, self);
        this.mapCollection.on('reset', self.newReset, self);
        this.mapCollection.on('sync', self.newSync, self);
        this.mapCollection.on('destroy', self.newDestroy, self);
        //this.mapCollection.fetch();
    },

    render: function () {
        this.$el.html(this.template());
        this.mapCollection.each(function (model) {
            var mapTableView = new app.mapTableView({
                model: model
            });

            //this.$el.append(mapTableView.render().el);
        });
        this.mapCollection.fetch();
        return this;
    },

    clickOnCreateNewMap: function () {
        var self = this;
        $('#submitCreateMap').prop("disabled", true);
        var name = $('#mapName').val();
        var description = $('#mapDescription').val();
        var urlImg = $('#mapImgURL').val();
        var newMapModel = new app.models.mapModel({'name': name, 'description': description, 'imageURL': urlImg});
        newMapModel.save(null, {
            success: (function () {
                console.log('success add');
                $('#mapName').val('');
                $('#mapDescription').val('');
                $('#mapImgURL').val('');
                $('#submitCreateMap').prop("disabled", false);
                $('#modalAddMap').modal('hide');
                self.mapCollection.add(newMapModel);
            }),
            error: (function () {
                $('#submitCreateMap').prop("disabled", false);
                console.error('error during saving new map');
            })
        });
    },

    clickOnPrintMap: function(event){
        var id = event.currentTarget.id;
        id = id.replace('afficher_', '');
        app.router.navigate('map/'+id, {trigger: true});
    },

    newElement: function (element) {
        console.log("new element");
        new app.mapTableView({
            model: element
        });
    },

    newChange: function (element) {
        console.log("change collection");
        console.log(element);
    },

    newReset: function () {
        console.log("reset collection");
        this.$el.html(this.template());
    },

    newSync: function (element) {
        console.log("sync collection");
        console.log(element);
    },

    newDestroy: function (element) {
        console.log('destroy');
        console.log(element);
        var divName = '#div_mapId_' + element.attributes.id;
        $(divName).remove();
        $('#modalRemoveMap').modal('hide');
    },

    clickOnRemove: function (event) {
        console.log('click on remove');
        var id = event.currentTarget.id;
        id = id.replace('remove_', '');
        var model = this.mapCollection.get(id);
        new app.modalRemoveMapView({
            model: model
        });
    },

    clickOnremove_map_confirm: function(event){
        var id = event.currentTarget.id;
        id = id.replace('confirmRemoveMap_', '');
        var model = this.mapCollection.get(id);
        model.destroy({wait: true});
    }

});