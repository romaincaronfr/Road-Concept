package fr.enssat.lanniontech.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.geojson.FeatureCollection;
import fr.enssat.lanniontech.api.repositories.MapRepository;

import java.io.InputStream;
import java.util.List;


public class MapService extends AbstractService {

    private MapRepository repository = new MapRepository();

    public MapInfo create(User user, String name, String imageURL, String description) {
        return repository.create(user, name, imageURL, description);
    }

    public List<MapInfo> getAllMapsInfo(User user) {
        return repository.getAll(user);
    }

    public FeatureCollection getMap(User user, int mapID) {
        //        //TODO: Retrive data from db. Now is just example data
        //        //  File json = new File("src/main/resources/map_all.json");
        //        InputStream json = getClass().getResourceAsStream("/map_all.json"); // FIXME: Retirer le fichier du dossier ressources une fois cette méthode OK
        //        Map map = null;
        //        try {
        //            String theString = IOUtils.toString(json);
        //            map = MapJSONParser.unmarshall(theString);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //        // TODO: Check mapID incorrect (400)
        //        // TODO: Check mapID pour 404
        //        // TODO: Check mapID pas consistent avec l'user logué

        FeatureCollection map = new FeatureCollection();
        try {
            InputStream source = getClass().getResourceAsStream("/geojson-small-map.json");
            map = new ObjectMapper().readValue(source, FeatureCollection.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public boolean delete(Integer mapID) {
        MapInfo map = new MapInfo();
        map.setId(mapID);
        int count = repository.delete(map);
        return count == 1; // // If false, something goes wrong (0 or more than 1 rows deleted)
    }
}
