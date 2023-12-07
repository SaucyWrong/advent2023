package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {

    private static long totalSeedStock = 0;
    private static AtomicLong progress = new AtomicLong(0);

    public static void main(String[] args) {
        var startTime = System.currentTimeMillis();
        String inputData = Utils.readInputAsString("day5-puzzle-input");
        var sections = Arrays.stream(inputData.split("\\n\\n")).toList();
        var seedValues = extractSeedBatches(sections.getFirst());
        System.out.println("Seed values: " + seedValues);
        totalSeedStock = seedValues.stream().mapToLong(SeedBatch::size).sum();


        System.out.println("Configuring Almanac Mappers...");
        var almanacMappers = sections.subList(1, sections.size()).stream()
                .map(Day5::extractAlmanacMap)
                .peek(System.out::println)
                .collect(Collectors.toMap(AlmanacMapper::name, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        var almanac = new Almanac(almanacMappers);
        System.out.printf("Translating %d seed values...\n", totalSeedStock);
        var minimumLocationValue = seedValues.stream()
                .flatMap(SeedBatch::streamSeedValues)
                .map(almanac::fullyTranslateValue)
                .min(Long::compareTo)
                .orElseThrow();

        System.out.println("Smallest Location: " + minimumLocationValue);
        System.out.println("Solution: " + minimumLocationValue);
        var endTime = System.currentTimeMillis();
        var duration = Duration.ofMillis(endTime - startTime);
        System.out.println("Duration: " + duration);
    }

    private static List<SeedBatch> extractSeedBatches(String seedSection) {
        var stringValues = seedSection.replace("seeds: ", "").split("\\W+");
        var batches = new ArrayList<SeedBatch>();
        for (int i = 0; i < stringValues.length; i += 2) {
            var batch = new SeedBatch(Long.parseLong(stringValues[i]), Long.parseLong(stringValues[i + 1]));
            System.out.println("Seed batch: " + batch);
            batches.add(batch);
        }
        return batches;
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

    public record SeedBatch(long initialValue, long size) {
        // return a parallel stream of seed values
        public Stream<Long> streamSeedValues() {
            return Stream.iterate(initialValue, i -> i + 1).limit(size);
        }
    }

    public record Almanac(Map<String, AlmanacMapper> mappers) {
        public long fullyTranslateValue(long value) {
            for (AlmanacMapper mapper : mappers.values()) {
                value = mapper.translate(value);
            }
            if (progress.incrementAndGet() % 10000000 == 0) {
                System.out.printf("(%.2f%%)\n", (float) progress.get() / totalSeedStock * 100);
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
