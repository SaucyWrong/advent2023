package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Day1 {

    static Pattern digitMatcher = Pattern.compile("(?=(one|two|three|four|five|six|seven|eight|nine|\\d))");

    public static void main(String[] args) {
        var solution = Utils.loadLines("day1-puzzle-input").stream()
                .filter(line -> !line.isEmpty())
                .map(String::toLowerCase)
                .mapToInt(Day1::getCalibrationValue)
                .sum();
        System.out.println("\nSolution: " + solution + "\n");
    }

    public static int getCalibrationValue(String input) {
        try {
            return Integer.parseInt(matchFirstAndLastDigits(input));
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }

    public static String matchFirstAndLastDigits(String input) {
        var matcher = digitMatcher.matcher(input);
        var matchedDigits = new ArrayList<String>();
        while (matcher.find()) {
            matchedDigits.add(matcher.group(1));
        }
        var firstDigit = digitalValue(matchedDigits.get(0));
        var lastDigit = digitalValue(matchedDigits.get(matchedDigits.size() - 1));

        System.out.printf("%s -> %s%s%n", input, firstDigit, lastDigit);

        return firstDigit + lastDigit;
    }

    public static String digitalValue(String value) {
        return switch (value.toLowerCase()) {
            case "one" -> "1";
            case "two" -> "2";
            case "three" -> "3";
            case "four" -> "4";
            case "five" -> "5";
            case "six" -> "6";
            case "seven" -> "7";
            case "eight" -> "8";
            case "nine" -> "9";
            default -> value;
        };
    }
}
