package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Day2 {

    public static Map<String, Integer> gameConstraints = Map.of(
        "red", 12,
        "green", 13,
        "blue", 14
    );

    public static void main(String[] args) {
        var solution = Utils.loadLines("day2-puzzle-input").stream()
                .filter(line -> !line.isEmpty())
                .map(String::toLowerCase)
                .map(GameResult::fromGameRecord)
                .filter(GameResult::isGamePossible)
                .mapToInt(result -> result.gameId)
                .sum();
        System.out.println("\nSolution: " + solution + "\n");
    }

    public static class GameResult {

        private static final Pattern gameIdPattern = Pattern.compile("game (\\d+)");
        private static final Pattern cubeColorMatcher = Pattern.compile("((\\d+) (?:(red|blue|green)))");

        public Integer gameId;
        public Map<String, Integer> maxSampled = new HashMap<>();

        private GameResult() {}

        public static GameResult fromGameRecord(String gameRecord) {
            var result = new GameResult();
            result.splitAndProcessGameSamples(gameRecord);
            return result;
        }

        public boolean isGamePossible() {
            return gameId != null &&
                    maxSampled.entrySet().stream()
                            .allMatch(entry -> entry.getValue() <= gameConstraints.getOrDefault(entry.getKey(), 0));
        }

        private void splitAndProcessGameSamples(String gameRecord) {
            var colonSplit = gameRecord.split(":");
            var gameIdMatcher = gameIdPattern.matcher(colonSplit[0]);
            if (gameIdMatcher.find()) {
                this.gameId = Integer.parseInt(gameIdMatcher.group(1));
            }

            List<String> bagDraws = List.of(colonSplit[1].split(";"));
            bagDraws.forEach(this::processBagDraws);
            System.out.printf("input --> %s\n\t Game ID: %d: %s, possible: %s\n",
                    gameRecord, gameId, maxSampled, this.isGamePossible()
            );
        }

        private void processBagDraws(String gameSample) {
            var matcher = cubeColorMatcher.matcher(gameSample);
            while (matcher.find()) {
                var color = matcher.group(3);
                var sample = Integer.parseInt(matcher.group(2));
                if (maxSampled.containsKey(color)) {
                    maxSampled.computeIfPresent(color, (k, v) -> Math.max(v, sample));
                } else {
                    maxSampled.put(color, sample);
                }
            }
        }
    }

}
