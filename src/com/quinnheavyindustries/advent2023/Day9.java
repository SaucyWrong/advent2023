package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Day9 {
    public static void main(String[] args) {
        List<List<Long>> sequences = Utils.loadLines("day9-puzzle-input").stream()
                .map(line -> line.split("\\s+"))
                .map(Day9::toLongs)
                .toList();

        var result = sequences.stream()
                .mapToLong(Day9::derivePreviousValue)
                .sum();

        System.out.println("Result: " + result);
    }

    static List<Long> toLongs(String[] strArray) {
        return Arrays.stream(strArray)
                .map(Long::parseLong)
                .toList();
    }

    static long derivePreviousValue(List<Long> inputSequence) {
        List<List<Long>> intermediateSequences = new LinkedList<>();
        intermediateSequences.add(List.copyOf(inputSequence));
        System.out.println("Deriving: " + inputSequence);

        while(intermediateSequences.getLast().stream().anyMatch(i -> i != 0)) {
            List<Long> seq = new ArrayList<>();
            for (var i = 1; i < inputSequence.size(); i++) {
                seq.add(inputSequence.get(i) - inputSequence.get(i - 1));
            }
            intermediateSequences.add(seq);
            inputSequence = List.copyOf(seq);
            System.out.println(seq);
        }

        var result = intermediateSequences.reversed().stream()
                .mapToLong(List::getFirst).reduce((a, b) -> b - a).orElseThrow();
        System.out.println("Next number in sequence: " + result);

        return result;
    }

}
