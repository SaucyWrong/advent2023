package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class Day12 {

    static Pattern questionMarkMatcher = Pattern.compile("\\?");

    public static void main(String[] args) {
        var start = System.nanoTime();
        var lines = Utils.loadLines("day12-puzzle");
        var hotSprings = lines.stream()
                .map(line -> line.split("\\s+"))
                .toList();

        var solution = hotSprings.stream()
                .mapToLong(spring -> searchForValidArrangements(spring[0], spring[1]))
                .sum();
        var end = System.nanoTime();
        out.println("Solution: " + solution);
        out.println("Time: " + Duration.ofNanos(end - start));
    }

    static long searchForValidArrangements(String symbolMap, String brokenSpringNumberString) {
        var brokenSpringGroups = Arrays.stream(brokenSpringNumberString.split(","))
                .map(Integer::parseInt)
                .toList();

        var unknownSymbolLocations = new ArrayList<Integer>();
        var matcher = questionMarkMatcher.matcher(symbolMap);
        while (matcher.find()) {
            unknownSymbolLocations.add(matcher.start());
        }
        var numCombos = Double.valueOf(Math.pow(2, unknownSymbolLocations.size())).intValue();

        out.printf("map: %s, brokenSpringGroups: %s, Unknown symbol locations: %s, possible combos: %d\n",
                symbolMap, brokenSpringGroups, unknownSymbolLocations, numCombos);
        var validCombos = countCombos(symbolMap, brokenSpringGroups);
        out.printf("Found %d valid combinations\n", validCombos);

        return validCombos;
    }

    static long countCombos(String symbolMap, List<Integer> brokenGroups) {
        if (symbolMap.isEmpty()) { // if the end of the symbol map has been reached
            return brokenGroups.isEmpty() ? 1 : 0; // there must be no remaining broken spring groups
        }
        if (brokenGroups.isEmpty()) { // if the end of the spring groups array has been reached
            return symbolMap.contains("#") ? 0 : 1; // the remaining symbol map must contain no broken springs (#)
        }

        var result = 0L;
        var firstSymbol = symbolMap.charAt(0);

        if (firstSymbol == '.' || firstSymbol == '?') {
            // first char is a working spring (a . or a ? that will be assigned a .)
            // in this case, just continue the recursive search with the next substring
            result += countCombos(symbolMap.substring(1), brokenGroups);
        }

        if (firstSymbol == '#' || firstSymbol == '?') {
            // first char is a broken spring (a # or a ? that will be assigned a #)
            // this marks the start of a group of broken springs
            var numBroken = brokenGroups.getFirst();
            boolean canContinueSearching =
                    numBroken <= symbolMap.length() // there are at least enough springs remaining
                    && symbolMap.indexOf('.', 0, numBroken) == -1 // none of the springs needed to form a block are operational
                    && (symbolMap.length() == numBroken || symbolMap.charAt(numBroken) != '#'); // any following spring _is_ operational

            if (canContinueSearching) {
                var strIndex = Math.min(numBroken + 1, symbolMap.length());
                result += countCombos(symbolMap.substring(strIndex), brokenGroups.subList(1, brokenGroups.size()));
            }
        }

        return result;
    }

}
