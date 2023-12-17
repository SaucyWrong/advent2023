package com.quinnheavyindustries.util;

import java.util.Set;

public enum Direction {
    North, South, West, East, None;

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
            case None:
                yield current;
        };
    }

    public static Direction relative(Point src, Point dst) {
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
        return None;
    }

    public Set<Direction> orthogonals() {
        return switch (this) {
            case North, South -> Set.of(West, East);
            case West, East -> Set.of(North, South);
            case None -> Set.of(North, South, West, East); // arguable, but it works for a specific AoC problem
        };
    }

    public boolean opposes(Direction other) {
        return switch (this) {
            case North -> other == South;
            case South -> other == North;
            case West -> other == East;
            case East -> other == West;
            case None -> false;
        };
    }
}
