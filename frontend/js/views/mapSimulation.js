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
    value : null,
    vectorSource: null,
    vectorLayer: null,
    selectPointerMove: null,
    selectPointer: null,
    snap: null,
    mapDetailsCollectionSimulation:null,
    stepSeconds: 30, // On veut un step sur le slider de 60 s
    timeSimulation : null,

    events: {
        'change #osmOppacity': 'clickOnOSM',
    },

    initialize: function () {
        this.mapDetailsCollectionSimulation = new app.collections.mapDetailsCollectionSimulation();
        this.render();
        var self = this;
        this.mapDetailsCollectionSimulation.on('sync', self.onSync, self);
        this.mapDetailsCollectionSimulation.on('add', self.onAddElement, self);
    },

    render: function () {
        var self = this;
        //Supression du content
        this.mapDetailsCollectionSimulation.reset();
        $('#content').empty();
        if (this.vectorSource){
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
        this.tile.setOpacity($('#osmOppacity').val());

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

        // Slider for time simulation
        $( "#sliderSimulation" ).slider({
            orientation: "horizontal",
            range: "min",
            min: 0,
            max: 89999, //86400
            step: this.stepSeconds,
            value: 25200, // correspond à 7h
            slide: function( event, ui ) {
                var time = self.convertSecdsToHrsMinsSecds(ui.value);
                console.log('---- slide ----');
                console.log('value : '+ui.value);
                console.log('time : '+time);
                $('#timepicker').timepicker('setTime', time);
            },
            change: function(event,ui){
                console.log('---- change ----');
                console.log('event :'+event);
                //console.log('time : '+time);
            }
        });

        // Time picker
        $('#timepicker').timepicker({
            minuteStep: this.stepSeconds,
            template: false,
            showSeconds: true,
            showMeridian: false,
            defaultTime: "7:00:00",
            modalBackDrop : true
        });

        $('#timepicker').timepicker().on('changeTime.timepicker', function(e) {
            console.log(e.time.hours+':'+e.time.minutes+':'+e.time.seconds);
            var time = (e.time.hours*3600) + (e.time.minutes*60)+ e.time.seconds;
            console.log('ChangeTime time in seconds : '+time);
            $( "#sliderSimulation" ).slider( "option", "value", time );
        });

        return this;
    },

    onSync: function(){
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

    fetchCollection: function () {
        var self = this;
        //this.mapDetailsCOllection.off("add");
        this.mapDetailsCollectionSimulation.fetch({
            success: function(){
                if (self.mapDetailsCollectionSimulation.length > 0){
                self.map.getView().fit(self.vectorSource.getExtent(), self.map.getSize());}

            }
        });
    },

    onAddElement: function(element){
        var geojsonModel = element.toGeoJSON();
        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
            featureProjection: 'EPSG:3857'
        });
        this.vectorSource.addFeature(newfeature);
    },

    clickOnOSM: function(){
        this.tile.setOpacity($('#osmOppacity').val());
    },

    generateStyle: function (feature, resolution) {

        var type = feature.getProperties().type;
        var congestion = feature.getProperties().congestion;
        var color;

        if(congestion == 'LOW'){
            color = [46,204,113,1];
        } else if(congestion == 'MEDIUM'){
            color = [211,84,0,1];
        } else if(congestion == 'HIGH'){
            color = [192,57,43,1];
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
                var angle = feature.getProperties().angle;
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [100, 100],
                        offset: [0, 0],
                        opacity: 1,
                        scale: 0.1 / resolution,
                        src: 'assets/img/car-100.png',
                        rotation : angle
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

    clickCloseInfo: function(){
        $('#osmInfo').empty();
    },

    renderFeatureInformations: function(feature){
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
        var m = Math.floor((seconds%3600)/60);
        var s = (seconds%3600)%60;
        h = h < 10 ? '0' + h : h;
        m = m < 10 ? '0' + m : m;
        s = s < 10 ? '0' + s : s;
        var time = h + ':' + m + ':' + s;
        console.log(time);
        return time;

    },
});
