package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.Utils;

import java.util.List;
import java.util.Set;

public class Day10 {
    public static void main(String[] args) {
        var maze = new Maze(Utils.loadLines("day10-puzzle-input"));
        var totalSteps = new Runner(maze).run();
        System.out.println("Ran loop in " + totalSteps + " steps");
        System.out.println("Solution: " + totalSteps / 2);
    }

    static class Maze {
        Character[][] maze;
        Point start;
        int height, width;

        Maze(List<String> input) {
            height = input.size();
            width = input.getFirst().length();
            maze = new Character[height][width];
            for (var currentY = 0; currentY < input.size(); currentY++) {
                var line = input.get(currentY);
                for (var currentX = 0; currentX < line.length(); currentX++) {
                    var character = line.charAt(currentX);
                    maze[currentY][currentX] = character;
                    if (character == 'S') {
                        start = new Point(currentX, currentY);
                    }
                }
            }
            System.out.printf("Loaded %d x %d maze with starting coordinates %s\n",
                    input.size(), input.getFirst().length(), start);
        }

        char valueAt(Point point) {
            if (isInBounds(point)) {
                return maze[point.y()][point.x()];
            }
            throw new RuntimeException("Invalid coordinate: " + point);
        }

        boolean isInBounds(Point point) {
            return point.x() >= 0 && point.x() < width &&
                    point.y() >= 0 && point.y() < height;
        }

        boolean isConnectedToStart(Point point) {
            if (isInBounds(point)) {
                var valueAtPoint = maze[point.y()][point.x()];

                return (point.isRightOf(start) && Set.of('-', '7', 'J').contains(valueAtPoint)) ||
                        (point.isLeftOf(start) && Set.of('-', 'F', 'L').contains(valueAtPoint)) ||
                        (point.isBelow(start) && Set.of('|', 'J', 'L').contains(valueAtPoint)) ||
                        (point.isAbove(start) && Set.of('|', '7', 'F').contains(valueAtPoint));
            }
            return false;
        }
    }

    static class Runner {
        Maze maze;
        Point pos;
        Heading heading;

        public Runner(Maze maze) {
            this.maze = maze;
            pos = maze.start;
            determineInitialHeading();
        }

        void determineInitialHeading() {
            if (pos == null) { throw new RuntimeException("start must not be null"); }
            var firstMove = pos.getCardinalNeighbors().stream()
                    .filter(maze::isInBounds)
                    .filter(maze::isConnectedToStart)
                    .findFirst()
                    .orElseThrow();
            heading = Heading.relative(pos, firstMove);
            System.out.printf("Runner start: %s, first move: %s (%s), initial heading: %s\n",
                    pos, firstMove, maze.valueAt(firstMove), heading);
        }

        public int run() {
            var stepsTaken = 1;
            while (stepsTaken == 1 || !pos.equals(maze.start)) {
                pos = heading.nextPosition(pos);
                System.out.printf("%d Traveled %s to %s (%s). ", stepsTaken, heading, pos, maze.valueAt(pos));
                heading = heading.nextHeading(maze.valueAt(pos));
                System.out.printf("Now heading %s\n", heading);
                stepsTaken++;
            }
            return stepsTaken;
        }
    }

    enum Heading {
        N, S, W, E;

        Point nextPosition(Point current) {
            return switch (this) {
                case N: yield new Point(current.x(), current.y() - 1);
                case S: yield new Point(current.x(), current.y() + 1);
                case W: yield new Point(current.x() - 1, current.y());
                case E: yield new Point(current.x() + 1, current.y());
            };
        }

        Heading nextHeading(char currentValue) {
            if (currentValue == 'S') { return N; }
            return switch (this) {
                case N -> switch (currentValue) {
                    case '|' : yield N;
                    case '7' : yield W;
                    case 'F' : yield E;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                case S -> switch (currentValue) {
                    case '|' : yield S;
                    case 'L' : yield E;
                    case 'J' : yield W;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                case W -> switch (currentValue) {
                    case '-' : yield W;
                    case 'L' : yield N;
                    case 'F' : yield S;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                case E -> switch (currentValue) {
                    case '-' : yield E;
                    case '7' : yield S;
                    case 'J' : yield N;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
            };
        }

        static Heading relative(Point src, Point dst) {
            if (dst.isAbove(src)) { return N; }
            if (dst.isBelow(src)) { return S; }
            if (dst.isLeftOf(src)) { return W; }
            if (dst.isRightOf(src)) { return E; }
            throw new RuntimeException("Cannot determine relative direction of two equal points");
        }
    }
}
