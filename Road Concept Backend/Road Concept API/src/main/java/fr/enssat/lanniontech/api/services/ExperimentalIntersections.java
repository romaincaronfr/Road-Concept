package fr.enssat.lanniontech.api.services;

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

public class ExperimentalIntersections {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentalIntersections.class);

    private FeatureCollection myMap;
    private Map<Coordinates, List<Feature>> explosionPivot;

    public ExperimentalIntersections(FeatureCollection map) {
        myMap = map;
        int cycles = initExplosionPivot();
        LOGGER.debug("cycle numbers = " + cycles);
        LOGGER.debug("explosionPivot size = " + explosionPivot.size());
        List<Feature> loops = cleanExplosionPivot();
        LOGGER.debug("explosionPivot size = " + explosionPivot.size());
        LOGGER.debug("explosionPivot size = " + explosionPivot.size());
        while (explosionPivot.size() > 0) {
            explodeRoads();
        }
        for (Feature f : loops) {
            explodeLoop(f);
        }
    }

    private int initExplosionPivot() {
        int cycles = 0;
        explosionPivot = new HashMap<>();
        for (Feature f : myMap.getFeatures()) {
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

    private List<Feature> cleanExplosionPivot() {
        Iterator<Coordinates> iterator = explosionPivot.keySet().iterator();
        List<Feature> loops = new ArrayList<>();
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
                        if (!loops.contains(f)) {
                            loops.add(f);
                        }
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

    private void explodeLoop(Feature f) {
        //todo find how to split corectly loops
        myMap.getFeatures().remove(f);
        for (Coordinates C : explosionPivot.keySet()) {
            if (explosionPivot.get(C).contains(f)) {
                explosionPivot.get(C).remove(f);
            }
        }
    }

    private void explodeRoads() {
        Map<Feature, Feature[]> tranformMap = new HashMap<>();

        Coordinates c = (Coordinates) explosionPivot.keySet().toArray()[0];

        for (Feature f : explosionPivot.get(c)) {
            if (!((LineString) f.getGeometry()).isFirstOrLast(c)) {
                tranformMap.put(f, split(f, ((LineString) f.getGeometry()).getCoordinates().indexOf(c)));
                myMap.getFeatures().remove(f);
                myMap.getFeatures().add(tranformMap.get(f)[0]);
                myMap.getFeatures().add(tranformMap.get(f)[1]);
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

    }

    private Feature[] split(Feature featureToSplit, int index) {
        Map<String, Object> properties = featureToSplit.getProperties();
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
        LOGGER.debug("@@@ First size = " + oneRoadFirstPart.size());
        LinkedList<Coordinates> oneRoadLastPart = new LinkedList<>();
        oneRoadLastPart.addAll(lineString.getCoordinates().subList(index, lineString.getCoordinates().size()));
        LOGGER.debug("@@@ Second size = " + oneRoadLastPart.size());

        ((LineString) newOneRoad1.getGeometry()).setCoordinates(oneRoadFirstPart);
        ((LineString) newOneRoad2.getGeometry()).setCoordinates(oneRoadLastPart);

        return new Feature[]{newOneRoad1, newOneRoad2};
    }
}
