/**
 * Created by Romain on 29/11/2016.
 */


app.mapCarSimulationView = Backbone.View.extend({
    el: '#body',
    map: null,
    tile: null,
    value: null,
    vectorSource: null,
    vectorLayer: null,
    vectorSource2: null,
    vectorLayer2: null,
    selectPointerMove: null,
    selectPointer: null,
    snap: null,
    mapDetailsCollectionSimulation: null,
    stepSeconds: 60, // On veut un step sur le slider de 60 s
    timeSimulation: null,
    mapID: null,

    events: {
        'change #osmOppacity': 'clickOnOSM',
        'click #nextSimuSnapshot': 'goNextSnapshot'
    },

    initialize: function (option) {
        //{idSimu:idSimu, simTime:simTime,sampling:sampling,idMap:idMap,idCar:idCar}
        this.id = option.idCar;
        this.stepSeconds = option.sampling;
        this.mapID = option.idMap;
        this.idSimu = option.idSimu;
        this.simTime = option.simTime;
        this.mapDetailsCollectionSimulation = new app.collections.mapDetailsCollectionSimulationCar({
            id: this.idSimu,
            vehicle_id: this.id
        });
        this.render();
    },

    changeID: function (idSimu, simTime, sampling, idMap, idCar) {
        this.id = idCar;
        this.stepSeconds = sampling;
        this.mapID = idMap;
        this.idSimu = idSimu;
        this.simTime = simTime;
        this.mapDetailsCollectionSimulation.id = idSimu;
        this.mapDetailsCollectionSimulation.vehicle_id = idCar;
    },

    render: function () {
        var self = this;
        //Suppression du content
        this.mapDetailsCollectionSimulation.reset();
        $('#content').empty();
        if (this.vectorSource) {
            this.vectorSource.clear();
        }

        //Si la div existait déjà
        if ($('#mapRow').length) {
            $('#mapRow').remove();
        }

        //Ajout de la template au body
        var mod = new Backbone.Model({
            "mapID": this.mapID,
            "simuID": this.idSimu,
            "samp": this.stepSeconds,
            "simTime": this.simTime
        });
        this.$el.append(this.template(mod.attributes));

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
                center: ol.proj.fromLonLat([5.336409, 43.36051]),
                zoom: 14
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
        this.vectorSource2 = new ol.source.Vector();
        this.vectorLayer2 = new ol.layer.Vector({
            source: this.vectorSource2,
            style: function (feature, resolution) {
                return self.generateStyle(feature, resolution);
            }
        });
        this.map.addLayer(this.vectorLayer2);


        //Fetch de la collection
        this.fetchCollection();

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
        $('[data-toggle="tooltip"]').tooltip();

        return this;
    },

    changeOppacity: function (value) {
        this.tile.setOpacity(value);
    },

    fetchCollection: function () {
        var self = this;
        this.mapDetailsCollectionSimulation.fetch({
            success: function () {
                if (self.mapDetailsCollectionSimulation.length > 0) {
                    for (var i = 0; i < self.mapDetailsCollectionSimulation.length; i++) {
                        var element = self.mapDetailsCollectionSimulation.models[i];
                        var geojsonModel = element.toGeoJSON();
                        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
                            featureProjection: 'EPSG:3857'
                        });
                        if (element.attributes.type == 6) {
                            self.vectorSource2.addFeature(newfeature);
                        } else {
                            self.vectorSource.addFeature(newfeature);
                        }
                    }
                    self.map.getView().fit(self.vectorSource.getExtent(), self.map.getSize());
                }
            }
        });
    },
    clickOnOSM: function () {
        this.tile.setOpacity($('#osmOppacity').val());
    },

    generateStyle: function (feature, resolution) {

        var type = feature.getProperties().type;
        var oneway = 1;
        if (feature.getProperties().oneway && feature.getProperties().oneway == true) {
            oneway = 0.5;
        }
        console.log("generateStyle");

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
                    })
                });
                return style;
                break;
            case 4:
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: [250, 178, 102, 1]
                    }),
                    stroke: new ol.style.Stroke({
                        color: [26, 155, 252, 1],
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
            case 6:
                //CAR
                var url = 'assets/img/car.png';
                switch (parseInt(feature.getProperties().vehicle_id) % 5) {
                    case 0:
                        url = 'assets/img/car_blue.png';
                        break;
                    case 1:
                        url = 'assets/img/car_green.png';
                        break;
                    case 2:
                        url = 'assets/img/car_pink.png';
                        break;
                    case 3:
                        url = 'assets/img/car_red.png';
                        break;
                    case 4:
                        url = 'assets/img/car_yellow.png';
                        break;
                }
                var angle = feature.getProperties().angle;
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [26, 40],
                        offset: [0, 0],
                        opacity: 1,
                        scale: 0.15 / resolution,
                        //scale: 1,
                        src: url,
                        rotation: angle
                    })
                });
                return style;
                break;
            case 7:
                //TRUCK
                console.log("case camion");
                var url = 'assets/img/truck.png';
                var angle = feature.getProperties().angle;
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [30, 60],
                        offset: [0, 0],
                        opacity: 1,
                        scale: 0.15 / resolution,
                        //scale: 1,
                        src: url,
                        rotation: angle
                    })
                });
                return style;
                break;
            default:
                break;
        }
    }
});
