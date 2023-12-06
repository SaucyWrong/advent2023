package com.quinnheavyindustries.advent2023;

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
        private static final Pattern symbolDetector = Pattern.compile("([^_a-zA-Z0-9.])");
        private static final Pattern partNumberDetector = Pattern.compile("(\\d+)");

        private int currentY;
        private final Set<String> symbols = new HashSet<>();
        private final List<String> nonUniqueParts = new ArrayList<>();
        private final SortedSet<Point> symbolCoordinates = new TreeSet<>();
        private final SortedMap<Point, String> partsTable = new TreeMap<>();

        public void analyze(String filename) {
            currentY = 0;
            List<String> lines = Utils.loadLines(filename);
            lines.forEach(line -> {
                System.out.printf("%d: %s\n", currentY, line);
                updateSymbolSet(line, currentY);
                updatePartsTable(line, currentY);
                currentY++;
            });
            System.out.printf("Symbols: %s\n", symbols);
            System.out.printf("Non-unique parts: %s\n", nonUniqueParts.size());
            System.out.printf("Unique parts: %s\n", new HashSet<>(nonUniqueParts).size());

            var solution = getConnectedParts().stream()
                    .mapToInt(Integer::parseInt)
                    .sum();
            System.out.printf("Solution: %d\n", solution);
        }

        public List<String> getConnectedParts() {
            return symbolCoordinates.stream()
                    .map(this::getPartsConnectedToPoint)
                    .flatMap(Set::stream)
                    .collect(Collectors.toList());
        }

        public Set<String> getPartsConnectedToPoint(Point point) {
            return point.getNeighbors().stream()
                    .map(partsTable::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        private void updateSymbolSet(String line, int lineNumber) {
            var symbolMatcher = symbolDetector.matcher(line);
            while (symbolMatcher.find()) {
                var symbolPoint = new Point(symbolMatcher.start(), lineNumber);
                symbolCoordinates.add(symbolPoint);
                symbols.add(symbolMatcher.group(1));
                System.out.printf("Symbol %s found at %s\n", symbolMatcher.group(1), symbolPoint);
            }
        }

        private void updatePartsTable(String line, int lineNumber) {
            var partMatcher = partNumberDetector.matcher(line);
            while (partMatcher.find()) {
                var partName = partMatcher.group(1);
                nonUniqueParts.add(partName);
                var partOrigin = new Point(partMatcher.start(), lineNumber);
                var partPoints = partOrigin.getHorizontalRange(partName.length());
                System.out.printf("Part %s found at %s\n", partName, partOrigin);
                System.out.printf("Part points: %s\n", partPoints);
                partPoints.forEach(point -> partsTable.put(point, partName));
            }
        }
    }

    public record Point(int x, int y) implements Comparable<Point> {
        public List<Point> getNeighbors() {
            return List.of(
                    new Point(x - 1, y - 1),
                    new Point(x, y - 1),
                    new Point(x + 1, y - 1),
                    new Point(x - 1, y),
                    new Point(x + 1, y),
                    new Point(x - 1, y + 1),
                    new Point(x, y + 1),
                    new Point(x + 1, y + 1)
            );
        }

        public List<Point> getHorizontalRange(int length) {
            List<Point> result = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                result.add(new Point(x + i, y));
            }
            return result;
        }

        @Override
        public int compareTo(Point o) {
            return Comparator.comparingInt(Point::y)
                    .thenComparingInt(Point::x)
                    .compare(this, o);
        }
    }
}
