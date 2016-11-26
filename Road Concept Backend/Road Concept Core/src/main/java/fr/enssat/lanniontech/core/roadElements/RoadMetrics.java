package fr.enssat.lanniontech.core.roadElements;

import java.util.UUID;

public class RoadMetrics {
    private final UUID roadId;
    private final int timestamp;
    private final int congestion;

    public RoadMetrics(UUID roadId, int timestamp, int congestion) {
        this.roadId = roadId;
        this.timestamp = timestamp;
        this.congestion = congestion;
    }

    public UUID getRoadId() {
        return roadId;
    }

    public int getCongestion() {
        return congestion;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
