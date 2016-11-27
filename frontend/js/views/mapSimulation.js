/**
 * Created by anais on 01/11/16.
 */
/**
 * Created by anais on 25/10/16.
 */

app.mapSimulationView = Backbone.View.extend({
    el: '#body',
    map: null,
    tile: null,
    value: null,
    vectorSource: null,
    vectorLayer: null,
    selectPointerMove: null,
    selectPointer: null,
    snap: null,
    mapDetailsCollectionSimulation: null,
    stepSeconds: 60, // On veut un step sur le slider de 60 s
    timeSimulation: null,

    events: {
        'change #osmOppacity': 'clickOnOSM',
        'click #nextSimuSnapshot': 'goNextSnapshot'
    },

    initialize: function (option) {
        console.log("init simmap");
        this.id = option.id;
        this.stepSeconds = parseFloat(option.samplingRate);
        this.departureLivingS = option.departureLivingS;
        console.log(this.samplingRate);
        console.log(this.departureLivingS);
        this.mapDetailsCollectionSimulation = new app.collections.mapDetailsCollectionSimulation({
            id: this.id,
            timestamp: this.departureLivingS
        });
        var self = this;
        //this.mapDetailsCollectionSimulation.on('sync', self.onSync, self);
        //this.mapDetailsCollectionSimulation.on('add', self.onAddElement, self);
        //this.mapDetailsCollectionSimulation.on('remove', self.onRemoveElement, self);
        this.render();
    },

    changeID: function (id, samplingRate, departureLivingS) {
        this.id = id;
        this.stepSeconds = parseFloat(samplingRate);
        this.departureLivingS = departureLivingS;
        this.mapDetailsCollectionSimulation.id = id;
        this.mapDetailsCollectionSimulation.timestamp = departureLivingS;
    },

    render: function () {
        var self = this;
        //Supression du content
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
        //this.$el.append(this.template(new Backbone.Model({"id": this.mapDetailsCollectionSimulation.id})));
        this.$el.append(this.template());

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

        //Trigger du click sur la map
        this.selectPointer = new ol.interaction.Select({
            layers: [this.vectorLayer],
            style: function (feature, resolution) {
                return self.generateSelectStyle(feature, resolution);
            }
        });
        this.selectPointer.on('select', function (e) {
            if (e.deselected.length > 0) {
                self.clickCloseInfo();
            }
            if (e.selected.length > 0) {
                self.renderFeatureInformations(e.selected[0]);
            }
        });
        this.selectPointerMove = new ol.interaction.Select({
            layers: [this.vectorLayer],
            condition: ol.events.condition.pointerMove,
            style: function (feature, resolution) {
                return self.generateSelectMoveStyle(feature, resolution);
            }
        });
        this.snap = new ol.interaction.Snap({
            source: this.vectorSource
        });
        //this.map.addInteraction(this.selectPointerMove);
        //this.map.addInteraction(this.selectPointer);
        this.map.addInteraction(this.snap);

        //Fetch de la collection
        this.fetchCollection();
        // TODO : remettre le modal d'avertissement à la fin
        // $('#modalAvertissementSimulation').modal('show');

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

        // Slider for time simulation
        $("#sliderSimulation").slider({
            orientation: "horizontal",
            range: "min",
            min: 0,
            max: 89999, //86400
            step: this.stepSeconds,
            value: parseFloat(this.departureLivingS), // correspond à 7h
            slide: function (event, ui) {
                var time = self.convertSecdsToHrsMinsSecds(ui.value);
                console.log('---- slide ----');
                console.log('value : ' + ui.value);
                console.log('time : ' + time);
                $('#timepicker').timepicker('setTime', time);
            },
            change: function (event, ui) {
                console.log('---- change ----');
                console.log('event :' + event);
                $("#sliderSimulation").slider({
                    disabled: true
                });
                $('#timepicker').prop('disabled', true);
                $('#nextSimuSnapshot').prop('disabled', true);
                console.log(ui.value);
                //var time = self.convertSecdsToHrsMinsSecds(ui.value);
                //$('#timepicker').timepicker('setTime', time);
                self.mapDetailsCollectionSimulation.timestamp = ui.value;
                self.mapDetailsCollectionSimulation.fetch({
                    success: function () {
                        console.log("success fetch");
                        self.vectorSource.clear();
                        if (self.mapDetailsCollectionSimulation.length > 0) {
                            for(var i=0; i<self.mapDetailsCollectionSimulation.length; i++) {
                                var element = self.mapDetailsCollectionSimulation.models[i];
                                var geojsonModel = element.toGeoJSON();
                                var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
                                    featureProjection: 'EPSG:3857'
                                });
                                self.vectorSource.addFeature(newfeature);
                            }
                        }
                        $("#sliderSimulation").slider({
                            disabled: false
                        });
                        $('#timepicker').prop('disabled', false);
                        $('#nextSimuSnapshot').prop('disabled', false);
                    }
                });
                //console.log('time : '+time);
            }
        });

        // Time picker
        $('#timepicker').timepicker({
            minuteStep: this.stepSeconds,
            template: false,
            showSeconds: true,
            showMeridian: false,
            defaultTime: this.convertSecdsToHrsMinsSecds(this.departureLivingS),
            modalBackDrop: true
        });

        $('#timepicker').timepicker().on('changeTime.timepicker', function (e) {
            console.log(e.time.hours + ':' + e.time.minutes + ':' + e.time.seconds);
            var time = (e.time.hours * 3600) + (e.time.minutes * 60) + e.time.seconds;
            console.log('ChangeTime time in seconds : ' + time);
            var verifStep = (time % self.stepSeconds);
            if (verifStep != 0) {
                time -= verifStep;
                time = self.convertSecdsToHrsMinsSecds(time);
                $('#timepicker').timepicker('setTime', time);
            }
            $("#sliderSimulation").slider("option", "value", time);
        });
        $('[data-toggle="tooltip"]').tooltip();

        return this;
    },

    goNextSnapshot: function(){
        var value = $( "#sliderSimulation" ).slider( "option", "value" );
        $('#timepicker').timepicker('setTime', this.convertSecdsToHrsMinsSecds(value+this.stepSeconds));
        //var value = $( "#sliderSimulation" ).slider( "option", "value" , parseFloat(value+this.stepSeconds));
    },

    changeOppacity: function (value) {
        this.tile.setOpacity(value);
    },

    onSync: function () {
        $("#sliderSimulation").slider({
            disabled: false
        });
        $('#timepicker').prop('disabled', false);
        var self = this;
        //this.mapDetailsCOllection.on('add', self.onAddElement, self);
    },

    fetchCollection: function () {
        var self = this;
        //this.mapDetailsCOllection.off("add");
        this.mapDetailsCollectionSimulation.fetch({
            success: function () {
                if (self.mapDetailsCollectionSimulation.length > 0) {
                    for(var i=0; i<self.mapDetailsCollectionSimulation.length; i++) {
                        var element = self.mapDetailsCollectionSimulation.models[i];
                        var geojsonModel = element.toGeoJSON();
                        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
                            featureProjection: 'EPSG:3857'
                        });
                        self.vectorSource.addFeature(newfeature);
                    }
                    self.map.getView().fit(self.vectorSource.getExtent(), self.map.getSize());
                }
            }
        });
    },

    onAddElement: function (element) {
        console.log('on add');
        var geojsonModel = element.toGeoJSON();
        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
            featureProjection: 'EPSG:3857'
        });
        this.vectorSource.addFeature(newfeature);
    },

    onRemoveElement: function (element) {
        console.log("remove");
        console.log(element);
        if (this.vectorSource.getFeatureById(element.attributes.id) || this.vectorSource.getFeatureById(element.attributes.id) != null) {
            this.vectorSource.removeFeature(this.vectorSource.getFeatureById(element.attributes.id));
        }
    },

    clickOnOSM: function () {
        this.tile.setOpacity($('#osmOppacity').val());
    },

    generateStyle: function (feature, resolution) {

        var type = feature.getProperties().type;
        var congestion = feature.getProperties().congestion;
        var color;

        if (congestion < 30) {
            color = [46, 204, 113, 1];
        } else if (congestion >= 30 && congestion < 80) {
            color = [211, 84, 0, 1];
        } else if (congestion >= 80) {
            color = [192, 57, 43, 1];
        }

        // TODO : récupérer les valeurs d'afluence des routes et afficher la couelur en fonction de ça
        var oneway = 1;
        if (feature.getProperties().oneway && feature.getProperties().oneway == true) {
            oneway = 0.5;
        }

        switch (type) {
            case 1:
                //SINGLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: color,
                        width: (7 / resolution) * oneway
                    })
                });
                return style;
                break;
            case 2:
                //DOUBLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: color,
                        width: (14 / resolution) * oneway
                    })
                });
                return style;
                break;
            case 3:
                //TRIPLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: color,
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
                        color: color,
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
                switch (parseInt(feature.getProperties().id) % 5) {
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
                        //scale: 0.027 / resolution,
                        scale: 1,
                        src: url,
                        rotation: angle
                    })
                });
                return style;
                break;
            default:
                break;
        }
    },

    generateSelectStyle: function (feature, resolution) {
        var type = feature.getProperties().type;
        var geometry = feature.getGeometry();
        var startCoord = geometry.getFirstCoordinate();
        var endCoord = geometry.getLastCoordinate();
        var oneway = 1;
        var circle = new ol.style.Circle({
            stroke: new ol.style.Stroke({
                color: [50, 50, 50, 1]
            }),
            fill: new ol.style.Fill({
                color: [200, 200, 200, 0.8]
            }),
            radius: 10
        });
        var firstPoint = new ol.style.Style({
            geometry: new ol.geom.Point(startCoord),
            image: circle,
            text: new ol.style.Text({
                textAlign: "center",
                textBaseline: "middle",
                font: 'Normal 12px Arial',
                text: 'A',
                fill: circle.getStroke(),
                offsetX: 0,
                offsetY: 0,
                rotation: 0
            })
        });
        var lastPoint = new ol.style.Style({
            geometry: new ol.geom.Point(endCoord),
            image: circle,
            text: new ol.style.Text({
                textAlign: "center",
                textBaseline: "middle",
                font: 'Normal 12px Arial',
                text: 'B',
                fill: circle.getStroke(),
                offsetX: 0,
                offsetY: 0,
                rotation: 0
            })
        });
        if (feature.getProperties().oneway && feature.getProperties().oneway == true) {
            oneway = 0.5;
        }
        switch (type) {
            case 1:
                //SINGLE ROAD
                var styles = [
                    // linestring
                    new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: [26, 155, 252, 1],
                            width: ((7 + 2) / resolution) * oneway
                        })
                    }),
                    //First point
                    firstPoint,
                    //Last point
                    lastPoint
                ];
                return styles;
                break;
            case 2:
                //DOUBLE ROAD
                var styles = [
                    // linestring
                    new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: [26, 155, 252, 1],
                            width: ((14 + 2) / resolution) * oneway
                        })
                    }),
                    //First point
                    firstPoint,
                    //Last point
                    lastPoint
                ];
                return styles;
                break;
            case 3:
                //TRIPLE ROAD
                var styles = [
                    // linestring
                    new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: [26, 155, 252, 1],
                            width: ((21 + 1) / resolution) * oneway
                        })
                    }),
                    //First point
                    firstPoint,
                    //Last point
                    lastPoint
                ];
                return styles;
                break;
            case 4:
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: [250, 178, 102, 1]
                    }),
                    stroke: new ol.style.Stroke({
                        color: [26, 155, 252, 1],
                        width: (3.5 + 2) / resolution
                    })
                });
                return style;
                break;
            case 5:
                //RED_LIGHT
                console.log(resolution);
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [44, 100],
                        offset: [0, 0],
                        opacity: 1,
                        scale: (0.1 + 0.2) / resolution,
                        src: 'assets/img/redlight.jpg'
                    })
                });
                return style;
                break;
            default:
                console.log("default");
                break;
        }
    },

    generateSelectMoveStyle: function (feature, resolution) {
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
                        color: [26, 155, 252, 1],
                        width: (7 / resolution) * oneway
                    })
                });
                return style;
                break;
            case 2:
                //DOUBLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [26, 155, 252, 1],
                        width: (14 / resolution) * oneway
                    })
                });
                return style;
                break;
            case 3:
                //TRIPLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [26, 155, 252, 1],
                        width: (21 / resolution) * oneway
                    }),

                });
                return style;
                break;
            case 4:
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: [26, 155, 252, 1]
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
                        scale: (0.1 + 0.2) / resolution,
                        src: 'assets/img/redlight.jpg'
                    })
                });
                return style;
                break;
            default:
                break;
        }
    },

    clickCloseInfo: function () {
        $('#osmInfo').empty();
    },

    renderFeatureInformations: function (feature) {
        var featureid = feature.getProperties().id;
        var model = this.mapDetailsCollectionSimulation.get(featureid);
        new app.mapPopUpInfoVisuView({
            model: model
        });
    },

    convertSecdsToHrsMinsSecds: function (seconds) {
        // TODO : faire attention au step, le prender en compte et mettre la valeur supérieur si besoin
        // TODO : prendre en compte les secondes
        var h = Math.floor(seconds / 3600);
        var m = Math.floor((seconds % 3600) / 60);
        var s = (seconds % 3600) % 60;
        h = h < 10 ? '0' + h : h;
        m = m < 10 ? '0' + m : m;
        s = s < 10 ? '0' + s : s;
        var time = h + ':' + m + ':' + s;
        console.log(time);
        return time;

    },
});
