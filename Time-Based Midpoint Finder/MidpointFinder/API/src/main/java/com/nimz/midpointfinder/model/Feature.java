package com.nimz.midpointfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Feature {
    private Geometry geometry;

    private Properties properties;

    public Geometry getGeometry() {
        return geometry;
    }

    public Properties getProperties() { return properties; }
}
