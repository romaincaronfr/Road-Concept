package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.FeatureType;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.repositories.MapFeatureRepository;
import fr.enssat.lanniontech.api.repositories.MapInfoRepository;
import fr.enssat.lanniontech.api.utilities.GlobalUtils;
import fr.enssat.lanniontech.api.utilities.IntersectionSplitter;
import fr.enssat.lanniontech.api.utilities.JSONUtils;
import fr.enssat.lanniontech.api.utilities.MathsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class MapService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);

    private static final String[] OSM_HIGHWAY_TO_CONSERVE = {"motorway", "trunk", "primary", "secondary", "tertiary", "residential", "motorway_link", "trunk_link", "primary_link", "secondary_link", "tertiary_link", "road" /*, "traffic_signals"*/};

    private MapInfoRepository mapInfoRepository = new MapInfoRepository();
    private MapFeatureRepository mapFeatureRepository = new MapFeatureRepository();

    public MapInfo create(User user, String name, String imageURL, String description) {
        return mapInfoRepository.create(user, name, imageURL, description);
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
        return getMap(user.getId(), mapID);
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

    public int importFromOSM(User user, int mapID, String fileData) {
        MapInfo infos = mapInfoRepository.get(mapID);
        if (infos == null) {
            throw new EntityNotExistingException(MapInfo.class);
        }

        FeatureCollection importedFeatures = JSONUtils.fromJSON(fileData, FeatureCollection.class);
        fromOSMAdaptation(importedFeatures);
        importedFeatures = IntersectionSplitter.process(importedFeatures);

        FeatureCollection toAdd = new FeatureCollection();
        FeatureCollection existingFeatures = mapFeatureRepository.getAll(mapID);

        for (Feature feature : importedFeatures) {
            boolean add = true;
            int i = 0;
            while (i < existingFeatures.getFeatures().size() && add) {
                if (feature.getOpenStreetMapID().equals(existingFeatures.getFeatures().get(i).getOpenStreetMapID())) {
                    LOGGER.debug("Duplicated feature detected for OSM id = " + feature.getOpenStreetMapID());
                    add = false;
                }
                i++;
            }
            if (add) {
                toAdd.getFeatures().add(feature);
            }
        }

        if (!toAdd.getFeatures().isEmpty()) {
            mapFeatureRepository.createAll(mapID, toAdd);
        }

        LOGGER.info("Duplicated features : " + (importedFeatures.getFeatures().size() - toAdd.getFeatures().size()));
        return toAdd.getFeatures().size();
    }

    private void fromOSMAdaptation(FeatureCollection features) {
        for (Iterator<Feature> iterator = features.getFeatures().iterator(); iterator.hasNext(); ) {
            Feature feature = iterator.next();

            // Delete all non 'LineString' features
            if (!(feature.getGeometry() instanceof LineString)) {
                //                if (feature.getGeometry() instanceof Point) {
                //                    Map tags = (LinkedHashMap) feature.getProperties().get("tags");
                //                    String highway = (String) tags.get("highway");
                //                    if (!"traffic_signals".equals(highway)) {
                //                        iterator.remove();
                //                    } else {
                //                        computeProperties(feature);
                //                    }
                //                } else {
                iterator.remove();
                //  }
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
        //   newProperties.put("redlighttime", getRedLightTime(feature.getProperties()));

        feature.getProperties().clear();
        feature.getProperties().putAll(newProperties);
    }

    private FeatureType getType(Map<String, Object> properties) {
        Map tags = (LinkedHashMap) properties.get("tags");
        if (tags.containsKey("junction")) {
            String junction = (String) tags.get("junction");
            if ("roundabout".equals(junction)) {
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
                return FeatureType.SINGLE_ROAD;
            }
        }

        String highway = (String) tags.get("highway");
        switch (highway) {
            case "motorway":
                return FeatureType.DOUBLE_ROAD;
            case "trunk":
                return FeatureType.DOUBLE_ROAD;
            case "primary":
                return FeatureType.SINGLE_ROAD;
            case "secondary":
                return FeatureType.SINGLE_ROAD;
            case "tertiary":
                return FeatureType.SINGLE_ROAD;
            case "residential":
                return FeatureType.SINGLE_ROAD;
            case "motorway_link":
                return FeatureType.TRIPLE_ROAD;
            case "trunk_link":
                return FeatureType.DOUBLE_ROAD;
            case "primary_link":
                return FeatureType.SINGLE_ROAD;
            case "secondary_link":
                return FeatureType.SINGLE_ROAD;
            case "tertiary_link":
                return FeatureType.SINGLE_ROAD;
            case "road":
                return FeatureType.SINGLE_ROAD;
            //case "traffic_signals":
            //    return FeatureType.RED_LIGHT;
            default:
                return FeatureType.SINGLE_ROAD;
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
            if ("yes".equals(oneway)) {
                return true;
            }
        }
        if (properties.containsKey("tags")) {
            Map tags = (LinkedHashMap) properties.get("tags");
            if (tags.containsKey("bridge")) {
                String oneway = (String) tags.get("bridge");
                if ("yes".equals(oneway)) {
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
                case SINGLE_ROAD:
                    return 50;
                case DOUBLE_ROAD:
                    return 90;
                case TRIPLE_ROAD:
                    return 130;
                case ROUNDABOUT:
                    return 40;
                // case RED_LIGHT:
                //     return 0;
                default:
                    break;
            }
        }
        return 50; // Default value
    }

    //    private Integer getRedLightTime(Map<String, Object> properties) {
    //        if (getType(properties) == FeatureType.RED_LIGHT) {
    //            return 30; // Default value
    //        }
    //        return null;
    //    }

    public void deleteFeature(int mapID, UUID featureUUID) {
        mapFeatureRepository.delete(mapID, featureUUID);
    }

    public void addFeature(int mapID, Feature feature) {
        feature.getProperties().put("id", feature.getUuid().toString());

        LineString createdRoad = (LineString) feature.getGeometry();

        Map<UUID, Feature> tab = new HashMap<>();
        Map<UUID, List<Boolean>> tabBool = new HashMap<>();
        
        List<List<String>> potentialIntersections = (List<List<String>>) feature.getProperties().get("intersections");

        //regarde le tableau des intersection potentielles
        for (int i = 0; i < potentialIntersections.size(); i++) {
            if (!potentialIntersections.get(i).isEmpty()) {

                //verifie les potentiele routes intersectée pour le point i
                for (String uuidToCheck : potentialIntersections.get(i)) {
                    UUID uuid = UUID.fromString(uuidToCheck);

                    //clone la route potentielement coupée
                    if (!tab.containsKey(uuid)) {
                        Feature featureToCheck = getFeature(mapID, uuid);
                        Feature duplicatedFeatureToCheck = GlobalUtils.CLONER.deepClone(featureToCheck);
                        tab.put(uuid, duplicatedFeatureToCheck);
                        LineString lineString = (LineString) duplicatedFeatureToCheck.getGeometry();
                        List<Boolean> newList = new ArrayList<>(Arrays.asList(new Boolean[lineString.getCoordinates().size()]));
                        Collections.fill(newList, Boolean.FALSE);
                        tabBool.put(uuid, newList);
                    }

                    Feature duplicatedFeatureToCheck = tab.get(uuid);

                    //recup de coordonées
                    LineString coordinaToCheck = (LineString) duplicatedFeatureToCheck.getGeometry();

                    for (int j = 0; j < coordinaToCheck.getCoordinates().size() - 1; j++) {
                        Coordinates firstPointRoadA = coordinaToCheck.getCoordinates().get(j); // A = to check
                        Coordinates lastPointRoadA = coordinaToCheck.getCoordinates().get(j + 1);

                        Coordinates firstPointRoadB;
                        firstPointRoadB = new Coordinates(createdRoad.getCoordinates().get(i).getLongitude(), createdRoad.getCoordinates().get(i).getLatitude());
                        boolean intersectionPoint = MathsUtils.intersect(firstPointRoadA, lastPointRoadA, firstPointRoadB);
                        if (intersectionPoint) {
                            coordinaToCheck.getCoordinates().add(j + 1, createdRoad.getCoordinates().get(i));
                            tabBool.get(uuid).add(j + 1, true);
                            break;
                        }
                    }
                }
            }
        }

        if (!tab.isEmpty()) {
            FeatureCollection featureCollection = new FeatureCollection();
            featureCollection.getFeatures().add(feature);
            for (Entry<UUID, Feature> entry : tab.entrySet()) {
                featureCollection.getFeatures().add(entry.getValue());
                mapFeatureRepository.delete(mapID, entry.getKey());
            }
            featureCollection = IntersectionSplitter.process(featureCollection);
            mapFeatureRepository.createAll(mapID, featureCollection);
        } else {
            mapFeatureRepository.create(mapID, feature);
        }
    }
}
