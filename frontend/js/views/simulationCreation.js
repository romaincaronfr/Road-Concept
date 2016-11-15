/**
 * Created by andreas on 12/11/16.
 */

app.simulationCreationView = Backbone.View.extend({
    el: '#body',
    tile: null,
    value: null,
    draw: null,
    vectorSource: null,
    vectorLayer: null,
    snap: null,
    mapDetailsCOllection: null,
    step: 0,

    events: {
        'click #ready': 'addInteraction',
        'click #previous' : 'previous'
    },

    initialize: function (options) {
        this.id = options.id;
        console.log('id:' + this.id);
        this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id: this.id});
        console.log(this.mapDetailsCOllection);
        this.render();
        var self = this;
        this.mapDetailsCOllection.on('sync', self.onSync, self);
        this.mapDetailsCOllection.on('add', self.onAddElement, self);
        this.mapDetailsCOllection.on('remove', self.onRemoveElement, self);
    },

    render: function () {
        var self = this;
        this.mapDetailsCOllection.reset();
        $('#content').empty();
        if (this.vectorSource) {
            this.vectorSource.clear();
        }

        //Si la div existait déjà
        if ($('#mapRow').length) {
            $('#mapRow').remove();
        }

        //Ajout de la template au body
        this.$el.append(this.template(new Backbone.Model({"id": this.id})));

        //Fond de carte OSM
        this.tile = new ol.layer.Tile({
            source: new ol.source.OSM()
        });

        //Création de la map, si la variable était déjà inialisé on écrase
        this.map = new ol.Map({
            target: 'map',
            controls: ol.control.defaults({
                attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
                    collapsible: false
                })
            }).extend([
                new ol.control.ScaleLine()
            ]),
            layers: [
                this.tile
            ],
            view: new ol.View({
                center: ol.proj.fromLonLat([-3.459144, 48.732084]),
                zoom: 12
            })
        });

        //Réglage de l'opacité du fond de carte OSM
        this.tile.setOpacity(0.3);

        //Préparation du layer pour notre GeoJSON
        this.vectorSource = new ol.source.Vector();
        this.vectorLayer = new ol.layer.Vector({
            source: this.vectorSource,
            style: function (feature, resolution) {
                return self.generateStyle(feature, resolution);
            }
        });
        this.map.addLayer(this.vectorLayer);

        this.snap = new ol.interaction.Snap({
            source: this.vectorSource
        });
        this.map.addInteraction(this.snap);

        this.fetchCollection();
        $('#modalAvertissementSimulation').modal('show');

        $("#osmSlider").slider({
            orientation: "vertical",
            range: "min",
            min: 0,
            max: 1,
            step: 0.1,
            value: 0.3,
            slide: function (event, ui) {
                self.changeOppacity(ui.value);
            }
        });

        //Tooltip bootstrap
        $('[data-toggle="tooltip"]').tooltip();

        return this;
    },

    changeID: function (id) {
        this.id = id;
        this.mapDetailsCOllection.id = id;
    },

    onAddElement: function (element) {
        console.log("add");
        var geojsonModel = element.toGeoJSON();
        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
            featureProjection: 'EPSG:3857'
        });
        this.vectorSource.addFeature(newfeature);
    },

    onRemoveElement: function (element) {
        console.log("remove");
        $('#osmInfo').empty();
        this.vectorSource.removeFeature(this.vectorSource.getFeatureById(element.attributes.id));
        this.selectPointer.getFeatures().clear();
    },

    onSync: function () {
        console.log('sync');
        /*if (this.mapDetailsCOllection.length > 0) {
         this.vectorSource.clear();
         var geoJson = this.mapDetailsCOllection.toGeoJSON();
         var featuresSource = new ol.format.GeoJSON().readFeatures(geoJson, {
         featureProjection: 'EPSG:3857'
         });
         this.vectorSource.addFeatures(featuresSource);
         this.map.getView().fit(this.vectorSource.getExtent(), this.map.getSize());
         }*/
        var self = this;
        //this.mapDetailsCOllection.on('add', self.onAddElement, self);
    },

    changeOppacity: function (value) {
        this.tile.setOpacity(value);
    },

    fetchCollection: function () {
        var self = this;
        //this.mapDetailsCOllection.off("add");
        this.mapDetailsCOllection.fetch({
            success: function () {
                self.map.getView().fit(self.vectorSource.getExtent(), self.map.getSize());
            }
        });
    },

    generateStyle: function (feature, resolution) {
        var type = feature.getProperties().type;
        var oneway = 1;
        if (feature.getProperties().oneway && feature.getProperties().oneway == true) {
            oneway = 0.5;
        }
        switch (type) {
            case 1:
                //SINGLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [241, 196, 15, 1],
                        width: (7 / resolution) * oneway
                    })
                });
                return style;
                break;
            case 2:
                //DOUBLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [230, 126, 34, 1],
                        width: (14 / resolution) * oneway
                    })
                });
                return style;
                break;
            case 3:
                //TRIPLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [231, 76, 60, 1],
                        width: (21 / resolution) * oneway
                    }),

                });
                return style;
                break;
            case 4:
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: [250, 178, 102, 1]
                    }),
                    stroke: new ol.style.Stroke({
                        color: [0, 255, 0, 1],
                        width: 3.5 / resolution
                    })
                });
                return style;
                break;
            case 5:
                //RED_LIGHT
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [44, 100],
                        offset: [0, 0],
                        opacity: 1,
                        scale: 0.1 / resolution,
                        src: 'assets/img/redlight.jpg'
                    })
                });
                return style;
                break;
            default:
                break;
        }
    },

    addInteraction: function (place) {
        var self = this;
        this.draw = new ol.interaction.Draw({
            source: new ol.source.Vector(),
            type: 'Point'
        });

        this.map.addInteraction(this.draw);
        this.map.addInteraction(this.snap);

        this.draw.on('drawend', function (event) {
            console.log('step courant : '+self.step);

            console.log('Draw : end');
            var feature = event.feature;
            var JSONFeature = new ol.format.GeoJSON().writeFeature(feature, {
                dataProjection: 'EPSG:3857',
                featureProjection: 'EPSG:3857'
            });
            JSONFeature = JSON.parse(JSONFeature);

            var coord = feature.getGeometry().getCoordinates();
            coord = ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
            JSONFeature.geometry.coordinates = coord;

            if(self.step == 0){ // Premier step : lieu d'habitation
                JSONFeature.properties = {type: 6, nbHabit: 30, hour: 10};
                // on doit ouvrir un modal et recup nbhabit et hour
                // On doit afficher la suite si modal OK
                $('#habitZone').hide();
                $('#workZone').show();
                $('#divPrevious').show();
                self.step++;
            } else if(self.step == 1){
                JSONFeature.properties = {type: 6, nbHabit: 30, hour: 10};
                // modal : heure de retour
                $('#workZone').hide();
                $('#startSim').show();

                self.step++;
            } else {
                console.log('Start Simulation');

            }

            self.newModel = new app.models.mapDetailsModel(JSONFeature, {
                parse: true,
                collection: self.mapDetailsCOllection
            });

            self.renderFeatureCreation(self.newModel);

            var geojsonModel = self.newModel.toGeoJSON();
            var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
                featureProjection: 'EPSG:3857'
            });
            self.vectorSource.addFeature(newfeature);

            console.log('step suivant : '+self.step);

            self.map.removeInteraction(this.draw);
            self.map.removeInteraction(this.snap);
        });
    },

    renderFeatureCreation: function (model) {
        switch (model.attributes.type) {
            case 1:
            case 2:
            case 3:
                new app.mapPopUpCreateRoadsView({
                    model: model
                });
                break;
            case 4:
                new app.mapPopUpCreateRondPointView({
                    model: model
                });
                break;
            case 5:
                new app.mapPopUpCreateRedlightsView({
                    model: model
                });
                break;
        }

    },
    previous : function (){
        console.log('cancel, step:'+this.step);
        if(this.step >0){
            this.step --;
        }

        if(this.step == 0){
            $('#workZone').hide();
            $('#startSim').hide();
            $('#divPrevious').hide();
            $('#habitZone').show();
        } else if (this.step == 1){
            $('#habitZone').hide();
            $('#startSim').hide();
            $('#workZone').show();
        } else if (this.step == 2){
            $('#workZone').hide();
            $('#habitZone').hide();
            $('#startSim').show();
        } else {
            console.log('step > 2 ou <0');
        }
    }
});