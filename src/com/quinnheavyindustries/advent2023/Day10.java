package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Direction;
import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.out;

public class Day10 {
    public static void main(String[] args) {
        var maze = new Maze(Utils.loadLines("day10-puzzle-input"));
        out.println("Maze:\n" + maze.render());
        var totalSteps = new Runner(maze).run();
        out.println("Maze after running:\n" + maze.render());
        out.println("Ran loop in " + totalSteps + " steps");
        out.println("Solution: " + totalSteps / 2);

        maze.floodMaze();
        out.println(maze.render());

        System.out.println("Remaining unflooded tiles: " + maze.countUnfloodedTiles());
    }

    static class Maze {
        Character[][] maze;
        Point start;
        int height, width;
        Set<Point> lastFlooded;
        Set<Point> loopTiles = new HashSet<>();

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
            out.printf("Loaded %d x %d maze with starting coordinates %s\n",
                    input.size(), input.getFirst().length(), start);
        }

        char valueAt(Point point) {
            if (point.isInBounds(maze)) {
                return maze[point.y()][point.x()];
            }
            throw new RuntimeException("Invalid coordinate: " + point);
        }

        boolean isConnectedToStart(Point point) {
            if (point.isInBounds(maze)) {
                var valueAtPoint = maze[point.y()][point.x()];

                return (point.isRightOf(start) && Set.of('-', '7', 'J').contains(valueAtPoint)) ||
                        (point.isLeftOf(start) && Set.of('-', 'F', 'L').contains(valueAtPoint)) ||
                        (point.isBelow(start) && Set.of('|', 'J', 'L').contains(valueAtPoint)) ||
                        (point.isAbove(start) && Set.of('|', '7', 'F').contains(valueAtPoint));
            }
            return false;
        }

        void floodMaze() {
            if (lastFlooded != null) { return; }
            maze[0][0] = '~';
            lastFlooded = Set.of(new Point(0, 0));
            while(!lastFlooded.isEmpty()) {
                doFlood();
            }
        }

        void doFlood() {
            var pointsToFlood = lastFlooded.stream()
                    .map(Point::getAllNeighbors)
                    .flatMap(List::stream)
                    .filter(point -> point.isInBounds(maze))
                    .filter(this::permitsWater)
                    .collect(Collectors.toSet());

            pointsToFlood.forEach(this::floodTile);
            lastFlooded = pointsToFlood;
        }

        boolean permitsWater(Point point) {
            return !loopTiles.contains(point) &&
                    !Set.of('#', '~', '*').contains(valueAt(point));
        }

        void floodTile(Point point) {
            maze[point.y()][point.x()] = valueAt(point) == '#' ? '*' : '~';
        }

        int countUnfloodedTiles() {
            var unflooded = 0;
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    if (permitsWater(new Point(x, y))) {
                        unflooded++;
                    }
                }
            }
            return unflooded;
        }

        String render() {
            var sb = new StringBuilder();
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    sb.append(maze[y][x]);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    static class Runner {
        Maze maze;
        Point pos;
        Direction direction;

        public Runner(Maze maze) {
            this.maze = maze;
            pos = maze.start;
            determineInitialHeading();
        }

        void determineInitialHeading() {
            if (pos == null) { throw new RuntimeException("start must not be null"); }
            var firstMove = pos.getCardinalNeighbors().stream()
                    .filter(point -> point.isInBounds(maze.maze))
                    .filter(maze::isConnectedToStart)
                    .findFirst()
                    .orElseThrow();
            direction = Direction.relative(pos, firstMove);
            out.printf("Runner start: %s, first move: %s (%s), initial heading: %s\n",
                    pos, firstMove, maze.valueAt(firstMove), direction);
        }

        public int run() {
            var stepsTaken = 1;
            while (stepsTaken == 1 || !pos.equals(maze.start)) {
                var previous = new Point(pos.x(), pos.y());
                pos = direction.next(pos);
                direction = nextHeading(maze.valueAt(pos));
                stepsTaken++;
                maze.loopTiles.add(previous);
//                maze.maze[previous.y()][previous.x()] = '#';
            }
            return stepsTaken;
        }

        private Direction nextHeading(char currentValue) {
            if (currentValue == 'S' || currentValue == '#') { return Direction.North; }
            return switch (direction) {
                case North -> switch (currentValue) {
                    case '|' : yield Direction.North;
                    case '7' : yield Direction.West;
                    case 'F' : yield Direction.East;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                case South -> switch (currentValue) {
                    case '|' : yield Direction.South;
                    case 'L' : yield Direction.East;
                    case 'J' : yield Direction.West;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                case West -> switch (currentValue) {
                    case '-' : yield Direction.West;
                    case 'L' : yield Direction.North;
                    case 'F' : yield Direction.South;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                case East -> switch (currentValue) {
                    case '-' : yield Direction.East;
                    case '7' : yield Direction.South;
                    case 'J' : yield Direction.North;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentValue);
                };
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
        }
    }

}
