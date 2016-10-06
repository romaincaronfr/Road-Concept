package fr.enssat.lanniontech.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.jsonparser.FeatureCollection;
import fr.enssat.lanniontech.api.repositories.MapRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MapService extends AbstractService {

    private MapRepository repository = new MapRepository();

    public List<MapInfo> getAllMapsInfo(User user) {
        //TODO: Retrive data from db. Now is just fake data
        List<MapInfo> result = new ArrayList<>();

        MapInfo map1 = new MapInfo();
        map1.setId(1);
        map1.setName("Ville de Lannion");
        map1.setDescription("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");

        MapInfo map2 = new MapInfo();
        map2.setId(2);
        map2.setImageURL("http://www.notempire.com/images/uploads/poke-3.jpg");
        map2.setName("Pokemon GO Plan");

        MapInfo map3 = new MapInfo();
        map3.setId(76);
        map3.setImageURL("http://pcad.lib.washington.edu/media/geo-images/gmap/2632.png");
        map3.setName("JFK Plazza area");
        map3.setDescription("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        result.add(map1);
        result.add(map2);
        result.add(map3);
        return result;
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
}
