package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.Utils;

import java.util.*;

import static java.lang.System.out;

public class Day11 {

    static String SPACE_CHAR = "O";
    static int EXPANSION_DISTANCE = 1000000;

    static LinkedList<Character> vastAmountsOfSpace(int length) {
        return SPACE_CHAR.repeat(length).chars().mapToObj(c -> (char) c)
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    public static void main(String[] args) {
        var galaxyChart = new GalaxyChart(Utils.loadLines("day11-puzzle"));
        out.printf("Solution: %d\n", galaxyChart.sumDistanceBetweenUniqueGalaxyPairs());
    }

    static class GalaxyChart {
        LinkedList<LinkedList<Character>> chart;
        List<Point> galaxyCoordinates;
        Set<GalaxyPair> galaxyPairs;
        SortedSet<Integer> expansionColumns = new TreeSet<>();
        SortedSet<Integer> expansionRows = new TreeSet<>();

        GalaxyChart(List<String> input) {
            chart = input.stream()
                    .map(line -> new LinkedList<>(line.chars().mapToObj(c -> (char) c).toList()))
                    .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
            out.printf("Loaded unexpanded galaxy chart with %d rows and %d columns\n",
                    chart.size(), chart.getFirst().size());
            out.println("Unexpended galaxy chart:\n" + charsToString(chart));

            expandGalaxyChart();
            out.println("Expanded galaxy chart:\n" + charsToString(chart));

            galaxyCoordinates = locateGalaxies();
            galaxyPairs = findGalaxyPairs();
            out.printf("Found %d galaxies and %d unique galaxy pairs\n", galaxyCoordinates.size(), galaxyPairs.size());
        }

        private void expandGalaxyChart() {
            for (var x = 0; x < chart.getFirst().size(); x++) {
                int finalX = x;
                if (chart.stream().map(row -> row.get(finalX)).allMatch(c -> c == '.')) {
                    expansionColumns.add(finalX);
                }
            }
            for (var y = 0; y < chart.size(); y++) {
                if (chart.get(y).stream().allMatch(c -> c == '.')) {
                    expansionRows.add(y);
                }
            }

            out.printf("Expansion required in columns %s and rows %s\n", expansionRows, expansionColumns);
            var rowLength = chart.getFirst().size();
            expansionRows.reversed().forEach(row -> {
                chart.add(row, vastAmountsOfSpace(rowLength));
                chart.remove(row + 1);
            });
            expansionColumns.reversed().forEach(column -> {
                chart.forEach(row -> row.add(column, SPACE_CHAR.charAt(0)));
                chart.forEach(row -> row.remove(column + 1));
            });
        }

        private List<Point> locateGalaxies() {
            var galaxies = new ArrayList<Point>();
            for (var y = 0; y < chart.size(); y++) {
                for (var x = 0; x < chart.get(y).size(); x++) {
                    if (chart.get(y).get(x) == '#') {
                        out.printf("Found galaxy at %d, %d\n", x, y);
                        galaxies.add(new Point(x, y));
                    }
                }
            }
            return galaxies;
        }

        private Set<GalaxyPair> findGalaxyPairs() {
            var galaxyPairs = new HashSet<GalaxyPair>();
            for (var i = 0; i < galaxyCoordinates.size(); i++) {
                for (var j = i + 1; j < galaxyCoordinates.size(); j++) {
                    galaxyPairs.add(new GalaxyPair(galaxyCoordinates.get(i), galaxyCoordinates.get(j)));
                }
            }
            return galaxyPairs;
        }

        public long sumDistanceBetweenUniqueGalaxyPairs() {
            return galaxyPairs.stream().mapToLong(GalaxyPair::distance).sum();
        }

        class GalaxyPair {
            TreeSet<Point> coordinates = new TreeSet<>();

            GalaxyPair(Point galaxyA, Point galaxyB) {
                coordinates.add(galaxyA);
                coordinates.add(galaxyB);
            }

            public long distance() {
                if (coordinates.isEmpty() || coordinates.size() == 1) {
                    return 0;
                }
                var minY = Math.min(coordinates.first().y(), coordinates.last().y());
                var maxY = Math.max(coordinates.first().y(), coordinates.last().y());
                var minX = Math.min(coordinates.first().x(), coordinates.last().x());
                var maxX = Math.max(coordinates.first().x(), coordinates.last().x());

                var expansionColumnsTraversed = expansionColumns.stream()
                        .filter(x -> x > minX && x < maxX)
                        .toList().size();
                var expansionRowsTraversed = expansionRows.stream()
                        .filter(y -> y > minY && y < maxY)
                        .toList().size();

                var rawDistance = coordinates.first().gridDistanceTo(coordinates.last());
                var totalExpansions = expansionColumnsTraversed + expansionRowsTraversed;
                var expandedDistance = (EXPANSION_DISTANCE * totalExpansions) - totalExpansions;
                var totalDistance = rawDistance + expandedDistance;

                out.printf("Galaxy pair %s has raw distance %d and crosses %d horizontal and %d vertical expansions (%d total) for %d additional distance for a total of %d\n",
                        this, rawDistance, expansionColumnsTraversed, expansionRowsTraversed, totalExpansions, expandedDistance, totalDistance);
                return totalDistance;
            }

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("GalaxyPair{");
                sb.append("coordinates=").append(coordinates);
                sb.append('}');
                return sb.toString();
            }
        }
    }

    static String charsToString(LinkedList<LinkedList<Character>> chars) {
        return chars.stream()
                .map(row -> row.stream().map(String::valueOf).reduce(String::concat).orElseThrow())
                .map(row -> row + "\n")
                .reduce(String::concat).orElseThrow();
    }
}
