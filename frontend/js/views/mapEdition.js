/**
 * Created by anais on 25/10/16.
 */

app.mapEditionView = Backbone.View.extend({

    el: '#body',
    map: null,
    tile: null,
    value : null,
    draw : null,
    vectorSource: null,
    vectorLayer: null,
    selectPointerMove: null,
    selectPointer: null,
    snap: null,
    mapDetailsCOllection:null,
    interactionZoomDoubleClick : null,

    events: {
        'change #osmOppacity': 'clickOnOSM',
        //'click .close_map_info': 'clickCloseInfo',
        'click button[name=chooseTool]': 'hasChooseTool',
        'click button[id=cancel]': 'cancelHasChooseTool',
        'change #onwayRoad': 'selectOneWay',
        'click .validModif': 'validModif',
        'click .removeModif': 'removeModif'

    },

    initialize: function (options) {
        this.id = options.id;
        this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id: this.id});
        this.render();
        var self = this;
        this.mapDetailsCOllection.on('sync', self.onSync, self);
        this.mapDetailsCOllection.on('add', self.onAddElement, self);
        this.mapDetailsCOllection.on('remove', self.onRemoveElement, self);
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

        /**
         * Get the interaction in interactionZoomDoubleClick : zoom when doubleclick
         */
        var interactions = this.map.getInteractions();
        console.log(interactions);
        for (var i = 0; i < interactions.getLength(); i++) {
            var interaction = interactions.item(i);
            if (interaction instanceof ol.interaction.DoubleClickZoom) {
                this.interactionZoomDoubleClick = interaction;
                console.log("interaction Zoom Double Click");
                console.log(this.interactionZoomDoubleClick);
            }
        }
        return this;
    },

    changeID: function (id) {
        this.id = id;
        this.mapDetailsCOllection.id = id;
    },

    onAddElement: function(element){
        console.log("add");
        var geojsonModel = element.toGeoJSON();
        var newfeature = new ol.format.GeoJSON().readFeature(geojsonModel, {
            featureProjection: 'EPSG:3857'
        });
        this.vectorSource.addFeature(newfeature);
    },

    onRemoveElement: function(element){
        console.log("remove");
        $('#osmInfo').empty();
        this.vectorSource.removeFeature(this.vectorSource.getFeatureById(element.attributes.id));
        this.selectPointer.getFeatures().clear();
    },

    onSync: function(){
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

    clickOnOSM: function(){
        this.tile.setOpacity($('#osmOppacity').val());
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

    clickCloseInfo: function(){
        $('#osmInfo').empty();
    },

    renderFeatureInformations: function(feature){
        var featureid = feature.getProperties().id;
        var model = this.mapDetailsCOllection.get(featureid);
        switch (model.attributes.type){
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


    addInteraction : function () {
        console.log('Draw : start '+this.value);
        if(this.value != 'None'){
            this.draw = new ol.interaction.Draw({
                //source: new ol.source.Vector(),
                source: new ol.source.Vector(),
                type: this.value
            });

            this.map.addInteraction(this.draw);
            this.map.addInteraction(this.snap);
            var self = this;
            
            this.draw.on('drawstart', function (evt) {
                console.log('drawStart');

                    self.interactionZoomDoubleClick.setActive(false);

            });

            this.draw.on('drawend', function(event) {
                console.log('Draw : end');
                var feature = event.feature;
                var JSONFeature  = new ol.format.GeoJSON().writeFeature(feature, {
                    dataProjection:'EPSG:3857',
                    featureProjection:'EPSG:3857'
                });
                JSONFeature = JSON.parse(JSONFeature);

                switch (self.value){
                    case 'Polygon':
                        console.log('Polygon');
                        JSONFeature.geometry.coordinates = self.transoformToGps(feature.getGeometry().getCoordinates()[0]);
                        JSONFeature.geometry.type = "LineString";
                        JSONFeature.properties = {type:4, maxspeed : 30};
                        //JSONFeature.properties.intersections = self.getIntersection(feature.getGeometry().getCoordinates()[0]);
                        break;
                    case 'LineString':
                        console.log('LineString');
                        JSONFeature.geometry.coordinates = self.transoformToGps(feature.getGeometry().getCoordinates());
                        JSONFeature.properties = {type:1, maxspeed : 50, oneway: "no"};
                        break;
                    case 'Point':
                        var coord = feature.getGeometry().getCoordinates();
                        coord = ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326');
                        JSONFeature.geometry.coordinates = coord;
                        JSONFeature.properties = {type:5, redlighttime:30};
                        break;
                }
                JSONFeature.properties.name = "Unnamed unit road";
                console.log(JSONFeature);
                var newModel = new app.models.mapDetailsModel(JSONFeature,{parse: true,collection:self.mapDetailsCOllection});
                console.log(newModel);
                console.log(newModel.get('geometry').get('coordinates'));
                newModel.save(null, {
                    success: (function () {
                        console.log('success add');
                        self.mapDetailsCOllection.add(newModel);
                        /*var feature = this.vectorSource.getFeatureById(newModel.attributes.id);
                        var selectFeatures = this.selectPointer.getFeatures();
                        selectFeatures.push(feature);*/
                    })
                });
                //self.mapDetailsCOllection.add(newModel);
                /*var feature = this.vectorSource.getFeatureById(newModel.attributes.id);
                var selectFeatures = this.selectPointer.getFeatures();
                selectFeatures.push(feature);*/
                self.cancelHasChooseTool();
                /*var format = new ol.format.GeoJSON();
                var routeFeatures = format.writeFeatures(feature);
                console.log(format);*/
                setTimeout(function(){
                    self.interactionZoomDoubleClick.setActive(true);
                },251);

            });
        }
    },

    hasChooseTool: function(e) {
        this.value = $(e.currentTarget).attr('value');
        console.log('Tool chosen : '+this.value);
        this.map.removeInteraction(this.selectPointer);
        this.addInteraction(this.value);
        this.changeChooseToolToCancel();
    },

    cancelHasChooseTool: function () {
        $('#editButtonCancel').hide();
        $('#editButtonChooseTool').show();
        this.value = 'None';
        console.log('Draw : Stop');
        this.map.removeInteraction(this.draw);
        this.map.addInteraction(this.selectPointer);
    },

    changeChooseToolToCancel : function(){
        $('#editButtonChooseTool').hide();
        $('#editButtonCancel').show();
    },

    fetchCollection: function () {
        var self = this;
        //this.mapDetailsCOllection.off("add");
        this.mapDetailsCOllection.fetch({
            success: function(){
                self.map.getView().fit(self.vectorSource.getExtent(), self.map.getSize());
            }
        });
    },

    transoformToGps: function(coordinates){
        for (var i=0;i<coordinates.length;i++){
            coordinates[i] = ol.proj.transform(coordinates[i], 'EPSG:3857', 'EPSG:4326');
        }
        return coordinates;
    },

    getIntersection: function(coordinates){
        var intersectionsArray = {};
        for (var i = 0; i<coordinates.length;i++){
            var featureAtCoord = this.vectorSource.getClosestFeatureToCoordinate(coordinates[i]);
            this.map.forEachFeatureAtPixel(coordinates[i],function(feature, layer){
                console.log(coordinates[i]);
                console.log(feature);
            });
            if (featureAtCoord.length > 0){
                console.log("intersection");
                for (var j = 0; j<featureAtCoord.length;j++){
                    var id = featureAtCoord[j].getId;
                    if (intersectionsArray[i]){
                        intersectionsArray[i].push(id);
                    } else {
                        intersectionsArray[i] = [id];
                    }
                }
            }
        }
        console.log(intersectionsArray);
        return intersectionsArray;
    },

    selectOneWay: function(event){
        if ($('#onwayRoad').val() == "no"){
            $('#wayDiv').addClass('hidden');
        } else {
            $('#wayDiv').removeClass('hidden');
        }
    },

    validModif: function(event){
        var id = event.currentTarget.id;
        console.log(id);
        var model = this.mapDetailsCOllection.get(id);
        console.log(model);
        if (model.attributes.type == 1 || model.attributes.type == 2 ||model.attributes.type == 3) {
            console.log('if ok');
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
        } else if (model.attributes.type == 5){
            model.set({
                name: $('#redName').val(),
                redlighttime: parseInt($('#redlightT').val())
            });
        } else if (model.attributes.type == 4){
            model.set({
                name: $('#RPName').val(),
                maxspeed: parseInt($('#maxspeedRP').val())
            });
        }
        var self = this;
        model.save(null,{
            success: function(){
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
        console.log(model.attributes);
    },

    removeModif: function(event){
        var id = event.currentTarget.id;
        id = id.replace('removeRoad_', '');
        var model = this.mapDetailsCOllection.get(id);
        model.destroy({wait: true});
    }


});
