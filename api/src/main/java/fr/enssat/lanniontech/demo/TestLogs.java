package fr.enssat.lanniontech.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maelig on 20/09/2016.
 */
public class TestLogs {

    private static Logger LOG = LoggerFactory.getLogger(TestLogs.class);

    public static void main (String[] args) {
        LOG.debug("Hello world.");
    }
}
