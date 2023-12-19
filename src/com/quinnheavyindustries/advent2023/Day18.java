package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Direction;
import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.LinkedList;
import java.util.regex.Pattern;

import static java.lang.System.nanoTime;
import static java.lang.System.out;

public class Day18 {

    public static Pattern diggerPattern = Pattern.compile("^(?<direction>[DULR]) (?<length>\\d+) \\(#(?<hexColor>\\w{6})\\)$");

    public static void main(String[] args) {
        var startTime = nanoTime();
        var input = Utils.readInputAsString("day18-puzzle");
        var solution = calculateTrenchSize(input);
        var endTime = nanoTime();
        out.println("Solution: " + solution);
        out.println("Took: " + Duration.ofNanos(endTime - startTime));
    }

    static long calculateTrenchSize(String input) {
        var position = new Point(0 , 0);
        LinkedList<Point> vertices = new LinkedList<>();
        long boundaryPoints = 0;
        var instructions = input.split("\\n");

        for (var instruction: instructions) {
            var matcher = diggerPattern.matcher(instruction);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid instruction: " + instruction);
            }
            var hexValue = matcher.group("hexColor");
            var length = Integer.parseInt(hexValue.substring(0, 5), 16);
            var directionCode = Integer.parseInt(hexValue.substring(5), 16);
            var direction = fromInteger(directionCode);
            var newVertex = direction.next(position, length);
            vertices.add(newVertex);
            position = newVertex;
            boundaryPoints += length;
        }

        var shoelaceArea = shoelaceArea(vertices);
        var i = shoelaceArea - (boundaryPoints / 2) + 1;
        return i + boundaryPoints;
    }

    static long shoelaceArea(LinkedList<Point> vertices) {
        var firstVertex = vertices.getFirst();
        var lastVertex = vertices.getLast();
        var area = 0L;
        for (var i = 0; i < vertices.size() - 1; i++) {
            var currentVertex = vertices.get(i);
            var nextVertex = vertices.get(i + 1);
            area += ((long) currentVertex.x() * nextVertex.y()) - ((long) currentVertex.y() * nextVertex.x());
        }
        area += ((long) lastVertex.x() * firstVertex.y()) - ((long) lastVertex.y() * firstVertex.x());
        return Math.abs(area) / 2;
    }

    static Direction fromInteger(int i) {
        return switch (i) {
            case 3 -> Direction.North;
            case 1 -> Direction.South;
            case 2 -> Direction.West;
            case 0 -> Direction.East;
            default -> throw new IllegalArgumentException("Invalid code: " + i);
        };
    }

}
