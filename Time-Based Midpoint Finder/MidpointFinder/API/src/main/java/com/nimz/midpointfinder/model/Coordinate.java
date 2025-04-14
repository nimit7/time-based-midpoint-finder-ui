package com.nimz.midpointfinder.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate() {}
    public Coordinate(List<Double> coordinates) {
        this.latitude = coordinates.get(0);
        this.longitude = coordinates.get(1);
    }
    // All-argument constructor
    public Coordinate(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }


}
