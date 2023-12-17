package com.quinnheavyindustries.util;

public record PointAndDirection(Point point, Direction direction) {

    public PointAndDirection next() {
        return new PointAndDirection(direction.next(point), direction);
    }

    public PointAndDirection next(Direction newDirection) {
        return new PointAndDirection(newDirection.next(point), newDirection);
    }

}
