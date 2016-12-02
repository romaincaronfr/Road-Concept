/**
 * Created by anais on 25/10/16.
 */

app.mapEditionView = Backbone.View.extend({

    el: '#body',
    map: null,
    tile: null,
    value: null,
    draw: null,
    vectorSource: null,
    vectorLayer: null,
    selectPointerMove: null,
    selectPointer: null,
    snap: null,
    mapDetailsCOllection: null,
    interactionZoomDoubleClick: null,
    index: null,
    intersections: null,
    newModel: null,
    eventClick: null,

    events: {
        //'click .close_map_info': 'clickCloseInfo',
        'click button[name=chooseTool]': 'hasChooseTool',
        'click button[id=cancel]': 'cancelHasChooseTool',
        'change #onwayRoad': 'selectOneWay',
        'click .validModif': 'validModif',
        'click .removeModif': 'removeModif',
        'click .validModel': 'validModel',
        'click .removeModel': 'cancelUnderCreation',
        'click #importButton': 'clickOnImport',
        'click .importButton': 'importData',
        'hide.bs.modal #modalImport': 'hideModal'
    },

    initialize: function (options) {
        this.id = options.id;
        this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id: this.id});
        this.render();
        var self = this;
        this.mapDetailsCOllection.on('add', self.onAddElement, self);
        this.mapDetailsCOllection.on('remove', self.onRemoveElement, self);
    },

    render: function () {
        //Suppression du content
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
        this.$el.append(this.template(new Backbone.Model({"id": this.mapDetailsCOllection.id})));

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

        //Trigger du click sur la map
        var self = this;
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
        this.map.addInteraction(this.selectPointerMove);
        this.map.addInteraction(this.selectPointer);
        this.map.addInteraction(this.snap);

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

        //Tooltip bootstrap
        $('[data-toggle="tooltip"]').tooltip();

        /**
         * Get the interaction in interactionZoomDoubleClick : zoom when doubleclick
         */
        var interactions = this.map.getInteractions();
        for (var i = 0; i < interactions.getLength(); i++) {
            var interaction = interactions.item(i);
            if (interaction instanceof ol.interaction.DoubleClickZoom) {
                this.interactionZoomDoubleClick = interaction;
            }
        }
        return this;
    },

    changeID: function (id) {
        this.id = id;
        this.mapDetailsCOllection.id = id;
    },

    onAddElement: function (element) {
        var geojsonModel = element.toGeoJSON();
        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
            featureProjection: 'EPSG:3857'
        });
        this.vectorSource.addFeature(newfeature);
    },

    onRemoveElement: function (element) {
        $('#osmInfo').empty();
        if (this.vectorSource.getFeatureById(element.attributes.id) || this.vectorSource.getFeatureById(element.attributes.id) != null) {
            this.vectorSource.removeFeature(this.vectorSource.getFeatureById(element.attributes.id));
        }
        this.selectPointer.getFeatures().clear();
    },

    changeOppacity: function (value) {
        this.tile.setOpacity(value);
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
        var model = this.mapDetailsCOllection.get(featureid);
        switch (model.attributes.type) {
            case 1:
            case 2:
            case 3:
                new app.mapPopUpEditRoadsView({
                    model: model
                });
                break;
            case 4:
                new app.mapPopUpEditRondPointView({
                    model: model
                });
                break;
            case 5:
                new app.mapPopUpEditRedlightsView({
                    model: model
                });
                break;
        }

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

    addInteraction: function () {
        if (this.value != 'None') {
            this.draw = new ol.interaction.Draw({
                source: new ol.source.Vector(),
                type: this.value
            });

            this.index = 0;
            this.intersections = [];
            var self = this;
            this.eventClick = this.map.on('click', function (event) {
                var pixel = event.pixel;
                var features = {};
                var marge = 0;
                var resolution = self.map.getView().getResolution();

                if (resolution > 0.5 && resolution < 1) {
                    marge = 3;
                } else if (resolution < 2) {
                    marge = 7;
                } else if (resolution < 4) {
                    marge = 8;
                } else {
                    marge = 9;
                }

                if (marge == 0) {
                    self.map.forEachFeatureAtPixel(pixel, function (feature) {
                        if (feature.getProperties().id && !features[feature.getProperties().id]) {
                            features[feature.getProperties().id] = feature.getProperties().id;
                        }
                    });
                } else {
                    for (var i = pixel[0] - marge; i < pixel[0] + marge; i++) {
                        for (var j = pixel[1] - marge; j < pixel[1] + marge; j++) {
                            var newPixel = [i, j];

                            self.map.forEachFeatureAtPixel(newPixel, function (feature) {
                                if (feature.getProperties().id && !features[feature.getProperties().id]) {
                                    features[feature.getProperties().id] = feature.getProperties().id;
                                }
                            });
                        }
                    }
                }

                self.intersections[self.index] = features;
                self.index++;
            });

            this.map.addInteraction(this.draw);
            this.map.addInteraction(this.snap);


            this.draw.on('drawstart', function () {

                self.interactionZoomDoubleClick.setActive(false);

            });

            this.draw.on('change', function (event) {
            });

            this.draw.on('drawend', function (event) {
                var feature = event.feature;

                var JSONFeature = new ol.format.GeoJSON().writeFeature(feature, {
                    dataProjection: 'EPSG:3857',
                    featureProjection: 'EPSG:3857'
                });
                JSONFeature = JSON.parse(JSONFeature);
                var newIntersections = [];
                for (var i = 0; i < self.intersections.length; i++) {
                    newIntersections[i] = [];
                    for (key in self.intersections[i]) {
                        newIntersections[i].push(key);
                    }
                }
                self.intersections = newIntersections;
                switch (self.value) {
                    case 'Polygon':
                        JSONFeature.geometry.coordinates = self.transoformToGps(feature.getGeometry().getCoordinates()[0]);
                        JSONFeature.geometry.type = "LineString";
                        JSONFeature.properties = {type: 4, maxspeed: 30};
                        break;
                    case 'LineString':
                        JSONFeature.geometry.coordinates = self.transoformToGps(feature.getGeometry().getCoordinates());
                        JSONFeature.properties = {type: 1, maxspeed: 50, oneway: "no"};
                        //console.log(self.vectorSource.getFeaturesAtCoordinate(feature.getGeometry().getCoordinates()[0]));
                        break;
                    case 'Point':
                        var coord = feature.getGeometry().getCoordinates();
                        coord = ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
                        JSONFeature.geometry.coordinates = coord;
                        JSONFeature.properties = {type: 5, redlighttime: 30};
                        break;
                }
                JSONFeature.properties.intersections = self.intersections;
                JSONFeature.properties.name = "Unnamed unit road";
                JSONFeature.properties.id = "1";
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
                self.underCreation();
                setTimeout(function () {
                    self.interactionZoomDoubleClick.setActive(true);
                }, 251);
                self.map.unByKey(self.eventClick);
            });
        }
    },

    hasChooseTool: function (e) {
        $('#editButtonChooseTool').hide();
        $('#editButtonCancel').show();
        this.value = $(e.currentTarget).attr('value');
        this.map.removeInteraction(this.selectPointer);
        this.addInteraction(this.value);

        //Affiche le tooltip correspondant à l'élément choisi
        switch (this.value) {
            case 'LineString':
                $('#roadHelp').show();
                $('#roundAboutHelp').hide();
                $('#trafficLightsHelp').hide();
                break;
            case 'Polygon':
                $('#roadHelp').hide();
                $('#roundAboutHelp').show();
                $('#trafficLightsHelp').hide();
                break;
            case 'Point':
                $('#roadHelp').hide();
                $('#roundAboutHelp').hide();
                $('#trafficLightsHelp').show();
                break;
            default:
                break;
        }
    },

    cancelHasChooseTool: function () {
        $('#editButtonCancel').hide();
        $('#editButtonChooseTool').show();
        this.value = 'None';
        this.newModel = null;
        this.map.unByKey(this.eventClick);
        this.map.removeInteraction(this.draw);
        this.map.addInteraction(this.selectPointer);
    },

    underCreation: function () {
        $('#editButtonCancel').hide();
        $('#editButtonChooseTool').show();
        $('#editBarMenu').hide();
        this.map.removeInteraction(this.draw);
        this.map.removeInteraction(this.selectPointerMove);
    },

    cancelUnderCreation: function () {
        $('#osmInfo').empty();
        $('#editBarMenu').show();
        this.value = 'None';
        this.newModel = null;
        this.vectorSource.removeFeature(this.vectorSource.getFeatureById("1"));
        this.map.addInteraction(this.selectPointerMove);
        this.map.addInteraction(this.selectPointer);
    },

    fetchCollection: function () {
        var self = this;
        this.mapDetailsCOllection.fetch({
            success: function () {
                if (self.mapDetailsCOllection.length > 0) {
                    console.log(self.mapDetailsCOllection.length);
                    self.map.getView().fit(self.vectorSource.getExtent(), self.map.getSize());
                }
            }
        });
    },

    transoformToGps: function (coordinates) {
        for (var i = 0; i < coordinates.length; i++) {
            coordinates[i] = ol.proj.transform(coordinates[i], 'EPSG:3857', 'EPSG:4326');
        }
        return coordinates;
    },

    selectOneWay: function (event) {
        if ($('#onwayRoad').val() == "no") {
            $('#wayDiv').addClass('hidden');
        } else {
            $('#wayDiv').removeClass('hidden');
        }
    },

    validModif: function (event) {
        var id = event.currentTarget.id;
        var model = this.mapDetailsCOllection.get(id);
        if (model.attributes.type == 1 || model.attributes.type == 2 || model.attributes.type == 3) {
            model.set({
                type: parseInt($('#selectTypeRoad').val()),
                name: $('#roadName').val(),
                maxspeed: parseInt($('#maxspeedRoad').val())
            });
            if ($('#onwayRoad').val() == "no") {
                model.set({oneway: "no"});
            } else {
                if ($('#wayRoad').val() == "yes") {
                    model.set({oneway: "yes"});
                } else {
                    model.set({oneway: "-1"});
                }
            }
        } else if (model.attributes.type == 5) {
            model.set({
                name: $('#redName').val(),
                redlighttime: parseInt($('#redlightT').val())
            });
        } else if (model.attributes.type == 4) {
            model.set({
                name: $('#RPName').val(),
                maxspeed: parseInt($('#maxspeedRP').val())
            });
        }
        var self = this;
        model.save(null, {
            success: function () {
                $('#osmInfo').empty();
                self.vectorSource.removeFeature(self.vectorSource.getFeatureById(model.attributes.id));
                var geojsonModel = model.toGeoJSON();
                var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
                    featureProjection: 'EPSG:3857'
                });
                self.selectPointer.getFeatures().clear();
                self.vectorSource.addFeature(newfeature);
            }
        });
    },

    removeModif: function (event) {
        var id = event.currentTarget.id;
        id = id.replace('removeRoad_', '');
        var model = this.mapDetailsCOllection.get(id);
        model.destroy({wait: true});
    },

    validModel: function () {
        var model = this.newModel;
        if (model.attributes.type == 1 || model.attributes.type == 2 || model.attributes.type == 3) {
            model.set({
                type: parseInt($('#selectTypeRoad').val()),
                name: $('#roadName').val(),
                maxspeed: parseInt($('#maxspeedRoad').val())
            });
            if ($('#onwayRoad').val() == "no") {
                model.set({oneway: "no"});
            } else {
                if ($('#wayRoad').val() == "yes") {
                    model.set({oneway: "yes"});
                } else {
                    model.set({oneway: "-1"});
                }
            }
        } else if (model.attributes.type == 5) {
            model.set({
                name: $('#redName').val(),
                redlighttime: parseInt($('#redlightT').val())
            });
        } else if (model.attributes.type == 4) {
            model.set({
                name: $('#RPName').val(),
                maxspeed: parseInt($('#maxspeedRP').val())
            });
        }
        model.unset("id");
        var self = this;
        model.save(null, {
            success: function () {
                //self.mapDetailsCOllection.add(model);
                self.cancelUnderCreation();
                self.vectorSource.clear();
                self.mapDetailsCOllection.reset();
                new app.waitMapEditionView();
                self.mapDetailsCOllection.fetch({
                    success: function () {
                        $('#osmInfo').empty();
                    }
                });
            }
        });
    },

    clickOnImport: function () {
        if ($('#modalImport').length) {
            $('#modalImport').remove();
        }
        new app.importModalView();
    },

    importData: function () {
        var f = $('input[type=file]')[0].files[0];
        var extentionFile;
        if (!f.name) {
            extentionFile = "null.null";
        } else {
            var re = /(?:\.([^.]+))?$/;
            extentionFile = re.exec(f.name)[1];
        }
        switch (extentionFile) {
            case 'osm':
                $('#importModalFooter').addClass('hidden');
                $('#formImport').addClass('hidden');
                $('#parseMessage').removeClass('hidden');
                $('#waitImport').removeClass('hidden');
                this.encodeOSMtoGeoJSON(f);
                break;
            case 'json':
                $('#importModalFooter').addClass('hidden');
                $('#formImport').addClass('hidden');
                $('#waitImport').removeClass('hidden');
                this.sendImportData(f);
                break;
            default:
                $('#alertImport').removeClass('hidden');
                break;
        }

    },

    encodeOSMtoGeoJSON: function (osm) {
        if (osm) {
            var r = new FileReader();
            var self = this;
            r.onload = function (e) {
                var contents = e.target.result;
                var parser = new DOMParser();
                var xmlDoc = parser.parseFromString(contents, "text/xml");
                contents = osmtogeojson(xmlDoc);
                contents = JSON.stringify(contents);
                contents = new File([contents, "import.json"], {type: "application/json"});
                $('#parseMessage').addClass('hidden');
                self.sendImportData(contents);
            };
            r.readAsText(osm);
        } else {
            alert("Failed to load file");
        }
    },

    sendImportData: function (dataS) {
        var formData = new FormData();
        formData.append('data', dataS);
        var self = this;
        $.ajax({
                url: Backbone.Collection.prototype.absURL + "/api/maps/" + self.id + "/import",
                type: "POST",
                processData: false,
                data: formData,
                contentType: false
            })
            .done(function (data, textStatus, jqXHR) {
                self.vectorSource.clear();
                self.fetchCollection();
                $('#waitImport').addClass('hidden');
                $('#alertSuccessImport').removeClass('hidden');
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                $('#danger-text-modal').html("<strong>Erreur ! </strong> Désolé, quelque chose s'est mal passée. Veuillez réessayer. (C'est encore le dev qui a du mal bosser...)");
                $('#modalError').modal('show');
            });
    },

    hideModal: function () {
        $('#modalImport').remove();
    }
});
