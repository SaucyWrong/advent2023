package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.time.Duration;

import static java.lang.System.out;

public class Day14 {

    public static void main(String[] args) {
        var startTime = System.nanoTime();
        var input = Utils.readInputAsString("day14-puzzle");
        out.println("Solution: " + shiftAllRocksNortherlyAndCalculateLoad(input));
        var endTime = System.nanoTime();
        out.println("Time: " + Duration.ofNanos(endTime - startTime));
    }

    static long shiftAllRocksNortherlyAndCalculateLoad(String input) {
//        out.println("Unshifted input:\n" + input);
        var matrix = Utils.fillTowDimensionalCharArray(input);
        for (var i = 1; i < matrix.length; i++) {
            for (var j =0; j < matrix[0].length; j++) {
                var symbolToMove = matrix[i][j];
                if (symbolToMove == 'O' && matrix[i - 1][j] == '.') {
//                    out.printf("Movable rock found at (%d, %d)\n", i, j);
                    matrix[i][j] = '.';
                    for (var currentRow = i; currentRow >= -1; currentRow--) {
//                        out.printf("(%d, %d)...", currentRow, j);
                        if (currentRow == -1 || matrix[currentRow][j] == '#' || matrix[currentRow][j] == 'O') {
//                            out.printf("Final (%d, %d)\n", currentRow + 1, j);
                            matrix[currentRow + 1][j] = symbolToMove;
                            break;
                        }
                    }
                }
            }
        }
//        out.println("Shifted input:\n" + Utils.charMatrixToString(matrix));
        return calculatLoadOnNorthernSupport(matrix);
    }

    static long calculatLoadOnNorthernSupport(char[][] matrix) {
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
