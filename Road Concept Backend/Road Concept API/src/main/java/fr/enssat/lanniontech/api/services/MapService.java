package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.*;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.repositories.MapFeatureRepository;
import fr.enssat.lanniontech.api.repositories.MapInfoRepository;
import fr.enssat.lanniontech.api.utilities.GlobalUtils;
import fr.enssat.lanniontech.api.utilities.JSONHelper;
import fr.enssat.lanniontech.api.utilities.MathsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MapService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);

    private static final String[] OSM_HIGHWAY_TO_CONSERVE = {"motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential", "motorway_link", "trunk_link", "primary_link", "secondary_link", "tertiary_link", "road", "traffic_signals"};

    private MapInfoRepository mapInfoRepository = new MapInfoRepository();
    private MapFeatureRepository mapFeatureRepository = new MapFeatureRepository();

    public MapInfo create(User user, String name, boolean fromOSM, String imageURL, String description) {
        return mapInfoRepository.create(user, name, fromOSM, imageURL, description);
    }

    public List<MapInfo> getAllMapsInfo(User user) {
        return mapInfoRepository.getAll(user);
    }

    public fr.enssat.lanniontech.api.entities.map.Map getMap(int userID, int mapID) {
        MapInfo infos = mapInfoRepository.get(mapID);

        if (infos == null) {
            throw new EntityNotExistingException(MapInfo.class);
        }
      //  checkAccessMap(user, infos); //FIXME

        FeatureCollection features = mapFeatureRepository.getAll(mapID);
        fr.enssat.lanniontech.api.entities.map.Map map = new fr.enssat.lanniontech.api.entities.map.Map();
        map.setInfos(infos);
        map.setFeatures(features);
        return map;
    }
    public fr.enssat.lanniontech.api.entities.map.Map getMap(User user, int mapID) {
        return getMap(user.getId(),mapID);
    }

    public Feature getFeature(int mapID, UUID featureUUID) {
        Feature feature = mapFeatureRepository.getFromUUID(mapID, featureUUID);
        if (feature == null) {
            throw new EntityNotExistingException(Feature.class);
        }
        return feature;
    }

    public Feature updateFeature(int mapID, Feature feature) {
        long count = mapFeatureRepository.delete(mapID, feature.getUuid());
        LOGGER.debug("Deleted count = " + count);
        if (count == 0) {
            throw new EntityNotExistingException(Feature.class);
        }
        return mapFeatureRepository.create(mapID, feature);
    }

    public boolean delete(Integer mapID) {
        mapFeatureRepository.deleteAll(mapID);
        int count = mapInfoRepository.delete(mapID);
        return count == 1; // // If false, something goes wrong (0 or more than 1 rows deleted)
    }

    // =======================
    // OPEN STREET MAP IMPORTS
    // =======================

    public int importFromOSM(int mapID, String fileData) throws Exception {
        MapInfo infos = mapInfoRepository.get(mapID);
        if (infos == null) {
            throw new EntityNotExistingException();
        }

        FeatureCollection features = JSONHelper.fromJSON(fileData, FeatureCollection.class);
        fromOSMAdaptation(features);

        FeatureCollection toAdd = new FeatureCollection();
        for (Feature feature : features) {
            Feature retrieved = mapFeatureRepository.getFromOSMID(mapID, feature.getOpenStreetMapID());
            if (retrieved == null) {
                toAdd.getFeatures().add(feature);
            }
        }

        //        for (Feature one : toAdd) {
        //            for (Feature two : toAdd) {
        //                if (!(one == two)) {
        //                    detectIntersections(mapID, one, two, false);
        //                }
        //            }
        //        }

        if (!toAdd.getFeatures().isEmpty()) {
            mapFeatureRepository.createAll(mapID, toAdd);
        }
        LOGGER.debug("Duplicated features : " + (features.getFeatures().size() - toAdd.getFeatures().size()));
        return toAdd.getFeatures().size();

    }

    //FIXME: refactor + extraire en constante les colonnes
    public void fromOSMAdaptation(FeatureCollection features) {
        for (Iterator<Feature> iterator = features.getFeatures().iterator(); iterator.hasNext(); ) {
            Feature feature = iterator.next();

            // Delete all non 'LineString' features
            if (!(feature.getGeometry() instanceof LineString)) {
                if (feature.getGeometry() instanceof Point) {
                    Map tags = (LinkedHashMap) feature.getProperties().get("tags");
                    String highway = (String) tags.get("highway");
                    if (!"traffic_signals".equals(highway)) {
                        iterator.remove();
                    } else {
                        computeProperties(feature);
                    }
                } else {
                    iterator.remove();
                }
            } else {
                if (feature.getProperties().containsKey("tags")) {
                    Map tags = (LinkedHashMap) feature.getProperties().get("tags");
                    String highway = (String) tags.get("highway");
                    if (highway == null) {
                        iterator.remove();
                    } else {
                        if (!Arrays.asList(OSM_HIGHWAY_TO_CONSERVE).contains(highway)) {
                            iterator.remove();
                        } else {
                            computeProperties(feature);
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
        }
    }

    private void computeProperties(Feature feature) {
        Map<String, Object> newProperties = new HashMap<>();
        newProperties.put("type", getType(feature.getProperties()));
        newProperties.put("name", getName(feature.getProperties()));
        newProperties.put("id", feature.getUuid());
        newProperties.put("oneway", getOneWay(feature.getProperties()));
        newProperties.put("bridge", getBridge(feature.getProperties()));
        newProperties.put("maxspeed", getMaxSpeed(feature.getProperties()));
        Integer time = getRedLightTime(feature.getProperties());
        if (time != null) { //TODO: Vérifier que à null, il n'est pas mis dans les properties
            newProperties.put("redlighttime", time);
        }
        feature.getProperties().clear();
        feature.getProperties().putAll(newProperties);
    }

    private FeatureType getType(Map<String, Object> properties) {
        Map tags = (LinkedHashMap) properties.get("tags");
        if (tags.containsKey("junction")) {
            String junction = (String) tags.get("junction");
            if (junction.equals("roundabout")) {
                return FeatureType.ROUNDABOUT;
            }
        }

        if (tags.containsKey("lanes")) {
            Integer lanes = Integer.valueOf((String) tags.get("lanes"));
            if (lanes >= 3) {
                return FeatureType.TRIPLE_ROAD;
            } else if (lanes > 1) {
                return FeatureType.DOUBLE_ROAD;
            } else if (lanes == 1) {
                return FeatureType.SINLGE_ROAD;
            }
        }

        String highway = (String) tags.get("highway");
        switch (highway) {
            case "motorway":
                return FeatureType.DOUBLE_ROAD;
            case "trunk":
                return FeatureType.DOUBLE_ROAD;
            case "primary":
                return FeatureType.SINLGE_ROAD;
            case "secondary":
                return FeatureType.SINLGE_ROAD;
            case "tertiary":
                return FeatureType.SINLGE_ROAD;
            case "residential":
                return FeatureType.SINLGE_ROAD;
            case "motorway_link":
                return FeatureType.TRIPLE_ROAD;
            case "trunk_link":
                return FeatureType.DOUBLE_ROAD;
            case "primary_link":
                return FeatureType.SINLGE_ROAD;
            case "secondary_link":
                return FeatureType.SINLGE_ROAD;
            case "tertiary_link":
                return FeatureType.SINLGE_ROAD;
            case "road":
                return FeatureType.SINLGE_ROAD;
            case "traffic_signals":
                return FeatureType.RED_LIGHT;
            default:
                return FeatureType.SINLGE_ROAD;
        }
    }

    private Object getName(Map<String, Object> properties) {
        if (properties.containsKey("name")) {
            return properties.get("name");
        }
        Map tags = (LinkedHashMap) properties.get("tags");
        if (tags.containsKey("name")) {
            return tags.get("name");
        }
        return "Unnamed unit road";
    }

    private String getOneWay(Map<String, Object> properties) {
        if (properties.containsKey("oneway")) {
            return (String) properties.get("oneway");
        }
        Map tags = (LinkedHashMap) properties.get("tags");
        if (tags.containsKey("oneway")) {
            return (String) tags.get("oneway");
        }
        return null;
    }

    private boolean getBridge(Map<String, Object> properties) {
        if (properties.containsKey("bridge")) {
            String oneway = (String) properties.get("bridge");
            if (oneway.equals("yes")) {
                return true;
            }
        }
        if (properties.containsKey("tags")) {
            Map tags = (LinkedHashMap) properties.get("tags");
            if (tags.containsKey("bridge")) {
                String oneway = (String) tags.get("bridge");
                if (oneway.equals("yes")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer getMaxSpeed(Map<String, Object> properties) {
        if (properties.containsKey("maxspeed")) {
            return Integer.valueOf((String) properties.get("maxspeed"));
        }
        Map tags = (LinkedHashMap) properties.get("tags");
        if (tags.containsKey("maxspeed")) {
            return Integer.valueOf((String) tags.get("maxspeed"));
        }
        if (getType(properties) != null) {
            switch (getType(properties)) {
                case SINLGE_ROAD:
                    return 50;
                case DOUBLE_ROAD:
                    return 90;
                case TRIPLE_ROAD:
                    return 130;
                case ROUNDABOUT:
                    return 40;
                case RED_LIGHT:
                    return 0;
                default:
                    break;
            }
        }
        return Integer.MIN_VALUE;
    }

    private Integer getRedLightTime(Map<String, Object> properties) {
        if (getType(properties) == FeatureType.RED_LIGHT) {
            return 30; // Default value
        }
        return null;
    }

    public void deleteFeature(int mapID, UUID featureUUID) {
        mapFeatureRepository.delete(mapID, featureUUID);
    }

    //=============================================================

    public Feature addFeature(int mapID, Feature feature) {
        int intersectionsDetectedCount = 0;

        LineString createdRoad = (LineString) feature.getGeometry();

        Map<UUID, Feature> tab = new HashMap<>();
        Map<UUID, List<Boolean>> tabBool = new HashMap<>();

        List<Boolean> splitNewFeatureAt = new ArrayList<>(Arrays.asList(new Boolean[createdRoad.getCoordinates().size()]));
        Collections.fill(splitNewFeatureAt, Boolean.FALSE);

        List<List<String>> potentialIntersections = (List<List<String>>) feature.getProperties().get("intersections");

        for (int i = 0; i < potentialIntersections.size(); i++) {
            if (!potentialIntersections.get(i).isEmpty()) {

                for (String uuidToCheck : potentialIntersections.get(i)) {
                    UUID uuid = UUID.fromString(uuidToCheck);
                    if (!tab.containsKey(uuid)){
                        Feature featureToCheck = getFeature(mapID, uuid);
                        Feature duplicatedFeatureToCheck = GlobalUtils.cloner.deepClone(featureToCheck);
                        tab.put(uuid,duplicatedFeatureToCheck);
                        LineString lineString = (LineString) duplicatedFeatureToCheck.getGeometry();
                        List<Boolean> newList = new ArrayList<>(Arrays.asList(new Boolean[lineString.getCoordinates().size()]));
                        Collections.fill(newList, Boolean.FALSE);
                        tabBool.put(uuid,newList);
                    }
                    Feature duplicatedFeatureToCheck = tab.get(uuid);

                    LineString coordinaToCheck = (LineString) duplicatedFeatureToCheck.getGeometry();

                    for (int j = 0; j < coordinaToCheck.getCoordinates().size() - 2; j++) {
                        Coordinates firstPointRoadA = coordinaToCheck.getCoordinates().get(j); // A = to check
                        Coordinates lastPointRoadA = coordinaToCheck.getCoordinates().get(j + 1);

                        Coordinates firstPointRoadB;
                        Coordinates lastPointRoadB;
                        if (i == createdRoad.getCoordinates().size()-1){
                            firstPointRoadB = new Coordinates(MathsUtils.roundGPS(createdRoad.getCoordinates().get(i-1).getLongitude()),MathsUtils.roundGPS(createdRoad.getCoordinates().get(i-1).getLatitude()));
                            lastPointRoadB = new Coordinates(MathsUtils.roundGPS(createdRoad.getCoordinates().get(i).getLongitude()),MathsUtils.roundGPS(createdRoad.getCoordinates().get(i).getLatitude()));
                        }else {
                            firstPointRoadB = new Coordinates(MathsUtils.roundGPS(createdRoad.getCoordinates().get(i).getLongitude()),MathsUtils.roundGPS(createdRoad.getCoordinates().get(i).getLatitude()));
                            lastPointRoadB = new Coordinates(MathsUtils.roundGPS(createdRoad.getCoordinates().get(i+1).getLongitude()),MathsUtils.roundGPS(createdRoad.getCoordinates().get(i+1).getLatitude()));
                        }
                        boolean intersectionPoint = MathsUtils.intersect(firstPointRoadA, lastPointRoadA, firstPointRoadB, lastPointRoadB);
                        LOGGER.debug("@@@ Intersection detected : " + intersectionPoint);
                        LOGGER.debug("@@@ j = : " + j);
                        if (intersectionPoint) {
                            LOGGER.debug("@@@ firstPointRoadA = "+firstPointRoadA+" lastPointRoadA = "+lastPointRoadA);
                            LOGGER.debug("@@@ INTERSECTION TRUE");
                            intersectionsDetectedCount++;
                            splitNewFeatureAt.set(i, true);
                            coordinaToCheck.getCoordinates().add(j+1,createdRoad.getCoordinates().get(i));
                            tabBool.get(uuid).add(j+1,true);
                            break;
                        }
                    }
                }
            }
        }

        for (Map.Entry<UUID,Feature> entry : tab.entrySet()){
            /*LineString original = (LineString) mapFeatureRepository.getFromUUID(mapID,entry.getKey()).getGeometry();
            LineString modify = (LineString) entry.getValue().getGeometry();
            LOGGER.debug("@@@ Size original : "+original.getCoordinates().size()+" modify = "+modify.getCoordinates().size());
            LOGGER.debug("@@@ Size boolean original : "+tabBool.get(entry.getKey()).size());*/
            List<Feature> newFeatures = new ArrayList<>();
            LineString lineString = (LineString) entry.getValue().getGeometry();
            int size = lineString.getCoordinates().size();
            Feature currentFeature = entry.getValue();
            int start = 0;
            for (int i = 0; i<size; i++){
                LOGGER.debug("@@@ i = "+i+" & start = "+start);
                if (tabBool.get(entry.getKey()).get(i)){
                    //LineString nmynew = (LineString) currentFeature.getGeometry();
                    //LOGGER.debug("Coordonées à split = "+nmynew.getCoordinates().get(compteur));
                    Feature splitFeatures = newSplit(currentFeature,start,i);
                    newFeatures.add(splitFeatures);
                    //LineString mynew0 = (LineString) splitFeatures.get(0).getGeometry();
                    //LineString mynew1 = (LineString) splitFeatures.get(1).getGeometry();
                    /*LOGGER.debug("Split feature [0] first point = "+mynew0.getCoordinates().get(0));
                    LOGGER.debug("Split feature [0] last point = "+mynew0.getCoordinates().get(mynew0.getCoordinates().size()-1));
                    LOGGER.debug("Split feature [1] first point = "+mynew1.getCoordinates().get(0));
                    LOGGER.debug("Split feature [1] last point = "+mynew1.getCoordinates().get(mynew1.getCoordinates().size()-1));*/
                    //tabBool.get(entry.getKey()).set(i,false);
                    //i--;
                    start = i;
                }
            }
            LineString nmynew = (LineString) currentFeature.getGeometry();
            Feature lastFeature = newSplit(currentFeature,start,nmynew.getCoordinates().size()-1);
            newFeatures.add(lastFeature);
            mapFeatureRepository.delete(mapID,entry.getKey());
            for (Feature feature1 : newFeatures){
                mapFeatureRepository.create(mapID,feature1);
            }
        }

        List<Feature> newFeatures = new ArrayList<>();
        LineString lineString = (LineString) feature.getGeometry();
        int size = lineString.getCoordinates().size();
        Feature currentFeature = feature;
        int start = 0;
        for (int i = 0; i<size; i++){
            LOGGER.debug("@@@ i = "+i+" & start = "+start);
            if (splitNewFeatureAt.get(i)){
                Feature splitFeatures = newSplit(currentFeature,start,i);
                newFeatures.add(splitFeatures);
                start = i;
            }
        }
        LineString nmynew = (LineString) currentFeature.getGeometry();
        Feature lastFeature = newSplit(currentFeature,start,nmynew.getCoordinates().size()-1);
        newFeatures.add(lastFeature);
        for (Feature feature1 : newFeatures){
            mapFeatureRepository.create(mapID,feature1);
        }
        LOGGER.debug("Nombre d'intersections détectées : " + intersectionsDetectedCount);
        return null;

         /*for (Entry<UUID, List<Tuple<Coordinates, Coordinates>>> entry : tab.entrySet()) {
            List<Feature> myNewFeatures = new ArrayList<>();
            Feature myFirstFeature = getFeature(mapID,entry.getKey());
            myNewFeatures.add(myFirstFeature);
            for (Tuple<Coordinates, Coordinates> tuple : entry.getValue()) {
                //myBoucleToBreak:
                int size = myNewFeatures.size();
                for (int i = 0; i < size; i++) {
                    LineString myLineString = (LineString) myNewFeatures.get(i).getGeometry();
                    for (int j = 0; j < myLineString.getCoordinates().size(); j++) {
                        if (tuple.getLeft().equals(myLineString.getCoordinates().get(j))) {
                            if (!tuple.getRight().equals(myLineString.getCoordinates().get(0)) && !tuple.getRight().equals(myLineString.getCoordinates().get(myLineString.getCoordinates().size() - 1))) {
                                List<Feature> mySplitFeatures = splitOldFeature(myNewFeatures.get(i), j, tuple.getRight());
                                myNewFeatures.addAll(mySplitFeatures);
                                myNewFeatures.remove(i);
                            }
                            //break myBoucleToBreak;
                        }
                    }
                }
            }
            mapFeatureRepository.delete(mapID, myFirstFeature.getUuid());
            for (Feature toAdd : myNewFeatures) {
                mapFeatureRepository.create(mapID, toAdd);
            }
        }*/
    }

    private LinkedList<Feature> split(Feature featureToSplit, int index){
        Map<String,Object> properties = featureToSplit.getProperties();
        Feature newOneRoad1 = new Feature();
        Feature newOneRoad2 = new Feature();
        newOneRoad1.setProperties(new HashMap<>(properties));
        newOneRoad2.setProperties(new HashMap<>(properties));
        newOneRoad1.setGeometry(new LineString());
        newOneRoad2.setGeometry(new LineString());
        newOneRoad1.getProperties().remove("id");
        newOneRoad2.getProperties().remove("id");
        newOneRoad1.getProperties().put("id", newOneRoad1.getUuid());
        newOneRoad2.getProperties().put("id", newOneRoad2.getUuid());


        LineString lineString = (LineString) featureToSplit.getGeometry();
        LinkedList<Coordinates> oneRoadFirstPart = new LinkedList<>();
        oneRoadFirstPart.addAll(lineString.getCoordinates().subList(0, index + 1));
        LOGGER.debug("@@@ First size = "+oneRoadFirstPart.size());
        LinkedList<Coordinates> oneRoadLastPart = new LinkedList<>();
        oneRoadLastPart.addAll(lineString.getCoordinates().subList(index,lineString.getCoordinates().size()));
        LOGGER.debug("@@@ Second size = "+oneRoadLastPart.size());

        ((LineString) newOneRoad1.getGeometry()).setCoordinates(oneRoadFirstPart);
        ((LineString) newOneRoad2.getGeometry()).setCoordinates(oneRoadLastPart);

        LinkedList<Feature> listToReturn = new LinkedList<>();
        listToReturn.add(newOneRoad1);
        listToReturn.add(newOneRoad2);
        return listToReturn;
    }

    private Feature newSplit (Feature featureToSplit, int start, int stop){
        Map<String,Object> properties = featureToSplit.getProperties();
        Feature newOneRoad1 = new Feature();
        newOneRoad1.setProperties(new HashMap<>(properties));
        newOneRoad1.setGeometry(new LineString());
        newOneRoad1.getProperties().remove("id");
        newOneRoad1.getProperties().put("id", newOneRoad1.getUuid());


        LineString lineString = (LineString) featureToSplit.getGeometry();
        LinkedList<Coordinates> oneRoadFirstPart = new LinkedList<>();
        oneRoadFirstPart.addAll(lineString.getCoordinates().subList(start, stop+1));
        LOGGER.debug("@@@ First size = "+oneRoadFirstPart.size());
        ((LineString) newOneRoad1.getGeometry()).setCoordinates(oneRoadFirstPart);
        return newOneRoad1;
    }
}

