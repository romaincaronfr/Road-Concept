package fr.enssat.lanniontech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maelig on 19/09/2016.
 */
public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    public static int foo() {
        LOG.debug("foo() returns 4");
        return 4;
    }
}
