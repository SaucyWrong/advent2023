package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Day8 {

    static Pattern nodePattern = Pattern.compile("^(?<self>\\w{3}) = \\((?<left>\\w{3}), (?<right>\\w{3})\\)$");
    static Map<String, Node> graph = new HashMap<>();

    public static void main(String[] args) {
        var input = Utils.readInputAsString("day8-puzzle-input").lines().toList();
        var navigationSet = input.getFirst();
        System.out.printf("Navigation set: %s\n", navigationSet);

        input.stream().skip(2).forEach(Day8::addNodes);

        var currentNode = graph.get("AAA");
        var targetNode = graph.get("ZZZ");
        System.out.printf("--- Starting node: %s, Target node: %s ---\n", currentNode, targetNode);

        var steps = 0;
        while (currentNode != targetNode) {
            for (var i = 0; i < navigationSet.length(); i++) {
                var nav = navigationSet.charAt(i);
                currentNode = currentNode.next(nav);
//                System.out.printf("Navigating '%s' to %s\n", nav, currentNode);
                steps++;

                if (currentNode.isTerminal() && currentNode != targetNode) {
                    throw new RuntimeException("reached an unexpected dead end");
                }
            }
        }

        System.out.printf("--- Reached target node '%s' in %d steps ---\n", targetNode, steps);
    }

    static void addNodes(String inputString) {
        nodePattern.matcher(inputString).results().forEach(matchResult -> {
            var self = matchResult.group("self");
            var left = matchResult.group("left");
            var right = matchResult.group("right");

            var newNode = getOrCreateNode(self);
            newNode.left = getOrCreateNode(left);
            newNode.right = getOrCreateNode(right);

            System.out.printf("Added node: %s, total graph size:%d\n", newNode, graph.size());
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
            return (left == null && right == null) ||
                    (left == this && right == this);
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
