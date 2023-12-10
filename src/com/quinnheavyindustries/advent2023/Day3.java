package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.Utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day3 {

    public static void main(String[] args) {
        var analyzer = new EngineSchematicAnalyzer();
        analyzer.analyze("day3-puzzle-input");
    }

    public static class EngineSchematicAnalyzer {
        private static final Pattern gearMatcher = Pattern.compile("(\\*)");
        private static final Pattern partNumberDetector = Pattern.compile("(\\d+)");

        private int currentY;
        private final SortedSet<Point> potentialGears = new TreeSet<>();
        private final SortedMap<Point, String> partsTable = new TreeMap<>();

        public void analyze(String filename) {
            currentY = 0;
            List<String> lines = Utils.loadLines(filename);
            lines.forEach(line -> {
                System.out.printf("%d: %s\n", currentY, line);
                updatePotentialGears(line, currentY);
                updatePartsTable(line, currentY);
                currentY++;
            });

            var solution = potentialGears.stream()
                    .map(this::calculateGearRatio)
                    .filter(Optional::isPresent)
                    .mapToInt(Optional::get)
                    .sum();
            System.out.printf("Solution: %d\n", solution);
        }

        public Optional<Integer> calculateGearRatio(Point point) {
            var connectedParts = getPartsConnectedToPoint(point).toArray(new String[0]);
            if (connectedParts.length != 2) {
                return Optional.empty();
            }
            var part1 = Integer.parseInt(connectedParts[0]);
            var part2 = Integer.parseInt(connectedParts[1]);
            System.out.printf("Found gear at %s with ratio %d * %d = %d\n", point, part1, part2, part1 * part2);
            return Optional.of(part1 * part2);
        }

        public Set<String> getPartsConnectedToPoint(Point point) {
            return point.getAllNeighbors().stream()
                    .map(partsTable::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        private void updatePotentialGears(String line, int lineNumber) {
            var matcher = gearMatcher.matcher(line);
            while (matcher.find()) {
                var symbolPoint = new Point(matcher.start(), lineNumber);
                potentialGears.add(symbolPoint);
                System.out.printf("Potential Gear found at %s\n", symbolPoint);
            }
        }

        private void updatePartsTable(String line, int lineNumber) {
            var partMatcher = partNumberDetector.matcher(line);
            while (partMatcher.find()) {
                var partName = partMatcher.group(1);
                var partOrigin = new Point(partMatcher.start(), lineNumber);
                var partPoints = partOrigin.getHorizontalRange(partName.length());
                System.out.printf("Part %s found at %s\n", partName, partOrigin);
                System.out.printf("Part points: %s\n", partPoints);
                partPoints.forEach(point -> partsTable.put(point, partName));
            }
        }
    }

}
