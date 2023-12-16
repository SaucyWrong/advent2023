package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.PointAndHeading;
import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.*;

import static com.quinnheavyindustries.util.Heading.*;
import static java.lang.System.*;

public class Day16 {

    public static char[][] grid;
    public static Set<PointAndHeading> beams = new HashSet<>();
    public static Set<Point> energizedPoints = new HashSet<>();

    public static void main(String[] args) {
        var startTime = nanoTime();
        var input = Utils.readInputAsString("day16-puzzle");
        grid = Utils.fillTowDimensionalCharArray(input);
        followNewBeam(new PointAndHeading(new Point(0, 0), East));
        var endTime = nanoTime();
        out.println("Solution: " + energizedPoints.size());
        out.println("Total time: " + Duration.ofNanos(endTime - startTime));
    }

    static void followNewBeam(PointAndHeading origin) {
//        out.printf("Following new beam from %s with heading %s\n", origin.point(), origin.heading());
        traceBeam(origin);
    }

    // propagate beam from origin point and heading until it reaches a splitter, leaves the grid, or rejoins itself
    static void traceBeam(PointAndHeading currentPoint) {
        beams.add(currentPoint);
        energizedPoints.add(currentPoint.point());

        var nextPoints = nextPointsAndHeading(currentPoint);

        if (nextPoints.size() == 1) {
            var nextPoint = nextPoints.getFirst();
            if (isOutOfBounds(nextPoint)) {
                // beam will leave the grid
//                out.printf("Beam left the grid at %s with heading %s\n", nextPoint.point(), nextPoint.heading());
                return;
            }
            if (beams.contains(nextPoint)) {
//                out.printf("Beam coincided with itself at %s with heading %s\n", nextPoint.point(), nextPoint.heading());
                // beam will rejoin itself (it will overlap with an existing beam traveling in the same direction)
                return;
            }
//            out.printf("Beam continued to %s with heading %s\n", nextPoint.point(), nextPoint.heading());
            traceBeam(nextPoint);
        } else if (nextPoints.size() > 1) {
            // beam splits
//            out.printf("Beam split at %s with heading %s\n", currentPoint.point(), currentPoint.heading());
            nextPoints.stream()
                    .filter(Day16::isInBounds)
                    .forEach(Day16::followNewBeam);
        }
    }

    static boolean isOutOfBounds(PointAndHeading pointAndHeading) {
        var point = pointAndHeading.point();
        return point.x() < 0 || point.x() >= grid[0].length || point.y() < 0 || point.y() >= grid.length;
    }

    static boolean isInBounds(PointAndHeading pointAndHeading) {
        return !isOutOfBounds(pointAndHeading);
    }

    static List<PointAndHeading> nextPointsAndHeading(PointAndHeading current) {
        if (isOutOfBounds(current)) { throw new ArrayIndexOutOfBoundsException("current point and heading is out of bounds"); }
        var currentPoint = current.point();
        var currentHeading = current.heading();
        char currentChar = grid[current.point().y()][current.point().x()];

        return switch (currentChar) {
            case '.' -> List.of(new PointAndHeading(currentHeading.next(currentPoint), currentHeading));
            case '/' -> switch (currentHeading) {
                case North -> List.of(new PointAndHeading(East.next(currentPoint), East));
                case South -> List.of(new PointAndHeading(West.next(currentPoint), West));
                case East -> List.of(new PointAndHeading(North.next(currentPoint), North));
                case West -> List.of(new PointAndHeading(South.next(currentPoint), South));
            };
            case '\\' -> switch (currentHeading) {
                case North -> List.of(new PointAndHeading(West.next(currentPoint), West));
                case South -> List.of(new PointAndHeading(East.next(currentPoint), East));
                case East -> List.of(new PointAndHeading(South.next(currentPoint), South));
                case West -> List.of(new PointAndHeading(North.next(currentPoint), North));
            };
            case '-' -> switch (currentHeading) {
                case East, West -> List.of(new PointAndHeading(currentHeading.next(currentPoint), currentHeading));
                default -> currentHeading.orthogonals().stream()
                        .map(heading -> new PointAndHeading(heading.next(currentPoint), heading))
                        .toList();
            };
            case '|' -> switch (currentHeading) {
                case North, South -> List.of(new PointAndHeading(currentHeading.next(currentPoint), currentHeading));
                default -> currentHeading.orthogonals().stream()
                        .map(heading -> new PointAndHeading(heading.next(currentPoint), heading))
                        .toList();
            };
            default -> throw new IllegalArgumentException("Unknown value: " + currentChar);
        };
    }

}
