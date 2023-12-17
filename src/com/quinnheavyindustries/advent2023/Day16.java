package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Direction;
import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.PointAndDirection;
import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.*;

import static com.quinnheavyindustries.util.Direction.*;
import static java.lang.System.*;

public class Day16 {

    public static char[][] grid;
    public static Set<PointAndDirection> beams = new HashSet<>();
    public static Set<Point> energizedPoints = new HashSet<>();
    public static Set<Integer> beamEnergies = new HashSet<>();

    public static void main(String[] args) {
        var startTime = nanoTime();
        var input = Utils.readInputAsString("day16-puzzle");
        grid = Utils.fillTowDimensionalCharArray(input);

        getAllEdgePoints().stream()
                .flatMap(point -> startingPoints(point.x(), point.y()).stream())
                .forEach(startingPoint -> {
                    followNewBeam(startingPoint);
                    beamEnergies.add(energizedPoints.size());
                    beams.clear();
                    energizedPoints.clear();
                });

        var endTime = nanoTime();
        out.println("Solution: " + beamEnergies.stream().max(Integer::compareTo).orElseThrow());
        out.println("Total time: " + Duration.ofNanos(endTime - startTime));
    }

    static void followNewBeam(PointAndDirection origin) {
        traceBeam(origin);
    }

    // propagate beam from origin point and heading until it reaches a splitter, leaves the grid, or rejoins itself
    static void traceBeam(PointAndDirection currentPoint) {
        beams.add(currentPoint);
        energizedPoints.add(currentPoint.point());

        var nextPoints = nextPointsAndHeading(currentPoint);

        if (nextPoints.size() == 1) {
            var nextPoint = nextPoints.getFirst();
            if (isOutOfBounds(nextPoint)) {
                // beam will leave the grid
                return;
            }
            if (beams.contains(nextPoint)) {
                // beam will rejoin itself (it will overlap with an existing beam traveling in the same direction)
                return;
            }
            traceBeam(nextPoint);
        } else if (nextPoints.size() > 1) {
            // beam splits
            nextPoints.stream()
                    .filter(Day16::isInBounds)
                    .forEach(Day16::followNewBeam);
        }
    }

    static boolean isOutOfBounds(PointAndDirection pointAndDirection) {
        var point = pointAndDirection.point();
        return point.x() < 0 || point.x() >= grid[0].length || point.y() < 0 || point.y() >= grid.length;
    }

    static boolean isInBounds(PointAndDirection pointAndDirection) {
        return !isOutOfBounds(pointAndDirection);
    }

    static List<Point> getAllEdgePoints() {
        var result = new ArrayList<Point>();
        for (var y = 0; y < grid.length; y++) {
            if (y == 0 || y == grid.length - 1) {
                for (var x = 0; x < grid[0].length; x++) {
                    result.add(new Point(x, y));
                }
            } else {
                result.add(new Point(0, y));
                result.add(new Point(grid[0].length - 1, y));
            }
        }
        return result;
    }

    static List<PointAndDirection> startingPoints(int x, int y) {
        // corner cases
        if (x == 0 && y == 0) {
            return List.of(
                    new PointAndDirection(new Point(0, 0), East),
                    new PointAndDirection(new Point(0, 0), South)
            );
        }
        if (x == 0 && y == grid.length - 1) {
            return List.of(
                    new PointAndDirection(new Point(0, grid.length - 1), East),
                    new PointAndDirection(new Point(0, grid.length - 1), North)
            );
        }
        if (x == grid[0].length - 1 && y == 0) {
            return List.of(
                    new PointAndDirection(new Point(grid[0].length - 1, 0), West),
                    new PointAndDirection(new Point(grid[0].length - 1, 0), South)
            );
        }
        if (x == grid[0].length - 1 && y == grid.length - 1) {
            return List.of(
                    new PointAndDirection(new Point(grid[0].length - 1, grid.length - 1), West),
                    new PointAndDirection(new Point(grid[0].length - 1, grid.length - 1), North)
            );
        }

        // edge cases
        Direction direction;
        if (x == 0) {
            direction = East;
        } else if (x == grid[0].length - 1) {
            direction = West;
        } else if (y == 0) {
            direction = South;
        } else if (y == grid.length - 1) {
            direction = North;
        } else {
            throw new IllegalArgumentException("Point is not on the edge of the grid");
        }
        return List.of(new PointAndDirection(new Point(x, y), direction));
    }

    static List<PointAndDirection> nextPointsAndHeading(PointAndDirection current) {
        if (isOutOfBounds(current)) { throw new ArrayIndexOutOfBoundsException("positionAndDirection point and heading is out of bounds"); }
        var currentPoint = current.point();
        var currentHeading = current.direction();
        char currentChar = grid[current.point().y()][current.point().x()];

        return switch (currentChar) {
            case '.' -> List.of(new PointAndDirection(currentHeading.next(currentPoint), currentHeading));
            case '/' -> switch (currentHeading) {
                case North -> List.of(new PointAndDirection(East.next(currentPoint), East));
                case South -> List.of(new PointAndDirection(West.next(currentPoint), West));
                case East -> List.of(new PointAndDirection(North.next(currentPoint), North));
                case West -> List.of(new PointAndDirection(South.next(currentPoint), South));
                case None -> throw new IllegalArgumentException("None is not a valid heading");
            };
            case '\\' -> switch (currentHeading) {
                case North -> List.of(new PointAndDirection(West.next(currentPoint), West));
                case South -> List.of(new PointAndDirection(East.next(currentPoint), East));
                case East -> List.of(new PointAndDirection(South.next(currentPoint), South));
                case West -> List.of(new PointAndDirection(North.next(currentPoint), North));
                case None -> throw new IllegalArgumentException("None is not a valid heading");
            };
            case '-' -> switch (currentHeading) {
                case East, West -> List.of(new PointAndDirection(currentHeading.next(currentPoint), currentHeading));
                case None -> throw new IllegalArgumentException("None is not a valid heading");
                default -> currentHeading.orthogonals().stream()
                        .map(direction -> new PointAndDirection(direction.next(currentPoint), direction))
                        .toList();
            };
            case '|' -> switch (currentHeading) {
                case North, South -> List.of(new PointAndDirection(currentHeading.next(currentPoint), currentHeading));
                case None -> throw new IllegalArgumentException("None is not a valid heading");
                default -> currentHeading.orthogonals().stream()
                        .map(direction -> new PointAndDirection(direction.next(currentPoint), direction))
                        .toList();
            };
            default -> throw new IllegalArgumentException("Unknown value: " + currentChar);
        };
    }

}
