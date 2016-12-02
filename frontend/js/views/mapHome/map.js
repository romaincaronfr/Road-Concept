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
        this.mapCollection.on('destroy', self.newDestroy, self);
    },

    render: function () {
        this.$el.html(this.template());
        this.mapCollection.each(function (model) {
            var mapTableView = new app.mapTableView({
                model: model
            });

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
        var newMapModel = new app.models.mapModel({'name': name, 'description': description, 'image_url': urlImg});
        newMapModel.save(null, {
            success: (function () {
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

    clickOnPrintMap: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('afficher_', '');
        app.router.navigate('map/' + id, {trigger: true});
    },

    newElement: function (element) {
        new app.mapTableView({
            model: element
        });
    },
    newDestroy: function (element) {
        var divName = '#div_mapId_' + element.attributes.id;
        $(divName).remove();
        $('#modalRemoveMap').modal('hide');
    },

    clickOnRemove: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('remove_', '');
        var model = this.mapCollection.get(id);
        new app.modalRemoveMapView({
            model: model
        });
    },

    clickOnremove_map_confirm: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('confirmRemoveMap_', '');
        var model = this.mapCollection.get(id);
        model.destroy({wait: true});
    }

});