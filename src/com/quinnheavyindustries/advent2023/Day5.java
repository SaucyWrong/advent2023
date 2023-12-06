package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day5 {

    public static void main(String[] args) {
        String inputData = Utils.readInputAsString("day5-puzzle-input");
        var sections = Arrays.stream(inputData.split("\\n\\n")).toList();
        var seedValues = extractSeedValues(sections.getFirst());
        System.out.println("Seed values: " + seedValues);

        System.out.println("Configuring Almanac Mappers...");
        var almanacMappers = sections.subList(1, sections.size()).stream()
                .map(Day5::extractAlmanacMap)
                .peek(System.out::println)
                .collect(Collectors.toMap(AlmanacMapper::name, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        var almanac = new Almanac(almanacMappers);
        System.out.println("Translating seed values...");
        var translatedSeedValues = seedValues.stream()
                .peek(value -> System.out.println("Translating seed value: " + value))
                .map(almanac::fullyTranslateValue)
                .sorted()
                .toList();

        System.out.println("Final location values: " + translatedSeedValues);
        System.out.println("Solution: " + translatedSeedValues.getFirst());
    }

    private static List<Long> extractSeedValues(String seedSection) {
        var stringValues = seedSection.replace("seeds: ", "").split("\\W+");
        return Arrays.stream(stringValues)
                .map(Long::parseLong)
                .toList();
    }

    private static AlmanacMapper extractAlmanacMap(String section) {
        var lines = section.split("\\n");
        var name = lines[0].replace(" map:", "");
        var ranges = new ArrayList<MapRange>();
        for (int i = 1; i < lines.length; i++) {
            var rangeStringVals = lines[i].split("\\W+");
            ranges.add(new MapRange(
                    Long.parseLong(rangeStringVals[0]),
                    Long.parseLong(rangeStringVals[1]),
                    Long.parseLong(rangeStringVals[2])
            ));
        }
        return new AlmanacMapper(name, ranges);
    }

    public record Almanac(Map<String, AlmanacMapper> mappers) {
        public long fullyTranslateValue(long value) {
            for (AlmanacMapper mapper : mappers.values()) {
                var input = value;
                value = mapper.translate(value);
                System.out.println(input + " -> " + mapper.name + " -> " + value);
            }
            return value;
        }
    }

    public record AlmanacMapper(String name, List<MapRange> ranges) {
        public long translate(long value) {
            for (MapRange range : ranges) {
                if (range.containsValue(value)) {
                    return value + range.translation();
                }
            }
            return value;
        }
    }

    public record MapRange(long destStart, long sourceStart, long length) {
        public boolean containsValue(long value) {
            return value >= sourceStart &&
                    value < sourceStart + length;
        }

        private long translation() {
            return destStart - sourceStart;
        }
    }
}
