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
        this.mapDetailsCOllection.on('add', self.onAddElement, self);
        this.mapDetailsCOllection.fetch();
    //    var test = new app.models.mapDetailsModel({ "type": "Feature", "id": "way/4007060", "properties": { "timestamp": "2015-05-07T20:04:46Z", "version": "4", "changeset": "30884050", "user": "brelevenix", "uid": "1467976", "highway": "residential", "name": "Avenue d\'Alsace", "ref:FR:FANTOIR": "221130035A", "id": "way/4007060"}, "geometry": { "type": "LineString", "coordinates": [ [ -3.4744614, 48.7443241], [ -3.4746925, 48.7442887] ]} });
    //    this.mapDetailsCOllection.add(test);
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25024188", "properties": { "timestamp": "2008-06-29T19:31:47Z", "version": "3", "changeset": "334849", "user": "latouche", "uid": "34964", "created_by": "JOSM", "highway": "residential", "name": "Rue de Bourgogne", "id": "way/25024188"}, "geometry": { "type": "LineString", "coordinates": [ [ -3.4757213, 48.7461208], [ -3.4750421, 48.745814], [ -3.4748368, 48.7457856], [ -3.4747675, 48.7457962], [ -3.474621, 48.7458249], [ -3.4742692, 48.7461766], [ -3.4739455, 48.7465203], [ -3.4739175, 48.7466602], [ -3.4739655, 48.7467322], [ -3.4746246, 48.7471609] ]} }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25024189", "properties": { "timestamp": "2008-06-29T15:35:35Z", "version": "3", "changeset": "333867", "user": "latouche", "uid": "34964", "created_by": "JOSM", "highway": "residential", "name": "Rue du Berry", "id": "way/25024189"}, "geometry": { "type": "LineString", "coordinates": [ [ -3.4752599, 48.746677], [ -3.4742692, 48.7461766] ]} }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25024187", "properties": { "timestamp": "2014-08-18T20:08:30Z", "version": "3", "changeset": "24843133", "user": "brelevenix", "uid": "1467976", "highway": "residential", "name": "Avenue d\'Alsace", "ref:FR:FANTOIR": "221130035A", "id": "way/25024187"}, "geometry": { "type": "LineString", "coordinates": [ [ -3.4746925, 48.7442887], [ -3.4749498, 48.7443667], [ -3.475206, 48.7445303], [ -3.4752952, 48.7445479], [ -3.4755653, 48.7447487], [ -3.4757876, 48.7448608], [ -3.4758672, 48.7449329], [ -3.4759634, 48.7450915], [ -3.4759974, 48.7453803], [ -3.4758842, 48.7457767], [ -3.4757213, 48.7461208], [ -3.4752599, 48.746677], [ -3.4749752, 48.7469081], [ -3.4746246, 48.7471609], [ -3.4743065, 48.7472913], [ -3.4738418, 48.7474136], [ -3.4735146, 48.7474764], [ -3.4732954, 48.7474626], [ -3.4730405, 48.7473943] ]} }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200835", "properties": { "timestamp": "2015-04-30T19:11:53Z", "version": "2", "changeset": "30669703", "user": "brelevenix", "uid": "1467976", "highway": "residential", "name": "Avenue d\'Anjou", "id": "way/25200835" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4712173, 48.7466018 ], [ -3.4710585, 48.7465497 ], [ -3.4706962, 48.7463819 ], [ -3.4702515, 48.7462159 ], [ -3.4701008, 48.746145 ], [ -3.4700467, 48.7461159 ], [ -3.4698601, 48.7459255 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200831", "properties": { "timestamp": "2015-05-07T20:16:18Z", "version": "3", "changeset": "30884331", "user": "brelevenix", "uid": "1467976", "highway": "residential", "name": "Avenue de Normandie", "ref:FR:FANTOIR": "221133105M", "id": "way/25200831" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4720637, 48.7457434 ], [ -3.4718953, 48.7458906 ], [ -3.4715263, 48.7462532 ], [ -3.4712173, 48.7466018 ], [ -3.4712118, 48.7466963 ], [ -3.4712144, 48.7469048 ], [ -3.4713349, 48.7471997 ], [ -3.471372, 48.7472913 ], [ -3.4714481, 48.7474704 ], [ -3.471632, 48.74762 ], [ -3.4720767, 48.7478072 ], [ -3.4724759, 48.7479556 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200815", "properties": { "timestamp": "2008-06-29T22:27:46Z", "version": "2", "changeset": "345846", "user": "latouche", "uid": "34964", "created_by": "JOSM", "highway": "residential", "name": "Rue de l\'Artois", "id": "way/25200815" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4717624, 48.7455789 ], [ -3.4719497, 48.7454428 ], [ -3.4720764, 48.745268 ], [ -3.4722117, 48.7450951 ], [ -3.4722831, 48.7450348 ], [ -3.472306, 48.7450283 ], [ -3.4725577, 48.7447491 ], [ -3.4726855, 48.7447566 ], [ -3.4730089, 48.7449446 ], [ -3.4731367, 48.74508 ], [ -3.4737609, 48.7452755 ], [ -3.4738286, 48.7451853 ], [ -3.4737985, 48.7449973 ], [ -3.4737834, 48.7448093 ], [ -3.4738812, 48.7446363 ], [ -3.4740015, 48.7444634 ], [ -3.4741971, 48.744343 ], [ -3.4744614, 48.7443241 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200833", "properties": { "timestamp": "2016-03-17T14:47:19Z", "version": "5", "changeset": "37898730", "user": "Super-Map", "uid": "709289", "highway": "residential", "name": "Avenue de Lorraine", "ref:FR:FANTOIR": "221132863Z", "id": "way/25200833" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4730405, 48.7473943 ], [ -3.4724759, 48.7479556 ], [ -3.4722432, 48.7482011 ], [ -3.4721029, 48.7483086 ], [ -3.4719293, 48.7483942 ], [ -3.4716717, 48.7484747 ], [ -3.4715133, 48.748496 ], [ -3.4713498, 48.7484908 ], [ -3.4709213, 48.7484403 ], [ -3.4706858, 48.748395 ], [ -3.4704694, 48.748358 ], [ -3.4701207, 48.7482897 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200834", "properties": { "timestamp": "2008-06-29T19:40:39Z", "version": "2", "changeset": "334849", "user": "latouche", "uid": "34964", "created_by": "JOSM", "highway": "residential", "name": "Rue du Dauphiné", "id": "way/25200834" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4712144, 48.7469048 ], [ -3.4707286, 48.7469743 ], [ -3.470487, 48.7470296 ], [ -3.4702205, 48.7472382 ], [ -3.4702205, 48.7474931 ], [ -3.4702668, 48.7477365 ], [ -3.4702436, 48.7479567 ], [ -3.4701695, 48.7481565 ], [ -3.4701207, 48.7482897 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200846", "properties": { "timestamp": "2008-06-29T19:31:44Z", "version": "1", "changeset": "334849", "user": "latouche", "uid": "34964", "created_by": "JOSM", "highway": "residential", "id": "way/25200846" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4721244, 48.7461625 ], [ -3.47206, 48.7459977 ], [ -3.4718953, 48.7458906 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200858", "properties": { "timestamp": "2009-09-16T17:05:30Z", "version": "2", "changeset": "2503804", "user": "JJL", "uid": "9780", "highway": "residential", "name": "Rue de Picardie", "id": "way/25200858" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4725942, 48.7469325 ], [ -3.4717844, 48.7470502 ], [ -3.4714975, 48.7471492 ], [ -3.4713349, 48.7471997 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25200853", "properties": { "timestamp": "2015-05-07T20:16:18Z", "version": "5", "changeset": "30884331", "user": "brelevenix", "uid": "1467976", "highway": "residential", "name": "Rue des Flandres", "ref:FR:FANTOIR": "221131585K", "id": "way/25200853" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4730405, 48.7473943 ], [ -3.4728062, 48.7472506 ], [ -3.4727615, 48.7472134 ], [ -3.4726558, 48.7470819 ], [ -3.4725942, 48.7469325 ], [ -3.4724735, 48.7466159 ], [ -3.4724156, 48.7464433 ], [ -3.4723779, 48.7463064 ], [ -3.4723267, 48.7461297 ], [ -3.4722975, 48.7460197 ], [ -3.4722258, 48.7458719 ], [ -3.4720637, 48.7457434 ], [ -3.4717624, 48.7455789 ], [ -3.4713873, 48.7454485 ], [ -3.4710856, 48.7452854 ], [ -3.4708573, 48.7451794 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/25201029", "properties": { "timestamp": "2008-06-29T19:40:37Z", "version": "1", "changeset": "334849", "user": "latouche", "uid": "34964", "created_by": "JOSM", "highway": "residential", "id": "way/25201029" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4715263, 48.7462532 ], [ -3.4717234, 48.7462617 ], [ -3.4721244, 48.7461625 ], [ -3.4723267, 48.7461297 ] ] } }));
    //    this.mapDetailsCOllection.add(new app.models.mapDetailsModel({ "type": "Feature", "id": "way/4007059", "properties": { "timestamp": "2016-04-23T16:56:23Z", "version": "17", "changeset": "38817074", "user": "brelevenix", "uid": "1467976", "cycleway": "lane", "highway": "secondary", "name": "Boulevard d\'Armor", "ref:FR:FANTOIR": "221130056Y", "id": "way/4007059" }, "geometry": { "type": "LineString", "coordinates": [ [ -3.4698601, 48.7459255 ], [ -3.4702875, 48.7457013 ], [ -3.4708573, 48.7451794 ] ] } }));
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
                center: ol.proj.fromLonLat([ -3.4746925, 48.7442887]),
                zoom: 15
            })
        });
        var self = this;
        this.map.on('click', function(evt){
            var pixel = evt.pixel;
            console.log(pixel);
            //loop through all features under this pixel coordinate
            //and save them in array
            self.map.forEachFeatureAtPixel(pixel, function(feature, layer) {
                if (feature) {
                    var encore = new ol.format.GeoJSON();
                    console.log(encore.writeFeature(feature));
                    console.log(feature.getId());
                }else {
                    console.log('feature null');
                }
            });
        });
        return this;

    },

    onAddElement: function(element){

        var points = new Array();
        var coords = element.getCoordinates();
        var style = this.generateStyle(element.getTypeProperties());
        console.log(element.getTypeProperties());
        var type = element.getGeometryType();

        for(var i= 0; i < coords.length; i++)
        {
            points.push(ol.proj.transform([coords[i][0],coords[i][1]], 'EPSG:4326',   'EPSG:3857'));
        }

        var layerLines = new ol.layer.Vector({
            source: new ol.source.Vector({
                features: [new ol.Feature({
                    geometry: new ol.geom.LineString(points, 'XY'),
                    name: 'Line',
                    id: element.attributes.id
                })]
            }),
            style: style
        });
        this.map.addLayer(layerLines);
    },

    clickOnOSM: function(){
        if (this.tile.getOpacity() == 1){
            this.tile.setOpacity(0);
        }else {
            this.tile.setOpacity(1);
        }
    },

    generateStyle: function(type){
        switch (type){
            case 1:
                //SINGLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [255,0,0,1],
                        width: 4
                    })
                });
                return style;
                break;
            case 2:
                //DOUBLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [255,0,0,1],
                        width: 6
                    })
                });
                return style;
                break;
            case 3:
                //TRIPLE ROAD
                var style = new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: [255,0,0,1],
                        width: 10
                    })
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
                        width: 4
                    })
                });
                return style;
                break;
            case 5:
                //RED_LIGHT
                var style = new ol.style.Style({
                    image: new ol.style.Icon({
                        anchor: [0.5, 0.5],
                        size: [52, 52],
                        offset: [52, 0],
                        opacity: 1,
                        scale: 0.25,
                        src: 'http://www.clipartkid.com/images/21/traffic-light-red-clip-art-Ub1tgZ-clipart.png'
                    })
                });
                return style;
                break;
        }
    }
});
