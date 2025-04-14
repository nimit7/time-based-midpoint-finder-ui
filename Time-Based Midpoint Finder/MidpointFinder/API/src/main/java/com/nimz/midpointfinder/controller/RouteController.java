package com.nimz.midpointfinder.controller;

import com.nimz.midpointfinder.model.Coordinate;
import com.nimz.midpointfinder.model.Path;
import com.nimz.midpointfinder.service.MapService;
import com.nimz.midpointfinder.service.RouteService;
import jakarta.servlet.ServletOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final MapService mapService;

    public RouteController(MapService mapService) {
        this.mapService = mapService;
    }

    @CrossOrigin(origins = "http://localhost:3001")
    @PostMapping("/waypoints")
    public ResponseEntity<List<List<Double>>> getWaypointsApi(@RequestBody Coordinate[] coordinates) {
        if (coordinates.length != 2) return ResponseEntity.badRequest().build();

        List<List<Double>> waypointsList = mapService.getWaypoints(coordinates[0], coordinates[1]);

        return ResponseEntity.ok(waypointsList);
    }

    @PostMapping("/midpoint")
    public ResponseEntity<List<Double>> getMidpointApi(@RequestBody Coordinate[] coordinates) {
        System.out.println("Midpoint called");
        System.out.println("start: " + coordinates[0]);
        System.out.println("end: " + coordinates[1]);

        if (coordinates.length != 2) return ResponseEntity.badRequest().build();

        List<List<Double>> waypointsList = mapService.getWaypoints(coordinates[0], coordinates[1]);

        List<Double> midpoint = calculateMidWaypoint(waypointsList);
        Collections.reverse(midpoint);
        return ResponseEntity.ok(midpoint);
    }

    @PostMapping("/path")
    public ResponseEntity<Path> getPathApi(@RequestBody Coordinate[] coordinates) {
        if (coordinates.length != 2) return ResponseEntity.badRequest().build();

        Path path = mapService.getPath(coordinates[0], coordinates[1]);

        System.out.println("Waypoints size: " + path.getWayPoints().size());

        return ResponseEntity.ok(path);
    }
    private List<Double> calculateMidWaypoint(List<List<Double>> waypointsList) {
        if (waypointsList == null || waypointsList.isEmpty()) {
            System.out.println("No waypoints available.");
            return null;
        }

        List<Double> mp = recursive(waypointsList, 0, waypointsList.size() - 1, (waypointsList.size() - 1)/2, 0, waypointsList.size() - 1);
        return mp;
    }
    int count = 0;
    private List<Double> recursive(List<List<Double>> waypointsList, int start1, int start2, int pointInBetween, int actualStart1, int actualStart2) {
//        System.out.println("Recursive fn called: " + count++ + " times");
        // Create reversed copies of the inner lists
        List<Double> point1 = new ArrayList<>(waypointsList.get(actualStart1));
        List<Double> point2 = new ArrayList<>(waypointsList.get(actualStart2));
        List<Double> midPoint = new ArrayList<>(waypointsList.get(pointInBetween));

        // Reverse the lists such that 1st coordinate is latitude and 2nd is longitude
        Collections.reverse(point1);
        Collections.reverse(point2);
        Collections.reverse(midPoint);

        long t1 = mapService.getDurationBetweenTwoPoints(point1, midPoint);
        long t2 = mapService.getDurationBetweenTwoPoints(point2, midPoint);
        long timeGap = Math.abs(t1 - t2);
        System.out.println("|------- " + t1 + "s -------| "+ pointInBetween +" |-------- " + t2 + "s --------|" + "    Timegap: " + timeGap);
//        System.out.println("Current Mid Point: " + pointInBetween + " TimeGap:" + timeGap);

        if(timeGap <= 80) return waypointsList.get(pointInBetween);

        // New midpoint index calculation
        long longerTime = Math.max(t1, t2);
        long halfTimeGap = timeGap/2;
        double ratio = (double) halfTimeGap / longerTime;
        int newPointInBetween;
        if(t1 > t2) {
            int shift = (int) Math.round((pointInBetween - start1) * ratio);
            newPointInBetween = pointInBetween - shift;
        } else {
            int shift = (int) Math.round((start2 - pointInBetween) * ratio);
            newPointInBetween = pointInBetween + shift;
        }

        // Ensure the index stays within valid bounds
        newPointInBetween = Math.max(start1 + 1, Math.min(start2 - 1, newPointInBetween));

        AtomicInteger index = new AtomicInteger();
        waypointsList.forEach((i) -> {
            int currentIndex = index.getAndIncrement(); // Get current index and increment once

            if(currentIndex == actualStart1) System.out.print("S1");
            else if(currentIndex == actualStart2) System.out.print("S2");
            else if(currentIndex == pointInBetween) System.out.print("M");
            else System.out.print("-");
        });
        System.out.println("");

        // return recursive(waypointsList, start1, start2, newPointInBetween);
        if(t1 > t2) {
            return recursive(waypointsList, start1, pointInBetween, newPointInBetween, 0, waypointsList.size() - 1);
        } else {
            return recursive(waypointsList, pointInBetween, start2, newPointInBetween, 0, waypointsList.size() - 1);
        }
    }

    // If the arguments are List<Double>
    private Long computeTimeGap(List<Double> start1, List<Double> start2, List<Double> pointInBetween) {
        return computeTimeGap(new Coordinate(start1), new Coordinate(start2), new Coordinate(pointInBetween));
    }

    // If the arguments are Coordinates
    private Long computeTimeGap(Coordinate start1, Coordinate start2, Coordinate pointInBetween) {
        long t1 = mapService.getDurationBetweenTwoPoints(start1, pointInBetween);
        long t2 = mapService.getDurationBetweenTwoPoints(start2, pointInBetween);

//        long timeGap = Math.abs(t1 - t2);
        long timeGap = t1 - t2;
        return timeGap;
    }
}
