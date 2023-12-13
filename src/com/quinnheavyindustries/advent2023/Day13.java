package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

public class Day13 {
    public static void main(String[] args) {
        var startTime = System.nanoTime();
        var patterns = Arrays.asList(Utils.readInputAsString("day13-puzzle").split("\\n{2}"));
        var solution = patterns.stream()
                .mapToInt(Day13::findReflectionValues)
                .sum();
        var endTime = System.nanoTime();
        out.println("Solution: " + solution);
        out.println("Time: " + Duration.ofNanos(endTime - startTime));
    }

    static int findReflectionValues(String input) {
        var reflectiveRow = findReflectiveRow(input) * 100;
        if (reflectiveRow == 0) {
            var reflectiveColumn = findReflectiveColumn(input);
            out.println("Reflected column value: " + reflectiveColumn);
            return reflectiveColumn;
        }
        out.println("Reflected row value: " + reflectiveRow);
        return reflectiveRow;
    }

    static int findReflectiveRow(String input) {
        var lines = Arrays.asList(input.split("\\n"));

        for (var i = 0; i < lines.size(); i++) {
            var topSection = lines.subList(0 , i + 1);
            var bottomSection = lines.subList(i + 1, lines.size());
            if (isReflective(topSection, bottomSection)) {
                return i + 1;
            }
        }
        return 0;
    }

    static int findReflectiveColumn(String input) {
        var byteMatrix = Utils.fillTwoDimensionalByteArray(input);
        var rotatedMatrix = Utils.rotateMatrixClockwise(byteMatrix);
        var rotatedString = Utils.byteMatrixToString(rotatedMatrix);
        return findReflectiveRow(rotatedString);
    }

    static boolean isReflective(List<String> top, List<String> bottom) {
        if (top.isEmpty() || bottom.isEmpty()) { return false; }
        var topReversed = top.reversed();
        var iterations = Math.min(top.size(), bottom.size());
        for (var i = 0; i < iterations; i++) {
            if (!topReversed.get(i).equals(bottom.get(i))) { return false; }
        }
        return true;
    }
}
