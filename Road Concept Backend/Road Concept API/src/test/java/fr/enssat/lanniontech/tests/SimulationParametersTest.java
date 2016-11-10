package fr.enssat.lanniontech.tests;

/**
 * Created by paul on 07/11/16.
 */

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.repositories.SimulationParametersRepository;
import org.junit.Assert;
import org.junit.Test;
public class SimulationParametersTest {

    @Test
    public void testSuccessBDD() {

        try {
            SimulationParametersRepository Simulationtest = new SimulationParametersRepository();
            User user1 = new User();
            user1.setId(0);
            user1.setEmail("pldp@alive.fr");
            user1.setFirstName("Paul");
            user1.setLastName("LDP");
            user1.setPassword("lalala");
            Simulationtest.create(0,user1,"test1","12345");




        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            Assert.fail();
        }

    }


}
