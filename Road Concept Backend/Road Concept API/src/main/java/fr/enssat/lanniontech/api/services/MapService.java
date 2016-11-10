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
import fr.enssat.lanniontech.api.utilities.MathsUtils;
import fr.enssat.lanniontech.api.utilities.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        LineString createdRoad = (LineString) feature.getGeometry();

        Map<UUID, List<Tuple<Coordinates, Coordinates>>> tab = new HashMap<>();

        List<Boolean> splitNewFeatureAt = new ArrayList<>(Arrays.asList(new Boolean[createdRoad.getCoordinates().size()]));
        Collections.fill(splitNewFeatureAt, Boolean.TRUE);

        List<List<String>> potentialIntersections = (List<List<String>>) feature.getProperties().get("intersections");

        for (int i = 0; i < potentialIntersections.size(); i++) {
            if (!potentialIntersections.get(i).isEmpty()) {

                for (String uuidToCheck : potentialIntersections.get(i)) {
                    Feature featureToCheck = getFeature(mapID, UUID.fromString(uuidToCheck));
                    LineString coordinaToCheck = (LineString) featureToCheck.getGeometry();

                    for (int j = 0; j < coordinaToCheck.getCoordinates().size() - 2; j++) {
                        Coordinates firstPointRoadA = coordinaToCheck.getCoordinates().get(j); // A = to check
                        Coordinates lastPointRoadA = coordinaToCheck.getCoordinates().get(j + 1);

                        Coordinates firstPointRoadB; // B = qu'on créé
                        Coordinates lastPointRoadB;

                        if (i == 0) {
                            firstPointRoadB = createdRoad.getCoordinates().get(0);
                            lastPointRoadB = createdRoad.getCoordinates().get(1);
                        } else {
                            firstPointRoadB = createdRoad.getCoordinates().get(i - 1);
                            lastPointRoadB = createdRoad.getCoordinates().get(i);
                        }

                        //FIXME: On ne détecte que 1 point d'intersection sur 2 :(
                        Coordinates intersectionPoint = MathsUtils.intersect(firstPointRoadA, lastPointRoadA, firstPointRoadB, lastPointRoadB);
                        LOGGER.debug("@@@ Intersection detected : " + intersectionPoint);
                        if (intersectionPoint != null) {
                            splitNewFeatureAt.set(i, true);
                            if (tab.containsKey(featureToCheck.getUuid())) {
                                tab.get(featureToCheck.getUuid()).add(new Tuple<>(coordinaToCheck.getCoordinates().get(j), createdRoad.getCoordinates().get(i)));
                            } else {
                                List<Tuple<Coordinates, Coordinates>> list = new ArrayList<>();
                                list.add(new Tuple<>(coordinaToCheck.getCoordinates().get(j), createdRoad.getCoordinates().get(i)));
                                tab.put(featureToCheck.getUuid(), list);
                            }
                        }
                    }
                }
            }
        }

        for (Entry<UUID, List<Tuple<Coordinates, Coordinates>>> entry : tab.entrySet()) {
            List<Feature> myNewFeatures = new ArrayList<>();
            Feature myFirstFeature = getFeature(mapID,entry.getKey());
            myNewFeatures.add(myFirstFeature);
            for (Tuple<Coordinates, Coordinates> tuple : entry.getValue()) {
                myBoucleToBreak:
                for (int i = 0; i < myNewFeatures.size(); i++) {
                    LineString myLineString = (LineString) myNewFeatures.get(i).getGeometry();
                    for (int j = 0; j < myLineString.getCoordinates().size(); j++) {
                        if (tuple.getLeft().equals(myLineString.getCoordinates().get(j))) {
                            if (!tuple.getRight().equals(myLineString.getCoordinates().get(0)) && !tuple.getRight().equals(myLineString.getCoordinates().get(myLineString.getCoordinates().size() - 1))) {
                                List<Feature> mySplitFeatures = splitOldFeature(myNewFeatures.get(i), j, tuple.getRight());
                                myNewFeatures.addAll(mySplitFeatures);
                                myNewFeatures.remove(i);
                            }
                            break myBoucleToBreak;
                        }
                    }
                }
            }
            long deletedCount = mapFeatureRepository.delete(mapID, myFirstFeature.getUuid());
            LOGGER.debug("@@@ deleted feature count : " + deletedCount);
            for (Feature toAdd : myNewFeatures) {
                mapFeatureRepository.create(mapID, toAdd);
            }
        }
        LOGGER.debug("" + tab);

        feature.getProperties().put("id", feature.getUuid());
        mapFeatureRepository.create(mapID, feature);
        return null;
    }

    private List<Feature> splitOldFeature(Feature one, int oneIntersectionPointIndex, Coordinates NewCoordinate) {
        Feature newOneRoad1 = new Feature();
        Feature newOneRoad2 = new Feature();
        newOneRoad1.setProperties(new HashMap<>(one.getProperties()));
        newOneRoad2.setProperties(new HashMap<>(one.getProperties()));
        newOneRoad1.setGeometry(new LineString());
        newOneRoad2.setGeometry(new LineString());

        LineString oneRoad = (LineString) one.getGeometry();

        LinkedList<Coordinates> oneRoadFirstPart = new LinkedList<>();
        oneRoadFirstPart.addAll(oneRoad.getCoordinates().subList(0, oneIntersectionPointIndex + 1));
        oneRoadFirstPart.add(NewCoordinate);
        LinkedList<Coordinates> oneRoadLastPart = new LinkedList<>();
        oneRoadLastPart.add(NewCoordinate);
        oneRoadLastPart.addAll(oneRoad.getCoordinates().subList(oneIntersectionPointIndex + 1, oneRoad.getCoordinates().size()));

        ((LineString) newOneRoad1.getGeometry()).setCoordinates(oneRoadFirstPart);
        ((LineString) newOneRoad2.getGeometry()).setCoordinates(oneRoadLastPart);

        newOneRoad1.getProperties().remove("id");
        newOneRoad2.getProperties().remove("id");
        newOneRoad1.getProperties().put("id", newOneRoad1.getUuid());
        newOneRoad2.getProperties().put("id", newOneRoad2.getUuid());

        List<Feature> listToReturn = new ArrayList<>();
        listToReturn.add(newOneRoad1);
        listToReturn.add(newOneRoad2);
        return listToReturn;
    }
}

