package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.Utils;

import java.util.*;

import static java.lang.System.out;

public class Day11 {

    static LinkedList<Character> dots(int length) {
        return ".".repeat(length).chars().mapToObj(c -> (char) c)
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    public static void main(String[] args) {
        LinkedList<LinkedList<Character>> galaxyChart = loadGalaxyChart();
        out.println("Unexpended galaxy chart:\n" + charsToString(galaxyChart));

        expandGalaxyChart(galaxyChart);
        out.println("Expanded galaxy chart:\n" + charsToString(galaxyChart));

        var allGalaxies = locateGalaxies(galaxyChart);
        var uniqueGalaxyPairs = findGalaxyPairs(allGalaxies);
        out.printf("Found %d galaxies and %d unique galaxy pairs\n", allGalaxies.size(), uniqueGalaxyPairs.size());

        var solution = uniqueGalaxyPairs.stream()
                .mapToLong(GalaxyPair::distance)
                .sum();
        out.printf("\nSolution: %d\n", solution);
    }

    static LinkedList<LinkedList<Character>> loadGalaxyChart() {
        LinkedList<LinkedList<Character>> unexpandedGalaxy = Utils.loadLines("day11-puzzle").stream()
                .map(line -> new LinkedList<>(line.chars().mapToObj(c -> (char) c).toList()))
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
        out.printf("Loaded unexpanded galaxy chart with %d rows and %d columns\n",
                unexpandedGalaxy.size(), unexpandedGalaxy.getFirst().size());
        return unexpandedGalaxy;
    }

    static void expandGalaxyChart(LinkedList<LinkedList<Character>> unexpandedGalaxy) {
        var expandColumns = new ArrayList<Integer>();
        var expandRows = new ArrayList<Integer>();
        for (var x = 0; x < unexpandedGalaxy.getFirst().size(); x++) {
            int finalX = x;
            if (unexpandedGalaxy.stream().map(row -> row.get(finalX)).allMatch(c -> c == '.')) {
                expandColumns.add(finalX);
            }
        }
        for (var y = 0; y < unexpandedGalaxy.size(); y++) {
            if (unexpandedGalaxy.get(y).stream().allMatch(c -> c == '.')) {
                expandRows.add(y);
            }
        }

        out.printf("Expansion required in columns %s and rows %s\n", expandColumns, expandRows);
        var rowLength = unexpandedGalaxy.getFirst().size();
        expandRows.reversed().forEach(row -> unexpandedGalaxy.add(row, dots(rowLength)));
        expandColumns.reversed().forEach(column -> unexpandedGalaxy.forEach(row -> row.add(column, '.')));
    }

    static List<Point> locateGalaxies(LinkedList<LinkedList<Character>> galaxyChart) {
        var galaxies = new ArrayList<Point>();
        for (var y = 0; y < galaxyChart.size(); y++) {
            for (var x = 0; x < galaxyChart.get(y).size(); x++) {
                if (galaxyChart.get(y).get(x) == '#') {
                    out.printf("Found galaxy at %d, %d\n", x, y);
                    galaxies.add(new Point(x, y));
                }
            }
        }
        return galaxies;
    }

    static Set<GalaxyPair> findGalaxyPairs(List<Point> galaxies) {
        var galaxyPairs = new HashSet<GalaxyPair>();
        for (var i = 0; i < galaxies.size(); i++) {
            for (var j = i + 1; j < galaxies.size(); j++) {
                galaxyPairs.add(new GalaxyPair(galaxies.get(i), galaxies.get(j)));
            }
        }
        return galaxyPairs;
    }

    static class GalaxyPair {
        TreeSet<Point> coordinates = new TreeSet<>();

        GalaxyPair(Point galaxyA, Point galaxyB) {
            coordinates.add(galaxyA);
            coordinates.add(galaxyB);
        }

        public long distance() {
            if (coordinates.isEmpty() || coordinates.size() == 1) {
                return 0;
            }
            return coordinates.first().gridDistanceTo(coordinates.last());
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GalaxyPair{");
            sb.append("coordinates=").append(coordinates);
            sb.append('}');
            return sb.toString();
        }
    }

    static String charsToString(LinkedList<LinkedList<Character>> chars) {
        return chars.stream()
                .map(row -> row.stream().map(String::valueOf).reduce(String::concat).orElseThrow())
                .map(row -> row + "\n")
                .reduce(String::concat).orElseThrow();
    }
}
