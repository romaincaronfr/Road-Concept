/**
 * Created by Romain on 16/10/2016.
 */
app.mapDetailsPageView = Backbone.View.extend({

    el: '#body',
    map: null,
    tile: null,

    events: {
        'change #osmOppacity': 'clickOnOSM',
        'click .close_map_info': 'clickCloseInfo',
        'click #importButton': 'clickOnImport',
        'click .importButton': 'importData'
    },


    initialize: function (options) {
        this.id = options.id;
        this.mapDetailsCOllection = new app.collections.mapDetailsCollection({id:this.id});
        this.render();
        var self = this;
        this.mapDetailsCOllection.fetch();
        this.mapDetailsCOllection.on('add', self.onAddElement, self);
        this.mapDetailsCOllection.on('sync', self.onSync, self);
    },

    render: function () {
        $('#content').empty();
        this.$el.append(this.template());
        this.tile = new ol.layer.Tile({
            source: new ol.source.OSM()
        });
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
                center: ol.proj.fromLonLat([ 5.336409, 43.36051]),
                zoom: 14
            })
        });
        this.tile.setOpacity($('#osmOppacity').val());
        var self = this;
        this.map.on('click', function(evt){
            var pixel = evt.pixel;
            console.log(pixel);
            //loop through all features under this pixel coordinate
            //and save them in array
            var featureKeep = null;
            self.map.forEachFeatureAtPixel(pixel, function(feature, layer) {
                if (feature) {
                    //var encore = new ol.format.GeoJSON();
                    //console.log(encore.writeFeature(feature));
                    featureKeep = feature;
                    console.log(feature.getProperties());
                }
            });
            if (featureKeep){
                self.renderFeatureInformations(featureKeep);
            }
        });
        return this;

    },

    onAddElement: function(element){
        //console.log(element);
        //var self = this;
        //var points = new Array();
        //var coords = element.getGPSCoordinates();
        //var style = this.generateStyle(element.getTypeProperties());
        //console.log(element.getTypeProperties());
        //var type = element.getGeometryType();
        //
        //for(var i= 0; i < coords.length; i++)
        //{
        //    points.push(ol.proj.transform([coords[i][0],coords[i][1]], 'EPSG:4326',   'EPSG:3857'));
        //}
        //
        //var layerLines = new ol.layer.Vector({
        //    source: new ol.source.Vector({
        //        features: [new ol.Feature({
        //            geometry: new ol.geom.LineString(points, 'XY'),
        //            name: 'Line',
        //            id: element.attributes.id,
        //            properties: element.attributes.properties
        //        })]
        //    }),
        //    style: function(feature, resolution){
        //        var type = feature.getProperties().properties.type;
        //        console.log('ououuoeueroeour');
        //        return self.generateStyle(type);
        //    }
        //});
        //this.map.addLayer(layerLines);
        //console.log(JSON.stringify(this.mapDetailsCOllection.toJSON()));
    },

    onSync: function(){
        if (this.mapDetailsCOllection.length > 0){
            var geoJson = this.mapDetailsCOllection.toGeoJSON();
            var self = this;
            var featuresSource = new ol.format.GeoJSON().readFeatures(geoJson, {
                featureProjection: 'EPSG:3857'
            });
            var vectorSource = new ol.source.Vector({
                features: featuresSource
            });
            var vectorLayer = new ol.layer.Vector({
                source: vectorSource,
                style: function(feature, resolution) {
                    var type = feature.getProperties().type;
                    var oneway = 1;
                    if (feature.getProperties().oneway && feature.getProperties().oneway == true){
                        console.log("oneway true");
                        oneway = 0.5;
                    }
                    var style = self.generateStyle(type, resolution,oneway);
                    return style;
                }

            });

            this.map.getView().fit(vectorSource.getExtent(), this.map.getSize());
            this.map.addLayer(vectorLayer);
        }
        /*this.map.addLayer(new ol.layer.Vector({
            title: 'added Layer',
            source: new ol.source.Vector({
                url: 'Templates/from-osm-lannion-center.json',
                format: new ol.format.GeoJSON()
            })
        }));*/
        //var test = new app.models.mapDetailsModel({ "type": "Feature", "properties": {"type":3, "timestamp": "2015-05-07T20:04:46Z", "version": "4", "changeset": "30884050", "user": "brelevenix", "uid": "1467976", "highway": "residential", "name": "Avenue d\'Alsace", "ref:FR:FANTOIR": "221130035A"}, "geometry": { "type": "LineString", "coordinates": [ [ -3.4744614, 48.7443241], [ -3.4746925, 48.7442887] ]} },{parse: true});
        //this.mapDetailsCOllection.add(test);
        //test.save();
        //newGeo = test.toGeoJSON();
        //console.log(newGeo);
        //var featuretest = new ol.format.GeoJSON().readFeature(newGeo, {
        //    featureProjection: 'EPSG:3857'
        //});
        //vectorSource.addFeature(featuretest);
        //this.map.getView().fit(vectorSource.getExtent(), this.map.getSize());
    },

    clickOnOSM: function(){
            this.tile.setOpacity($('#osmOppacity').val());
    },

    generateStyle: function(type,resolution,oneway){
        switch (type){
            case 1:
                //SINGLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [241, 196, 15,1],
                        width: (7/resolution)*oneway
                    })
                });
                return style;
                break;
            case 2:
                //DOUBLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [230, 126, 34,1],
                        width: (14/resolution)*oneway
                    })
                });
                return style;
                break;
            case 3:
                console.log("triple road");
                //TRIPLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [231, 76, 60,1],
                        width: (21/resolution)*oneway
                    }),

                });
                return style;
                break;
            case 4:
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: [250,178,102,1]
                    }),
                    stroke: new ol.style.Stroke({
                        color: [0,255,0,1],
                        width: 3.5/resolution
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
                        scale: 0.1/resolution,
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

    clickCloseInfo: function(){
        $('#osmInfo').empty();
    },

    renderFeatureInformations: function(feature){
        var featureid = feature.getProperties().id;
        var model = this.mapDetailsCOllection.get(featureid);
        console.log(model);
        new app.mapPopUpInfoVisuView({
            model: model
        });
    },

    clickOnImport: function(){
        console.log("importButton");
        if ($('#modalImport').length){
            $('#modalImport').remove();
        }
        new app.importModalView();
    },

    importData: function(){
        var f = $('input[type=file]')[0].files[0];
        console.log(f.name);
        var re = /(?:\.([^.]+))?$/;
        var extention = re.exec(f.name)[1];
        switch (extention){
            case 'osm':

                break;
            case 'json':
                this.sendImportData(f);
                break;
            default:
                $('#danger-text-modal').html("<strong>Erreur ! </strong> Désolé, le fichier que vous essayez d'envoyer n'est pas au bon format");
                break;
        }
        console.log(extention);
        if (f) {
            var r = new FileReader();
            r.onload = function(e) {
                var contents = e.target.result;
                console.log(contents);
                var parser = new DOMParser();
                var xmlDoc = parser.parseFromString(contents,"text/xml");
                contents = osmtogeojson(xmlDoc);
                console.log(contents);
            };
            r.readAsText(f);
        } else {
            alert("Failed to load file");
        }
    },

    sendImportData: function(dataS){
        var formData = new FormData();
        formData.append('data',dataS);
        var self = this;
        $.ajax({
                url: Backbone.Collection.prototype.absURL + "/api/maps/"+self.id+"/import",
                type: "POST",
                processData: false,
                data: formData,
                headers: {
                    "Content-Type": "application/json; charset=utf-8"
                },
                contentType: "application/json"
            })
            .done(function (data, textStatus, jqXHR) {
                console.log("HTTP Request Succeeded: " + jqXHR.status);
            })
            .fail(function (jqXHR, textStatus, errorThrown) {
                console.log("HTTP Request Failed : /api/maps/"+self.id+"/import");
            })
            .always(function () {
                /* ... */
            });
    }
});
