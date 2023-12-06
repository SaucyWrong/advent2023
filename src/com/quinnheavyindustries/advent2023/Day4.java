package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4 {

    public static void main(String[] args) {
        var solution = Utils.loadLines("day4-puzzle-input").stream()
                .map(Scratcher::new)
                .mapToInt(Scratcher::getCardScore)
                .sum();
        System.out.printf("solution: %d\n", solution);
    }

    public static class Scratcher {

        private static final Pattern cardNumberMatcher = Pattern.compile("Card (\\d+):");

        public int cardNumber;
        public Set<Integer> winningNumbers;
        public Set<Integer> yourNumbers;

        /**
         * @param input a string like 'Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53' that describes the card number,
         *              the list of winning numbers to the left of the pipe, and the list of numbers you have to the
         *              right of the pipe.
         */
        public Scratcher(String input) {
            parseScratcherInput(input);
        }

        public List<Integer> getMatchingNumbers() {
            return yourNumbers.stream().filter(winningNumbers::contains).collect(Collectors.toList());
        }

        public int getCardScore() {
            return switch (getMatchingNumbers().size()) {
                case 0 -> 0;
                case 1 -> 1;
                default -> {
                    var points = 2;
                    for (var i = 2; i < getMatchingNumbers().size(); i++) {
                        points *= 2;
                    }
                    yield points;
                }
            };
        }

        private void parseScratcherInput(String input) {
            var tokens = input.split(":");
            var numbers = tokens[1];

            var matcher = cardNumberMatcher.matcher(input);
            if (matcher.find()) {
                cardNumber = Integer.parseInt(matcher.group(1));
            }

            var numberTokens = numbers.split("\\|");
            winningNumbers = toIntegerSet(numberTokens[0].trim().split("\\s+"));
            yourNumbers = toIntegerSet(numberTokens[1].trim().split("\\s+"));

            System.out.printf("Card %d: winning numbers: %s, your numbers: %s\n", cardNumber, winningNumbers, yourNumbers);
            System.out.printf("matching numbers: %s, points: %d\n", getMatchingNumbers(), getCardScore());
        }

        private static Set<Integer> toIntegerSet(String[] tokens) {
            return Arrays.stream(tokens).map(Integer::parseInt).collect(Collectors.toSet());
        }
    }
}
