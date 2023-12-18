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

    public List<Point> range(Direction direction, int length) {
        var start = new Point(this.x, this.y);
        List<Point> result = new ArrayList<>();
        result.add(start);
        for (int i = 0; i < length; i++) {
            result.add(direction.next(result.getLast()));
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

    public long manhattanDistance(Point other) {
        return Math.abs(x - other.x()) + Math.abs(y - other.y());
    }

    public boolean isInBounds(char[][] grid) {
        return y >= 0 && y < grid.length && x >= 0 && x < grid[0].length;
    }

    public <T> boolean isInBounds(T[][] grid) {
        return y >= 0 && y < grid.length && x >= 0 && x < grid[0].length;
    }

    public <T> boolean isOutOfBounds(T[][] grid) {
        return !isInBounds(grid);
    }

    @Override
    public int compareTo(Point o) {
        return Comparator.comparingInt(Point::y)
                .thenComparingInt(Point::x)
                .compare(this, o);
    }
}
