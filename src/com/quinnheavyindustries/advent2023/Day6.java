package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.Arrays;
import java.util.stream.Stream;

public class Day6 {

    public static void main(String[] args) {
        var input = Utils.readInputAsString("day6-puzzle-input").lines().toList();

        var raceTimeString = Arrays.stream(input.get(0).split("\\W+"))
                .skip(1)
                .map(String::trim)
                .reduce(String::concat)
                .orElseThrow();

        var distanceString = Arrays.stream(input.get(1).split("\\W+"))
                .skip(1)
                .map(String::trim)
                .reduce(String::concat)
                .orElseThrow();

        var approximateRoots = solveRaceEquation(Long.parseLong(raceTimeString), Long.parseLong(distanceString));
        var solution = approximateRoots[1] - approximateRoots[0] + 1;

        System.out.printf("\nSolution: %d\n", ((Double) solution).longValue());
    }

    public static double[] solveRaceEquation(long time, long distanceToBeat) {
        System.out.printf("Race: time=%d, distanceToBeat=%d\n", time, distanceToBeat);
        long a = -1;
        long b = time;
        long c = (-1 * distanceToBeat);

        double sqrt = Math.sqrt(b * b - 4 * a * c);
        var solution1 = (-b + sqrt) / (2 * a);
        var solution2 = (-b - sqrt) / (2 * a);

        var roots = new double[]{Math.floor(++solution1), Math.ceil(--solution2)};
        System.out.printf("--- nearest whole numbers inside roots: %s ---\n", Arrays.toString(roots));
        return roots;
    }

}
