package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {

    static Pattern nodePattern = Pattern.compile("^(?<self>\\w{3}) = \\((?<left>\\w{3}), (?<right>\\w{3})\\)$");
    static Map<String, Node> graph = new HashMap<>();

    public static void main(String[] args) {
        var input = Utils.readInputAsString("day8-puzzle-input").lines().toList();
        var navigationSet = input.getFirst();
        System.out.printf("Navigation set: %d instructions\n", navigationSet.length());

        input.stream().skip(2).forEach(Day8::addNodes);
        System.out.printf("Created %d nodes\n", graph.size());

        var currentNodes = graph.values().stream()
                .filter(k -> k.label.endsWith("A"))
                .collect(Collectors.toList());
        var potentialTerminals = graph.values().stream()
                .filter(Node::isTerminal)
                .collect(Collectors.toSet());
        System.out.printf("--- Found %d starting nodes that end with 'A' (%s)---\n", currentNodes.size(), currentNodes);
        System.out.printf("--- Found %d potential terminal nodes (%s)---\n", potentialTerminals.size(), potentialTerminals);

        System.out.println("--- Exploring graph one traveler at a time ---");
        var cycles = new HashMap<String, Long>();

        currentNodes.forEach(node -> {
            System.out.println("Exploring using " + node);
            String startLabel = node.label;
            long steps = 0;
            boolean cycleFound = false;
            while (steps <= 1_000_000 && !cycleFound) {
                for (var i = 0; i < navigationSet.length(); i++) {
                    var nav = navigationSet.charAt(i);
                    node = node.next(nav);
                    steps++;
                    if (node.isTerminal()) {
                        cycleFound = true;
                        System.out.printf("node %s reached terminal node %s in %d steps\n", startLabel, node, steps);
                        cycles.put(startLabel + "-->" + node.label, steps);
                    }
                }
            }
        });
        System.out.printf("--- Finished exploring graph...found cycle lengths %s ---", cycles.values());
        // at this point I gave up and just punch lcm(...) into wolfram alpha
    }

    static void addNodes(String inputString) {
        nodePattern.matcher(inputString).results().forEach(matchResult -> {
            var self = matchResult.group("self");
            var left = matchResult.group("left");
            var right = matchResult.group("right");

            var newNode = getOrCreateNode(self);
            newNode.left = getOrCreateNode(left);
            newNode.right = getOrCreateNode(right);
        });
    }

    static Node getOrCreateNode(String label) {
        return graph.compute(label, (k, v) -> v == null ? new Node(label) : v);
    }

    static class Node {
        String label;
        Node left;
        Node right;

        Node(String label, Node left, Node right) {
            this.label = label;
            this.left = left;
            this.right = right;
        }

        Node(String label) {
            this(label, null, null);
        }

        Node next(char nav) {
            return switch (nav) {
                case 'L' -> left;
                case 'R' -> right;
                default -> throw new IllegalArgumentException("Invalid navigation: " + nav);
            };
        }

        boolean isTerminal() {
            return label.endsWith("Z");
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
