/**
 * Created by Romain on 03/10/2016.
 */
app.mapView = Backbone.View.extend({

    el: '#content',
    events: {
        'click #submitCreateMap': 'clickOnCreateNewMap',
        'click .remove_map': 'clickOnRemove',
        'click .print_map': 'clickOnPrintMap',
        'click .remove_map_confirm': 'clickOnremove_map_confirm',
    },
    imageBase64 : null,

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
        // $('#submitCreateMap').prop("disabled", true);
        var name = $('#mapName').val();
        var description = $('#mapDescription').val();
        var urlImg = null;

        var file = $('input[type=file]')[0].files[0];

        console.log(file);

        if (file && file.length != 0 && file.type.match('image.*')) {

            var img = document.createElement("img");
            var reader = new FileReader();
            reader.onload = function (e) {
                img.src = e.target.result
            }
            reader.readAsDataURL(file);

            img.onload = function () {
                console.log("Chargement de l'image");
                // Création du canevas pour resize l'image
                var canvas = document.createElement('canvas');
                var ctx = canvas.getContext("2d");
                ctx.drawImage(img, 0, 0);

                var MAX_WIDTH = 500;
                var MAX_HEIGHT = 500;
                var width = img.width;
                var height = img.height;

                if (width > height) {
                    if (width > MAX_WIDTH) {
                        height *= MAX_WIDTH / width;
                        width = MAX_WIDTH;
                    }
                } else {
                    if (height > MAX_HEIGHT) {
                        width *= MAX_HEIGHT / height;
                        height = MAX_HEIGHT;
                    }
                }
                canvas.width = width;
                canvas.height = height;
                var ctx = canvas.getContext("2d");
                ctx.drawImage(img, 0, 0, width, height);

                urlImg = canvas.toDataURL(file.type);
                self.sendCreateNewMap(name,description,urlImg);
            }
        } else {
            self.sendCreateNewMap(name,description,urlImg);
        }

    },

    sendCreateNewMap: function(name,description,urlImg){
        var self = this;
        var newMapModel;
        if (urlImg){
            newMapModel = new app.models.mapModel({'name': name, 'description': description, 'image_url': urlImg});
        } else {
            newMapModel = new app.models.mapModel({'name': name, 'description': description});
        }
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