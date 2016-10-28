package fr.enssat.lanniontech.api.entities.simulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum RoadCongestionLevel {
    LOW,
    MEDIUM,
    HIGH;

    private static final List<RoadCongestionLevel> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static RoadCongestionLevel random() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
