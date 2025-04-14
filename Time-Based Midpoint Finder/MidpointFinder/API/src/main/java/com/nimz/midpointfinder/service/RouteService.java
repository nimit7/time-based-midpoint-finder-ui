package com.nimz.midpointfinder.service;

import com.nimz.midpointfinder.exception.RoutingException;
import com.nimz.midpointfinder.model.*;
import com.nimz.midpointfinder.config.AppConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class RouteService implements MapService {
    private final AppConfig appConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public RouteService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public RouteResponse getRouteResponse(Coordinate start, Coordinate end) {
//        System.out.println("getRouteResponse : " + start + " " + end);
        String orsUrl = String.format(
                "http://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%f,%f&end=%f,%f&simplify_tolerance=100",
                appConfig.getOrsApiKey(),
                start.getLongitude(), start.getLatitude(), // Longitude first, then latitude
                end.getLongitude(), end.getLatitude()
        );

        try {
            ResponseEntity<RouteResponse> response = restTemplate.getForEntity(orsUrl, RouteResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("Routing Failed: " + e.getResponseBodyAsString());
            throw new RoutingException("Unable to generate route. Please ensure coordinates are near roads");
        }
    }

    @Override
    public Path getPath(Coordinate start, Coordinate end) {
        Feature routeResponseFeatures = getRouteResponse(start, end)
                .getFeatures()
                .get(0);

        List<List<Double>> wayPoints = routeResponseFeatures.getGeometry().getCoordinates();

        Summary summary = routeResponseFeatures.getProperties().getSummary();

        Path path = new Path(summary.getDistance(), summary.getDuration(), wayPoints);

        return path;
    }

    @Override
    public List<List<Double>> getWaypoints(Coordinate start, Coordinate end) {
        return getRouteResponse(start, end)
                .getFeatures()
                .get(0)
                .getGeometry()
                .getCoordinates();
    }

    @Override
    public Long getDurationBetweenTwoPoints(List<Double> start, List<Double> end) {
        return getDurationBetweenTwoPoints(new Coordinate(start), new Coordinate(end));
    }
    @Override
    public Long getDurationBetweenTwoPoints(Coordinate start, Coordinate end) {
        // If start and end coordinates are the same, return 0
        if (start.equals(end)) {
            System.out.println("Equal");
            return 0L;
        }

        // Fetch the duration from API response
        Long duration = getRouteResponse(start, end)
                .getFeatures()
                .get(0)
                .getProperties()
                .getSummary()
                .getDuration();

        // Return 0 if duration is null
        return Objects.requireNonNullElse(duration, 0L);
    }

}
