package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.System.*;

public class Day15 {

    static Pattern cmdPattern = Pattern.compile("^(?<label>\\w+)(?<op>\\W)(?<focalLength>\\d?)$");
    static TreeMap<Long, LinkedHashMap<String, Long>> lensBoxes = new TreeMap<>();

    public static void main(String[] args) {
        var startTime = nanoTime();
        var input = Utils.readInputAsString("day15-puzzle");

        var commands = input.split(",");
        Arrays.stream(commands).forEach(Day15::processCmd);
        var solution = calculateFocusingPower();

        var endTime = nanoTime();
        out.println("Solution: " + solution);
        out.println("Took: " + Duration.ofNanos(endTime - startTime));
    }

    static void processCmd(String cmd) {
//        out.printf("cmd: %s\n", cmd);
        var matcher = cmdPattern.matcher(cmd);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid command: " + cmd);
        }
        var label = matcher.group("label");
        var focalLength = matcher.group("focalLength");

        switch(matcher.group("op")) {
            case "=" -> {
//                out.printf("Adding lens: %s, %s\n", label, focalLength);
                addLens(label, Long.parseLong(focalLength));
            }
            case "-" -> {
//                out.printf("Removing lens: %s\n", label);
                removeLens(label);
            }
        }
//        out.printf("After %s: %s\n", cmd, lensBoxes);
    }

    static void addLens(String label, long focalLength) {
        var hashBucket = getOrCreateHashBucket(label);
        if (hashBucket.containsKey(label)) {
            hashBucket.replace(label, focalLength);
        } else {
            hashBucket.put(label, focalLength);
        }
    }

    static void removeLens(String label) {
        var hashValue = hash(label);
        if (lensBoxes.containsKey(hashValue)) {
            lensBoxes.get(hash(label)).remove(label);
        }
    }

    static LinkedHashMap<String, Long> getOrCreateHashBucket(String label) {
        return lensBoxes.compute(hash(label), (k, v) -> v == null ? new LinkedHashMap<>() : v);
    }

    static long calculateFocusingPower() {
        var focusingPower = 0L;
        for (var entry: lensBoxes.entrySet()) {
            var bucketNumber = entry.getKey();
            var hashBucket = entry.getValue();
            var lensOrdinal = 1L;
            for (var lensPower: hashBucket.values()) {
                focusingPower += ((bucketNumber + 1) * lensOrdinal * lensPower);
                lensOrdinal++;
            }
        }
        return focusingPower;
    }

    static long hash(String s) {
        byte[] bytes = s.getBytes();
        var result = 0L;
        for (byte aByte : bytes) {
            result += aByte;
            result *= 17;
            result %= 256;
        }
        return result;
    }

}
