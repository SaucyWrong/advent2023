package com.quinnheavyindustries.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record Point(int x, int y) implements Comparable<Point> {
    public List<Point> getAllNeighbors() {
        var neighbors = new ArrayList<>(getCardinalNeighbors());
        neighbors.addAll(List.of(
                new Point(x - 1, y - 1),
                new Point(x + 1, y - 1),
                new Point(x - 1, y + 1),
                new Point(x + 1, y + 1)
        ));
        return neighbors;
    }

    public List<Point> getCardinalNeighbors() {
        return List.of(
                new Point(x - 1, y),
                new Point(x + 1, y),
                new Point(x, y - 1),
                new Point(x, y + 1)
        );
    }

    public List<Point> getHorizontalRange(int length) {
        List<Point> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            result.add(new Point(x + i, y));
        }
        return result;
    }

    public boolean isAbove(Point other) {
        return y < other.y();
    }

    public boolean isBelow(Point other) {
        return y > other.y();
    }

    public boolean isLeftOf(Point other) {
        return x < other.x();
    }

    public boolean isRightOf(Point other) {
        return x > other.x();
    }

    @Override
    public int compareTo(Point o) {
        return Comparator.comparingInt(Point::y)
                .thenComparingInt(Point::x)
                .compare(this, o);
    }
}
