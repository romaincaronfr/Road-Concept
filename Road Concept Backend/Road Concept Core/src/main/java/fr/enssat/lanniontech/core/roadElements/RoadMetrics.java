package fr.enssat.lanniontech.core.roadElements;

import java.util.UUID;

public class RoadMetrics {
    private UUID roadId;
    private int congestion;

    public RoadMetrics(UUID roadId,int congestion) {
        this.roadId = roadId;
        this.congestion = congestion;
    }

    public UUID getRoadId() {
        return roadId;
    }

    public int getCongestion() {
        return congestion;
    }
}
