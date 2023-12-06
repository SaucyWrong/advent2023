package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4 {

    public static void main(String[] args) {
        SortedMap<Integer, Scratcher> scratchers = Utils.loadLines("day4-puzzle-input").stream()
                .map(Scratcher::new)
                .collect(Collectors.toMap(s -> s.cardNumber, Function.identity(), (s1, s2) -> s1, TreeMap::new));

        scratchers.forEach((cardNumber, scratcher) -> {
            var winningNumbers = scratcher.getMatchingNumbers();
            var copyFactor = scratcher.copies;
            System.out.printf("Card %d has %d copies and %d matching numbers: %s\n",
                    cardNumber, copyFactor, winningNumbers.size(), winningNumbers);
            var copyStart = cardNumber + 1;
            var copyEnd = copyStart + winningNumbers.size();
            for (var i = copyStart; i < copyEnd; i++) {
                scratchers.get(i).copies += copyFactor;
                System.out.printf("Card %d...adding %d...now has %d copies\n", i, copyFactor, scratchers.get(i).copies);
            }
        });

        var solution = scratchers.values().stream()
                .mapToInt(s -> s.copies)
                .sum();
        System.out.printf("Solution: %d\n", solution);
    }

    public static class Scratcher {

        private static final Pattern cardNumberMatcher = Pattern.compile("Card\\W+(\\d+):");

        public int cardNumber;
        public int copies = 1;
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
        }

        private static Set<Integer> toIntegerSet(String[] tokens) {
            return Arrays.stream(tokens).map(Integer::parseInt).collect(Collectors.toSet());
        }
    }
}
