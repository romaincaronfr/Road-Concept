package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.geojson.Coordinates;
import fr.enssat.lanniontech.api.entities.geojson.Feature;
import fr.enssat.lanniontech.api.entities.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.entities.geojson.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExperimentalIntersections {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentalIntersections.class);

    private FeatureCollection myMap;
    private Map<Coordinates, List<Feature>> explosionPivot;

    public ExperimentalIntersections(FeatureCollection map) {
        myMap = map;
        initExplosionPivot();
        LOGGER.debug("explosionPivot size = " + explosionPivot);
        cleanExplosionPivot();
        LOGGER.debug("explosionPivot size = " + explosionPivot);
    }

    private void initExplosionPivot() {
        explosionPivot = new HashMap<>();
        for (Feature f :myMap.getFeatures()){
            if(f.isRoad()){
                LineString road = (LineString)f.getGeometry();
                for (Coordinates c :road.getCoordinates()){
                    explosionPivot.putIfAbsent(c,new ArrayList<>());
                    explosionPivot.get(c).add(f);
                }
            }
        }
    }

    private void cleanExplosionPivot(){
        Iterator<Coordinates> iterator = explosionPivot.keySet().iterator();
        while (iterator.hasNext()){
            Coordinates c = iterator.next();
            if(explosionPivot.get(c).size()<2){
                explosionPivot.remove(c);
                iterator.remove();
            }else{
                boolean remove = true;
                for (Feature f : explosionPivot.get(c)) {
                    remove &= ((LineString) f.getGeometry()).isFirstOrLast(c);
                }
                if (remove) {
                    explosionPivot.remove(c);
                    iterator.remove();
                }
            }
        }
    }
}
