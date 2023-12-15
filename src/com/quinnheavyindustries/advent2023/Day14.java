package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;
import org.graalvm.collections.Pair;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class Day14 {

    public static Map<String, Pair<String, Long>> memoizedResults = new HashMap<>();
    public static long spins = 1_000_000_000L;
    public static boolean cycleTripped = false;

    public static void main(String[] args) {
        var startTime = System.nanoTime();
        var input = Utils.readInputAsString("day14-puzzle");
        for (var i = 0L; i < spins; i++) {
            if (memoizedResults.containsKey(input) && !cycleTripped) {
                var cycleLength = i - memoizedResults.get(input).getRight();
                var remainingSpins = spins - i;
                var remainingSpinsModCycleLength = remainingSpins % cycleLength;
                cycleTripped = true;
                out.printf("\n--- Cycle detected at spin %d, cycle length %d, remaining spins %d, remaining spins mod cycle length %d ---\n", i, cycleLength, remainingSpins, remainingSpinsModCycleLength);
                i = spins - remainingSpinsModCycleLength;
                out.printf("--- Skipping all the way to spin %d ---\n", i);
            }
            input = runOneSpinCycle(input, i);
        }
        out.println("\n--- Finished spinning ---");
        var load = calculateLoadOnNorthernSupport(input);
        var endTime = System.nanoTime();
        out.println("Load: " + load);
        out.println("Time: " + Duration.ofNanos(endTime - startTime));
    }

    public static String runOneSpinCycle(String input, long currentSpinCount) {
        if (memoizedResults.containsKey(input)) {
            return memoizedResults.get(input).getLeft();
        }

        var matrix = Utils.fillTowDimensionalCharArray(input);
        // first shift all of the rocks up (north)
        shiftAllRocksUp(matrix);

        for (var i = 0; i < 4; i++) {
            matrix = Utils.rotateMatrixClockwise(matrix);
            if (i < 3) {
                shiftAllRocksUp(matrix);
            }
        }

        var result = Utils.charMatrixToString(matrix);
        memoizedResults.put(input, Pair.create(result, currentSpinCount));
        return result;
    }

    static void shiftAllRocksUp(char[][] matrix) {
        for (var i = 1; i < matrix.length; i++) {
            for (var j =0; j < matrix[0].length; j++) {
                var symbolToMove = matrix[i][j];
                if (symbolToMove == 'O' && matrix[i - 1][j] == '.') {
                    matrix[i][j] = '.';
                    for (var currentRow = i; currentRow >= -1; currentRow--) {
                        if (currentRow == -1 || matrix[currentRow][j] == '#' || matrix[currentRow][j] == 'O') {
                            matrix[currentRow + 1][j] = symbolToMove;
                            break;
                        }
                    }
                }
            }
        }
    }

    static long calculateLoadOnNorthernSupport(String input) {
        var matrix = Utils.fillTowDimensionalCharArray(input);
        var load = 0L;
        long loadFactor = matrix.length;
        for (char[] characters : matrix) {
            for (var column = 0; column < matrix[0].length; column++) {
                if (characters[column] == 'O') {
                    load += loadFactor;
                }
            }
            loadFactor--;
        }
        return load;
    }

}
