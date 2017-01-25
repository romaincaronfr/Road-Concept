package fr.enssat.lanniontech.roadconceptandroid.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zone {

    @SerializedName("working_feature")
    @Expose
    private String workingFeature;
    @SerializedName("living_feature")
    @Expose
    private String livingFeature;
    @SerializedName("car_percentage")
    @Expose
    private Integer carPercentage;
    @SerializedName("vehicle_count")
    @Expose
    private Integer vehicleCount;
    @SerializedName("departure_living_s")
    @Expose
    private Integer departureLivingS;
    @SerializedName("departure_working_s")
    @Expose
    private Integer departureWorkingS;

    public String getWorkingFeature() {
        return workingFeature;
    }

    public void setWorkingFeature(String workingFeature) {
        this.workingFeature = workingFeature;
    }

    public String getLivingFeature() {
        return livingFeature;
    }

    public void setLivingFeature(String livingFeature) {
        this.livingFeature = livingFeature;
    }

    public Integer getCarPercentage() {
        return carPercentage;
    }

    public void setCarPercentage(Integer carPercentage) {
        this.carPercentage = carPercentage;
    }

    public Integer getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Integer vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public Integer getDepartureLivingS() {
        return departureLivingS;
    }

    public void setDepartureLivingS(Integer departureLivingS) {
        this.departureLivingS = departureLivingS;
    }

    public Integer getDepartureWorkingS() {
        return departureWorkingS;
    }

    public void setDepartureWorkingS(Integer departureWorkingS) {
        this.departureWorkingS = departureWorkingS;
    }

}
