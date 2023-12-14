package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;
import org.graalvm.collections.Pair;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

public class Day13 {

    static Pair<Integer, Integer> ZERO_ZERO = Pair.create(0, 0);

    public static void main(String[] args) {
        var startTime = System.nanoTime();
        var patterns = Arrays.asList(Utils.readInputAsString("day13-puzzle").split("\\n{2}"));
        var solution = patterns.stream()
                .mapToInt(Day13::fixSmudge)
                .sum();
        var endTime = System.nanoTime();

        out.println("Solution: " + solution);
        out.println("Time: " + Duration.ofNanos(endTime - startTime));
    }

    static int fixSmudge(String input) {
        var smudgedResult = findReflections(input, null, null);
        Integer ignoreRow = smudgedResult.getLeft() != 0 ? smudgedResult.getLeft() : null;
        Integer ignoreColumn = smudgedResult.getRight() != 0 ? smudgedResult.getRight() : null;

        var chars = Utils.fillTowDimensionalCharArray(input);
        for (var i = 0; i < chars.length; i++) {
            for (var j = 0; j < chars[0].length; j++) {
                var originalByte = chars[i][j];
                char replacementByte = originalByte == '.' ? '#' : '.';

                chars[i][j] = replacementByte;
                var newResult = findReflections(Utils.charMatrixToString(chars), ignoreRow, ignoreColumn);
                chars[i][j] = originalByte;

                if (!newResult.equals(ZERO_ZERO) && !newResult.equals(smudgedResult)) {
                    return calcFixedValue(smudgedResult, newResult);
                }
            }
        }
        throw new RuntimeException("No fix found");
    }

    static int calcFixedValue(Pair<Integer, Integer> smudgedValue, Pair<Integer, Integer> fixedValue) {
        if (!smudgedValue.getLeft().equals(fixedValue.getLeft()) && fixedValue.getLeft() != 0) {
            return fixedValue.getLeft() * 100;
        }
        return fixedValue.getRight();
    }

    static Pair<Integer, Integer> findReflections(String input, Integer ignoreRow, Integer ignoreColumn) {
        return Pair.create(findReflectiveRow(input, ignoreRow), findReflectiveColumn(input, ignoreColumn));
    }

    static int findReflectiveRow(String input, Integer ignoreRow) {
        var lines = Arrays.asList(input.split("\\n"));

        for (var i = 0; i < lines.size(); i++) {
            if (ignoreRow != null && (i + 1) == ignoreRow) continue;
            var topSection = lines.subList(0 , i + 1);
            var bottomSection = lines.subList(i + 1, lines.size());
            if (isReflective(topSection, bottomSection)) {
                return i + 1;
            }
        }
        return 0;
    }

    static int findReflectiveColumn(String input, Integer ignoreColumn) {
        var charMatrix = Utils.fillTowDimensionalCharArray(input);
        var rotatedMatrix = Utils.rotateMatrixClockwise(charMatrix);
        var rotatedString = Utils.charMatrixToString(rotatedMatrix);
        return findReflectiveRow(rotatedString, ignoreColumn);
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
