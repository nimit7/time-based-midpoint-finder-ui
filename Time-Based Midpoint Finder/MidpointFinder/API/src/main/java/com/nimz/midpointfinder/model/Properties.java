package com.nimz.midpointfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Properties {
    private Summary summary;

    public Summary getSummary() {
        return summary;
    }
}
