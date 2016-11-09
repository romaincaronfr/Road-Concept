package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.FeatureType;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.entities.geojson.Point;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.repositories.MapFeatureRepository;
import fr.enssat.lanniontech.api.repositories.MapInfoRepository;
import fr.enssat.lanniontech.api.utilities.JSONHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public fr.enssat.lanniontech.api.entities.map.Map getMap(User user, int mapID) {
        MapInfo infos = mapInfoRepository.get(mapID);

        if (infos == null) {
            throw new EntityNotExistingException(MapInfo.class);
        }
        checkAccessMap(user, infos);

        FeatureCollection features = mapFeatureRepository.getAll(mapID);
        fr.enssat.lanniontech.api.entities.map.Map map = new fr.enssat.lanniontech.api.entities.map.Map();
        map.setInfos(infos);
        map.setFeatures(features);
        return map;
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

        for (Feature one : toAdd) {
            for (Feature two : toAdd) {
                if (!(one == two)) {
                    detectIntersections(mapID, one, two, false);
                }
            }
        }

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

    public Feature addFeature(int mapID, Feature feature) {
        List<List<String>> potentialIntersections = (List<List<String>>) feature.getProperties().get("intersections");
        boolean intersectionDeteted = false;

        for (List<String> potentialIntersection : potentialIntersections) {
            if (!potentialIntersection.isEmpty()) { //FIXME: C'est une intersections potential au point i de la nouvelle route
                LOGGER.debug("Working on potential intersection : " + potentialIntersection);

                for (String uuidToCheck : potentialIntersection) {
                    Feature featureToCheck = getFeature(mapID, UUID.fromString(uuidToCheck));
                    boolean tmp = detectIntersections(mapID, feature, featureToCheck, true, indexToCheck);
                    if (!intersectionDeteted && tmp) {
                        intersectionDeteted = true;
                    }
                }
            }
        }

        // Ne pas create si les intersections ont fait un split
        if (!intersectionDeteted) {
            feature.getProperties().put("id", feature.getUuid());
            mapFeatureRepository.create(mapID, feature);
        }
        return null; //mapFeatureRepository.create(mapID, feature);
    }

    private boolean detectIntersections(int mapID, Feature one, Feature two, boolean fullMode, Integer indexToCheck) {
        return fullMode ? detectFullIntersections(mapID, one, two, indexToCheck) : detectBasicIntersections(mapID, one, two);
    }

    private boolean detectFullIntersections(int mapID, Feature one, Feature two, int indexToCheck) { // indexto check index d'un point de la feature B (la nouvelle)
        LOGGER.debug("Start detect full intersections");
        if (one.getGeometry() instanceof LineString && two.getGeometry() instanceof LineString) {

            LineString oneRoad = (LineString) one.getGeometry();
            LineString twoRoad = (LineString) two.getGeometry();

            for (Coordinates coord : oneRoad.getCoordinates()) {

            }




            // FIXME: Ne pas faire entre premier et dernier point mais il faut le faire entre chaque segment 2 à 2 en coordonées
            Coordinates firstPointRoadA = oneRoad.getCoordinates().getFirst();
            Coordinates lastPointRoadA = oneRoad.getCoordinates().getLast();
            Coordinates firstPointRoadB = twoRoad.getCoordinates().getFirst();
            Coordinates lastPointRoadB = twoRoad.getCoordinates().getLast();

            Coordinates intersectionPoint = FuckIt.intersect(firstPointRoadA, lastPointRoadA, firstPointRoadB, lastPointRoadB);
            LOGGER.debug("intersection point computed = " + intersectionPoint);
            if (intersectionPoint != null) {
                oneRoad.add(intersectionPoint); //FIXME: Faut il trier pour insérer au bon endroit ?
                twoRoad.add(intersectionPoint);
                return detectBasicIntersections(mapID, one, two);
            }
        }
        return false;
    }

    // Détection d'intersection quand deux features ont un point en commun (pour OSM)
    private boolean detectBasicIntersections(int mapID, Feature one, Feature two) {
        boolean result = false;
        if (one.getGeometry() instanceof LineString && two.getGeometry() instanceof LineString) {
            LineString oneRoad = (LineString) one.getGeometry();
            LineString twoRoad = (LineString) two.getGeometry();

            for (Coordinates oneCoord : oneRoad.getCoordinates()) {
                for (Coordinates twoCoord : twoRoad.getCoordinates()) {
                    if (oneCoord.getLatitude() == twoCoord.getLatitude() && oneCoord.getLongitude() == twoCoord.getLongitude()) {
                        LOGGER.debug("Intersection détectée au point : " + oneCoord);

                        int oneIntersectionPointIndex = oneRoad.getCoordinates().indexOf(oneCoord); // oneCoord == twoCoord
                        int twoIntersectionPointIndex = twoRoad.getCoordinates().indexOf(twoCoord);

                        Feature newOneRoad1 = new Feature();
                        Feature newOneRoad2 = new Feature();
                        newOneRoad1.setProperties(one.getProperties());
                        newOneRoad2.setProperties(one.getProperties());
                        newOneRoad1.setGeometry(new LineString());
                        newOneRoad2.setGeometry(new LineString());

                        Feature newTwoRoad1 = new Feature();
                        Feature newTwoRoad2 = new Feature();
                        newTwoRoad1.setProperties(one.getProperties());
                        newTwoRoad2.setProperties(one.getProperties());
                        newTwoRoad1.setGeometry(new LineString());
                        newTwoRoad2.setGeometry(new LineString());

                        LinkedList<Coordinates> oneRoadFirstPart = new LinkedList<>();
                        oneRoadFirstPart.addAll(oneRoad.getCoordinates().subList(0, oneIntersectionPointIndex));
                        LinkedList<Coordinates> oneRoadLastPart = new LinkedList<>();
                        oneRoadLastPart.addAll(oneRoad.getCoordinates().subList(oneIntersectionPointIndex, oneRoad.getCoordinates().size()));
                        LinkedList<Coordinates> twoRoadFirstPart = new LinkedList<>();
                        oneRoadFirstPart.addAll(twoRoad.getCoordinates().subList(0, twoIntersectionPointIndex));
                        LinkedList<Coordinates> twoRoadLastPart = new LinkedList<>();
                        oneRoadLastPart.addAll(twoRoad.getCoordinates().subList(twoIntersectionPointIndex, twoRoad.getCoordinates().size()));

                        ((LineString) newOneRoad1.getGeometry()).setCoordinates(oneRoadFirstPart);
                        ((LineString) newOneRoad2.getGeometry()).setCoordinates(oneRoadLastPart);
                        ((LineString) newTwoRoad1.getGeometry()).setCoordinates(twoRoadFirstPart);
                        ((LineString) newTwoRoad2.getGeometry()).setCoordinates(twoRoadLastPart);

                        deleteFeature(mapID, one.getUuid());
                        deleteFeature(mapID, two.getUuid());

                        newOneRoad1.getProperties().put("id", newOneRoad1.getUuid());
                        newOneRoad2.getProperties().put("id", newOneRoad2.getUuid());
                        newTwoRoad1.getProperties().put("id", newTwoRoad1.getUuid());
                        newTwoRoad2.getProperties().put("id", newTwoRoad2.getUuid());

                        mapFeatureRepository.create(mapID, newOneRoad1);
                        mapFeatureRepository.create(mapID, newOneRoad2);
                        mapFeatureRepository.create(mapID, newTwoRoad1);
                        mapFeatureRepository.create(mapID, newTwoRoad2);
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
