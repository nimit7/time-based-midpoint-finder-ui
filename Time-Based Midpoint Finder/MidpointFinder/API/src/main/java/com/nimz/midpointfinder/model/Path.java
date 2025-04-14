package com.nimz.midpointfinder.model;

import java.util.List;

public class Path {
    List<List<Double>> wayPoints;

    Long distance;

    Long duration;

    public Path(Long distance, Long duration, List<List<Double>> wayPoints) {
        this.distance = distance;
        this.duration = duration;
        this.wayPoints = wayPoints;
    }

    public Long getDistance() {  return distance;  }

    public Long getDuration() {  return duration;  }

    public List<List<Double>> getWayPoints() {
        return wayPoints;
    }
}
