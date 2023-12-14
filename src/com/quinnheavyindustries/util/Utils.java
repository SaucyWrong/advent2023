package com.quinnheavyindustries.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static String readInputAsString(String filename) {
        try (var inputStream = loadStream(filename)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public static InputStream loadStream(String filename) {
        var loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(filename);
    }

    public static List<String> loadLines(String filename) {
        try (var inputStream = loadStream(filename)) {
            if (inputStream != null) {
                var reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader.lines().toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    public static char[][] fillTowDimensionalCharArray(String input) {
        var lines = input.split("\\n");
        var maxLineLength = Arrays.stream(lines).mapToInt(String::length).max().orElseThrow();
        var result = new char[lines.length][maxLineLength];
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            for (var j = 0; j < line.length(); j++) {
                result[i][j] = line.charAt(j);
            }
        }
        return result;
    }

    public static char[][] rotateMatrixClockwise(char[][] matrix) {
        var result = new char[matrix[0].length][matrix.length];
        for (var i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            for (var j = 0; j < row.length; j++) {
                result[j][matrix.length - i - 1] = matrix[i][j];
            }
        }
        return result;
    }

    public static String charMatrixToString(char[][] matrix) {
        var result = new StringBuilder();
        for (var row : matrix) {
            for (var cell : row) {
                result.append(cell);
            }
            result.append("\n");
        }
        return result.toString();
    }
}
