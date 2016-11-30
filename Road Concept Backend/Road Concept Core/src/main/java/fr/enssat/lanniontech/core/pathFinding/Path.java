package fr.enssat.lanniontech.core.pathFinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Path {
    private List<UUID> path;

    public Path() {
        path = new ArrayList<>();
    }

    public void addToPath(UUID dest) {
        path.add(dest);
    }

    // FIXME: ON NE FAIT PAS UN CATCH VIDE !!! En plus, y'a des exceptions qui pètent...
    public UUID getStep(int step) {
        UUID stepUuid = null;
        try {
            stepUuid = path.get(step);
        } catch (Exception e) {

        }
        return stepUuid;
    }

    public int getSize() {
        return path.size();
    }

    @Override
    public String toString() {
        String res = "";
        for (UUID id : path) {
            res = res.concat(id + ", ");
        }
        return res;
    }
}
