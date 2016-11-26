package fr.enssat.lanniontech.core.roadElements;

import java.util.UUID;

public class RoadMetrics {
    private UUID roadId;
    private int congestion;
    private long timestamp;

    public RoadMetrics(UUID roadId,int congestion, long timestamp) {
        this.roadId = roadId;
        this.congestion = congestion;
        this.timestamp = timestamp;
    }

    public UUID getRoadId() {
        return roadId;
    }

    public int getCongestion() {
        return congestion;
    }

    public void setRoadId(UUID roadId) {
        this.roadId = roadId;
    }

    public void setCongestion(int congestion) {
        this.congestion = congestion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
