package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.Arrays;

import static java.lang.System.*;

public class Day15 {

    public static void main(String[] args) {
        var startTime = nanoTime();
        var input = Utils.readInputAsString("day15-puzzle");
        var solution = Arrays.stream(input.split(","))
                .mapToLong(Day15::calculateHash)
                .sum();
        var endTime = nanoTime();
        out.println("Solution: " + solution);
        out.println("Took: " + Duration.ofNanos(endTime - startTime));
    }

    static long calculateHash(String s) {
        byte[] bytes = s.getBytes();
        var result = 0L;
        for (var i = 0; i < bytes.length; i++) {
            result += bytes[i];
            result *= 17;
            result %= 256;
        }
        return result;
    }

}
