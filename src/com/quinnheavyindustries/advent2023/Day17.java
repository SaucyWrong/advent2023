package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Point;
import com.quinnheavyindustries.util.PointAndDirection;
import com.quinnheavyindustries.util.Utils;

import java.time.Duration;
import java.util.*;

import static com.quinnheavyindustries.util.Direction.*;
import static java.lang.System.*;

public class Day17 {

    public static void main(String[] args) {
        var startTime = nanoTime();
        var strInput = Utils.readInputAsString("day17-puzzle");
        Integer[][] cityGrid = Utils.fillTwoDimensionsalIntArray(strInput);
        var start = new Point(0, 0);
        var finish = new Point(cityGrid[0].length - 1, cityGrid.length - 1);
        out.println("Solution: " + shortestPath(cityGrid, start, finish));
        var endTime = nanoTime();
        out.println("Total time: " + Duration.ofNanos(endTime - startTime));
    }

    public static int shortestPath(Integer[][] grid, Point start, Point end) {
        int totalHeatLoss = -1;
        var initialState = new State(0, new PointAndDirection(start, None), 0);
        var visited = new HashSet<State>();
        var queue = new PriorityQueue<State>();
        queue.offer(initialState);

        while (!queue.isEmpty()) {
            var state = queue.poll();
            if (state.positionAndDirection.point().equals(end)) {
                if (state.consecutiveSteps >= 4) {
                    totalHeatLoss = state.heatLoss;
                    break;
                }
            }
            if (state.positionAndDirection.point().isOutOfBounds(grid)) {
                continue;
            }
            if (visited.contains(state)) {
                continue;
            }
            visited.add(state);

            // calculate possible next states:
            // - you can continue forward if you haven't already taken 10 steps in a row
            if (state.consecutiveSteps < 10 && state.positionAndDirection.direction() != None) {
                var nextPoint = state.positionAndDirection.next();
                if (nextPoint.point().isInBounds(grid)) {
                    var nextHeatLoss = state.heatLoss + heatLossAt(nextPoint.point(), grid);
                    queue.offer(new State(nextHeatLoss, nextPoint, state.consecutiveSteps + 1));
                }
            }

            // - or you can proceed in an orthogonal direction if you've gone at least 4 steps in a row
            if (state.consecutiveSteps >= 4 || state.positionAndDirection.direction() == None) {
                var orthogonalDirections = state.positionAndDirection.direction().orthogonals();
                for (var orthogonalDirection : orthogonalDirections) {
                    var nextPoint = state.positionAndDirection.next(orthogonalDirection);
                    if (nextPoint.point().isInBounds(grid)) {
                        var nextHeatLoss = state.heatLoss + heatLossAt(nextPoint.point(), grid);
                        queue.offer(new State(nextHeatLoss, nextPoint, 1));
                    }
                }
            }
        }
        return totalHeatLoss;
    }

    public static int heatLossAt(Point point, Integer[][] grid) {
        return grid[point.y()][point.x()];

    }

    record State(int heatLoss, PointAndDirection positionAndDirection, int consecutiveSteps) implements Comparable<State> {
        @Override
        public int compareTo(State o) {
            return Integer.compare(heatLoss, o.heatLoss);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return consecutiveSteps == state.consecutiveSteps && Objects.equals(positionAndDirection, state.positionAndDirection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(positionAndDirection, consecutiveSteps);
        }
    }

}
