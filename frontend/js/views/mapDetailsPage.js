/**
 * Created by Romain on 16/10/2016.
 */
app.mapDetailsPageView = Backbone.View.extend({

    el: '#body',
    map: null,
    tile: null,
    vectorSource: null,
    vectorLayer: null,
    selectPointerMove: null,
    selectPointer: null,

    events: {
        //'click .close_map_info': 'clickCloseInfo',
        'click #importButton': 'clickOnImport',
        'click .importButton': 'importData',
        'hide.bs.modal #modalImport': 'hideModal'
    },


    initialize: function (options) {
        this.id = options.id;
        this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id: this.id});
        this.render();
        var self = this;
        this.mapDetailsCOllection.on('sync', self.onSync, self);
    },

    render: function () {
        //Supression du content
        this.mapDetailsCOllection.reset();
        $('#content').empty();
        if (this.vectorSource){
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
        this.mapDetailsCOllection.fetch();

        $( "#osmSlider" ).slider({
            orientation: "vertical",
            range: "min",
            min: 0,
            max: 1,
            step: 0.1,
            value: 0.3,
            slide: function( event, ui ) {
                self.changeOppacity(ui.value);
            }
        });

        return this;

    },

    changeID: function (id) {
        this.id = id;
        this.mapDetailsCOllection.id = id;
    },

    onSync: function () {
        if (this.mapDetailsCOllection.length > 0) {
            this.vectorSource.clear();
            var geoJson = this.mapDetailsCOllection.toGeoJSON();
            var featuresSource = new ol.format.GeoJSON().readFeatures(geoJson, {
                featureProjection: 'EPSG:3857'
            });
            this.vectorSource.addFeatures(featuresSource);
            this.map.getView().fit(this.vectorSource.getExtent(), this.map.getSize());
        }
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

    generateSelectStyle: function(feature,resolution){
        var type = feature.getProperties().type;
        var geometry = feature.getGeometry();
        var startCoord = geometry.getFirstCoordinate();
        var endCoord = geometry.getLastCoordinate();
        var oneway = 1;
        var circle = new ol.style.Circle({
            stroke: new ol.style.Stroke({
                color: [50, 50, 50,1]
            }),
            fill: new ol.style.Fill({
                color: [200, 200, 200,0.8]
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
        if (feature.getProperties().oneway && feature.getProperties().oneway == true){
            console.log("oneway true");
            oneway = 0.5;
        }
        switch (type){
            case 1:
                //SINGLE ROAD
                var styles = [
                    // linestring
                    new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: [26, 155, 252, 1],
                            width: ((7+2)/resolution)*oneway
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
                            width: ((14+2)/resolution)*oneway
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
                            width: ((21+1)/resolution)*oneway
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
                        color: [250,178,102,1]
                    }),
                    stroke: new ol.style.Stroke({
                        color: [26, 155, 252, 1],
                        width: (3.5+2)/resolution
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
                        scale: (0.1 + 0.2)/resolution,
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
        //this.selectPointer.getFeatures().clear();
        $('#osmInfo').empty();
    },

    renderFeatureInformations: function (feature) {
        var featureid = feature.getProperties().id;
        var model = this.mapDetailsCOllection.get(featureid);
        new app.mapPopUpInfoVisuView({
            model: model
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
                console.log("HTTP Request Succeeded: " + jqXHR.status);
                self.fetchCollection();
                $('#waitImport').addClass('hidden');
                $('#alertSuccessImport').removeClass('hidden');
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                $('#danger-text-modal').html("<strong>Erreur ! </strong> Désolé, quelque chose s'est mal passée. Veuillez réessayer. (C'est encore le dev qui a du mal bosser...)");
                $('#modalError').modal('show');
            })
            .always(function () {
                /* ... */
            });
    },

    hideModal: function () {
        $('#modalImport').remove();
    },

    fetchCollection: function () {
        this.mapDetailsCOllection.fetch();
    }
});
