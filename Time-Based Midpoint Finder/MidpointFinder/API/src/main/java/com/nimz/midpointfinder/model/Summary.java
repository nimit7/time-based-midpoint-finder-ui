package com.nimz.midpointfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Summary {
    Long distance;
    Long duration;

    public Long getDistance() {
        return distance;
    }

    public Long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "distance=" + distance +
                ", duration=" + duration +
                '}';
    }
}
