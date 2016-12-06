package fr.enssat.lanniontech.api.utilities;

import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class IntersectionsSplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntersectionsSplitter.class);

    private IntersectionsSplitter() {
        // prevent instantiation
    }

    public static FeatureCollection process(FeatureCollection map) {
        Map<Coordinates, List<Feature>> explosionPivot = new HashMap<>();
        Map<Feature, Coordinates> loops;

        initExplosionPivot(explosionPivot, map);
        loops = cleanExplosionPivot(explosionPivot);
        while (!explosionPivot.isEmpty()) {
            explodeRoads(explosionPivot, map, loops);
        }
        for (Feature feature : loops.keySet()) {
            explodeLoop(explosionPivot, feature, map);
        }
        return map;
    }

    private static int initExplosionPivot(Map<Coordinates, List<Feature>> explosionPivot, FeatureCollection map) {
        int cycles = 0;
        for (Feature f : map.getFeatures()) {
            if (f.isRoad()) {
                LineString road = (LineString) f.getGeometry();
                for (Coordinates c : road.getCoordinates()) {
                    explosionPivot.putIfAbsent(c, new ArrayList<>());
                    explosionPivot.get(c).add(f);
                    cycles++;
                }
            }
        }
        return cycles;
    }

    private static Map<Feature, Coordinates> cleanExplosionPivot(Map<Coordinates, List<Feature>> explosionPivot) {
        Iterator<Coordinates> iterator = explosionPivot.keySet().iterator();
        Map<Feature, Coordinates> loops = new HashMap<>();
        while (iterator.hasNext()) {
            Coordinates c = iterator.next();
            if (explosionPivot.get(c).size() < 2) {
                iterator.remove();
            } else {
                Map<Feature, Boolean> loopDetector = new HashMap<>();
                boolean remove = true;
                for (Feature f : explosionPivot.get(c)) {
                    if (loopDetector.containsKey(f)) {
                        loopDetector.replace(f, true);
                        loops.putIfAbsent(f, c);
                    } else {
                        loopDetector.put(f, false);
                    }
                    remove &= ((LineString) f.getGeometry()).isFirstOrLast(c);
                }
                if (remove) {
                    iterator.remove();
                }
            }
        }
        return loops;
    }

    private static void explodeLoop(Map<Coordinates, List<Feature>> explosionPivot, Feature feature, FeatureCollection map) {
        //TODO: find how to split corectly loops
        map.getFeatures().remove(feature);
        for (Coordinates coordinates : explosionPivot.keySet()) {
            if (explosionPivot.get(coordinates).contains(feature)) {
                explosionPivot.get(coordinates).remove(feature);
            }
        }
    }

    private static void explodeRoads(Map<Coordinates, List<Feature>> explosionPivot, FeatureCollection map, Map<Feature, Coordinates> loops) {
        Map<Feature, Feature[]> tranformMap = new HashMap<>();

        Coordinates c = (Coordinates) explosionPivot.keySet().toArray()[0];
        for (Feature f : explosionPivot.get(c)) {
            if (!((LineString) f.getGeometry()).isFirstOrLast(c)) {
                tranformMap.put(f, split(f, ((LineString) f.getGeometry()).getCoordinates().indexOf(c)));
                map.getFeatures().remove(f);
                map.getFeatures().add(tranformMap.get(f)[0]);
                map.getFeatures().add(tranformMap.get(f)[1]);
            }
        }
        explosionPivot.remove(c);

        for (Coordinates C : explosionPivot.keySet()) {
            for (Feature f : tranformMap.keySet()) {
                if (explosionPivot.get(C).contains(f)) {
                    explosionPivot.get(C).remove(f);
                    if (((LineString) tranformMap.get(f)[0].getGeometry()).contains(C)) {
                        explosionPivot.get(C).add(tranformMap.get(f)[0]);
                    } else {
                        explosionPivot.get(C).add(tranformMap.get(f)[1]);
                    }
                }
            }
        }

        for (Feature feature : tranformMap.keySet()) {
            if (loops.containsKey(feature)) {
                Coordinates C = loops.get(feature);
                loops.remove(feature);

                if (((LineString) tranformMap.get(feature)[0].getGeometry()).contains(C)) {
                    loops.put(tranformMap.get(feature)[0], C);
                } else {
                    loops.put(tranformMap.get(feature)[1], C);
                }
            }
        }
    }

    private static Feature[] split(Feature featureToSplit, int index) {
        Feature newOneRoad1 = new Feature();
        Feature newOneRoad2 = new Feature();

        newOneRoad1.setProperties(new HashMap<>(featureToSplit.getProperties()));
        newOneRoad2.setProperties(new HashMap<>(featureToSplit.getProperties()));

        // OSM ID is not in the 'properties'
        newOneRoad1.setOpenStreetMapID(featureToSplit.getOpenStreetMapID()); // Strings are immutable
        newOneRoad2.setOpenStreetMapID(featureToSplit.getOpenStreetMapID());

        newOneRoad1.setGeometry(new LineString());
        newOneRoad2.setGeometry(new LineString());

        newOneRoad1.getProperties().remove("id");
        newOneRoad2.getProperties().remove("id");
        newOneRoad1.getProperties().put("id", newOneRoad1.getUuid());
        newOneRoad2.getProperties().put("id", newOneRoad2.getUuid());

        LineString lineString = (LineString) featureToSplit.getGeometry();
        LinkedList<Coordinates> oneRoadFirstPart = new LinkedList<>();
        oneRoadFirstPart.addAll(lineString.getCoordinates().subList(0, index + 1));
        LinkedList<Coordinates> oneRoadLastPart = new LinkedList<>();
        oneRoadLastPart.addAll(lineString.getCoordinates().subList(index, lineString.getCoordinates().size()));

        ((LineString) newOneRoad1.getGeometry()).setCoordinates(oneRoadFirstPart);
        ((LineString) newOneRoad2.getGeometry()).setCoordinates(oneRoadLastPart);

        return new Feature[]{newOneRoad1, newOneRoad2};
    }
}
