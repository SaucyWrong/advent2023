package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.Arrays;

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

        var solution = simulateRace(Long.parseLong(raceTimeString), Long.parseLong(distanceString));

        System.out.printf("\nSolution: %d\n", solution);
    }

    public static int simulateRace(long time, long distanceToBeat) {
        System.out.printf("Race: time=%d, distanceToBeat=%d\n", time, distanceToBeat);
        var racesWon = 0;
        for (int i = 1; i < time; i++) {
            var distanceAchieved = i * (time - i);
            if (distanceAchieved > distanceToBeat) {
                racesWon++;
            }
        }
        System.out.printf("--- winning solutions: %d ---\n", racesWon);
        return racesWon;
    }

}
