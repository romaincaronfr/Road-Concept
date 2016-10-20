/**
 * Created by Romain on 16/10/2016.
 */
app.mapDetailsPageView = Backbone.View.extend({

    el: '#mapRow',
    map: null,
    tile: null,

    events: {
        'click #osmButton': 'clickOnOSM'
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
        this.$el.html(this.template());
        this.tile = new ol.layer.Tile({
            source: new ol.source.OSM()
        });
        this.map = new ol.Map({
            target: 'map',
            layers: [
                this.tile
            ],
            view: new ol.View({
                center: ol.proj.fromLonLat([ 5.336409, 43.36051]),
                zoom: 14
            })
        });
        var self = this;
        //this.map.on('click', function(evt){
        //    var pixel = evt.pixel;
        //    console.log(pixel);
        //    //loop through all features under this pixel coordinate
        //    //and save them in array
        //    self.map.forEachFeatureAtPixel(pixel, function(feature, layer) {
        //        if (feature) {
        //            //var encore = new ol.format.GeoJSON();
        //            //console.log(encore.writeFeature(feature));
        //            //console.log(feature.getId());
        //            console.log(feature.getProperties().properties);
        //        }else {
        //            console.log('feature null');
        //        }
        //    });
        //});
        return this;

    },

    onAddElement: function(element){
        console.log("new element");
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
        console.log('sync');
        if (this.mapDetailsCOllection.length > 0){
            console.log('if ok');
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
                style: function(feature, resolution){
                    var type = feature.getProperties().type;
                    return self.generateStyle(type,resolution);
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
    },

    clickOnOSM: function(){
        if (this.tile.getOpacity() == 1){
            this.tile.setOpacity(0);
        }else {
            this.tile.setOpacity(1);
        }
    },

    generateStyle: function(type,resolution){
        console.log(resolution);
        switch (type){
            case 1:
                //SINGLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [254,234,178,1],
                        width: 4/resolution
                    })
                });
                return style;
                break;
            case 2:
                //DOUBLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [253,215,160,1],
                        width: 6/resolution
                    })
                });
                return style;
                break;
            case 3:
                //TRIPLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [253,215,160,1],
                        width: 10/resolution
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
                        width: 4/resolution
                    })
                });
                return style;
                break;
            case 5:
                //RED_LIGHT
                console.log('red light');
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [100, 200],
                        offset: [0, 0],
                        opacity: 1,
                        scale: 0.1,
                        src: 'http://www.clipartkid.com/images/21/traffic-light-red-clip-art-Ub1tgZ-clipart.png'
                    })
                });
                return style;
                break;
        }
    }
});
