package com.nimz.midpointfinder.service;

import com.nimz.midpointfinder.model.Coordinate;
import com.nimz.midpointfinder.model.Path;

import java.util.List;

public interface MapService {

    Path getPath(Coordinate start, Coordinate end);

    List<List<Double>> getWaypoints(Coordinate start, Coordinate end);

    Long getDurationBetweenTwoPoints(Coordinate start, Coordinate end);

    Long getDurationBetweenTwoPoints(List<Double> start, List<Double> end);

}
