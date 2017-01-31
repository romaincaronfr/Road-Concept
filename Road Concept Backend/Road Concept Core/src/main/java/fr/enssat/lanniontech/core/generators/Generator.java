package fr.enssat.lanniontech.core.generators;

import java.util.List;

public interface Generator {
    List<VehicleKernel> addVehicles(long timestamp);
}
