package fr.enssat.lanniontech.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.EntityNotExistingException;
import fr.enssat.lanniontech.api.geojson.Feature;
import fr.enssat.lanniontech.api.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.geojson.FeatureType;
import fr.enssat.lanniontech.api.geojson.LineString;
import fr.enssat.lanniontech.api.geojson.Point;
import fr.enssat.lanniontech.api.repositories.MapInfoRepository;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapService extends AbstractService {

    private static final String[] OSM_HIGHWAY_TO_CONSERVE = {"motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential", "motorway_link", "trunk_link", "primary_link", "secondary_link", "tertiary_link", "road", "traffic_signals"};
    private MapInfoRepository repository = new MapInfoRepository();

    public MapInfo create(User user, String name, boolean fromOSM, String imageURL, String description) {
        return repository.create(user, name, fromOSM, imageURL, description);
    }

    public List<MapInfo> getAllMapsInfo(User user) {
        return repository.getAll(user);
    }

    public fr.enssat.lanniontech.api.entities.Map getMap(User user, int mapID) {
        MapInfo infos = repository.get(mapID);
        if (infos == null) {
            throw new EntityNotExistingException(MapInfo.class);
        }

        FeatureCollection features = new FeatureCollection();
        try {
            //    InputStream source = getClass().getResourceAsStream("/from-osm-lannion-center.json");
            InputStream source = getClass().getResourceAsStream("/marseille-saint-antoine.json");
            //  InputStream source = getClass().getResourceAsStream("/antoine-example.json");

            features = new ObjectMapper().readValue(source, FeatureCollection.class);
            infos.setFromOSM(true);
            if (infos.isFromOSM()) {
                fromOSMAdaptation(features);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fr.enssat.lanniontech.api.entities.Map map = new fr.enssat.lanniontech.api.entities.Map();
        map.setInfos(infos);
        map.setFeatures(features);
        return map;
    }

    // =======================
    // OPEN STREET MAP IMPORTS
    // =======================

    //FIXME: refactor
    private void fromOSMAdaptation(FeatureCollection map) {
        for (Iterator<Feature> iterator = map.getFeatures().iterator(); iterator.hasNext(); ) {
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
                //FIXME: get le highway qui ne serai pas dans les tags
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
        newProperties.put("id", new Random().nextInt()); // TODO: A récupérer après l'ajout en base
        newProperties.put("oneway", getOneWay(feature.getProperties()));
        newProperties.put("bridge", getBridge(feature.getProperties()));
        newProperties.put("maxspeed", getMaxSpeed(feature.getProperties()));
        Integer time = getRedLightTime(feature.getProperties());
        if (time != null) {
            newProperties.put("redlighttime", getRedLightTime(feature.getProperties()));
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

    private boolean getOneWay(Map<String, Object> properties) {
        if (properties.containsKey("oneway")) {
            String oneway = (String) properties.get("oneway");
            if (oneway.equals("yes")) {
                return true;
            }
        }
        Map tags = (LinkedHashMap) properties.get("tags");
        if (tags.containsKey("oneway")) {
            String oneway = (String) tags.get("oneway");
            if (oneway.equals("yes")) {
                return true;
            }
        }

        return false;
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
            if (properties.containsKey("redlighttime")) {
                return Integer.valueOf((String) properties.get("redlighttime"));
            }
            return 30; // Default value
        }
        return null;
    }

    public boolean delete(Integer mapID) {
        MapInfo map = new MapInfo();
        map.setId(mapID);
        int count = repository.delete(map);
        return count == 1; // // If false, something goes wrong (0 or more than 1 rows deleted)
    }
}
