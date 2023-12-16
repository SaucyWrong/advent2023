package com.quinnheavyindustries.util;

import java.util.Set;

public enum Heading {
    North, South, West, East;

    public Point next(Point current) {
        return switch (this) {
            case North:
                yield new Point(current.x(), current.y() - 1);
            case South:
                yield new Point(current.x(), current.y() + 1);
            case West:
                yield new Point(current.x() - 1, current.y());
            case East:
                yield new Point(current.x() + 1, current.y());
        };
    }

    public static Heading relative(Point src, Point dst) {
        if (dst.isAbove(src)) {
            return North;
        }
        if (dst.isBelow(src)) {
            return South;
        }
        if (dst.isLeftOf(src)) {
            return West;
        }
        if (dst.isRightOf(src)) {
            return East;
        }
        throw new RuntimeException("Cannot determine relative direction of two equal points");
    }

    public Set<Heading> orthogonals() {
        return switch (this) {
            case North, South -> Set.of(West, East);
            case West, East -> Set.of(North, South);
        };
    }
}
