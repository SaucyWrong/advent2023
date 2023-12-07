package com.quinnheavyindustries.advent2023;

import com.quinnheavyindustries.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Day7 {
    public static void main(String[] args) {
        var lines = Utils.loadLines("day7-puzzle-input");
        SortedSet<CamelHand> hands = lines.stream()
                .map(CamelHand::new)
                .collect(Collectors.toCollection(TreeSet::new));

        if (hands.size() != lines.size()) {
            throw new IllegalStateException("Expected " + lines.size() + " hands, but got " + hands.size() + " hands.");
        }

        System.out.println("\nRanked Hands:");
        hands.forEach(System.out::println);

        System.out.println("\nLowest hand: " + hands.first());
        System.out.println("Highest hand: " + hands.last());

        var handList = new ArrayList<>(hands);
        var totalWinnings = 0L;
        for (int i = 1; i <= handList.size(); i++) {
            var hand = handList.get(i - 1);
            var winnings = hand.bid * i;
            totalWinnings += winnings;
            System.out.printf("%s: %d * %d = %d --> tot: %d\n", hand.handString, hand.bid, i, winnings, totalWinnings);
        }
        System.out.println("\nTotal Winnings: " + totalWinnings);
    }

    public static class CamelHand implements Comparable<CamelHand> {
        private final String handString;
        private final String encodedHand;
        private final Map<Integer, Integer> countedCards = new HashMap<>();
        private final List<Integer> cardValuesInOrder = new ArrayList<>();
        private final long bid;

        public CamelHand(String hand) {
            System.out.println(hand);
            var tokens = hand.split("\\W+");
            this.handString = tokens[0];
            this.bid = Long.parseLong(tokens[1]);

            for (int i = 0; i < handString.length(); i++) {
                char card = handString.charAt(i);
                cardValuesInOrder.add(cardValue(card));
                countedCards.compute(cardValue(card), (k, v) -> v == null ? 1 : v + 1);
            }

            applyJokers();

            this.encodedHand = countedCards.values().stream()
                    .filter(v -> v > 0)
                    .sorted(Comparator.reverseOrder())
                    .map(String::valueOf)
                    .collect(Collectors.joining());
            System.out.println(this);
        }

        public void applyJokers() {
            if (!countedCards.containsKey(cardValue('J'))) { // there are no jokers in the hand
                return;
            }

            var jokerCount = countedCards.get(cardValue('J'));
            if (jokerCount == 5) { // nothing can be done with this hand
                return;
            }
            countedCards.remove(cardValue('J'));

            var highestCardCount = countedCards.values().stream().max(Integer::compareTo).orElseThrow();
            var targetCardValue = countedCards.entrySet().stream()
                    .filter(e -> Objects.equals(e.getValue(), highestCardCount))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();

            countedCards.compute(targetCardValue, (k, v) -> v + jokerCount);
        }

        public int handStrength() {
            return switch (encodedHand) {
                case "5" -> 6; // five of a kind
                case "41" -> 5; // four of a kind
                case "32" -> 4; // full house
                case "311" -> 3; // three of a kind
                case "221" -> 2; // two pair
                case "2111" -> 1; // one pair
                case "11111" -> 0; // high card
                default -> throw new IllegalStateException("Unexpected value: " + encodedHand);
            };
        }

        private static int cardValue(char card) {
            return switch (card) {
                case 'A' -> 14;
                case 'K' -> 13;
                case 'Q' -> 12;
                case 'J' -> -1; // joker
                case 'T' -> 10;
                default -> card - '0';
            };
        }

        private int compareByOrderedCards(CamelHand o) {
            for (int i = 0; i < cardValuesInOrder.size(); i++) {
                int result = Integer.compare(cardValuesInOrder.get(i), o.cardValuesInOrder.get(i));
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }

        @Override
        public int compareTo(CamelHand o) {
            int result = Integer.compare(handStrength(), o.handStrength());
            if (result == 0) {
                result = compareByOrderedCards(o);
            }
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CamelHand{");
            sb.append("handString='").append(handString).append('\'');
            sb.append(", encodedHand='").append(encodedHand).append('\'');
            sb.append(", countedCards=").append(countedCards);
            sb.append(", cardValuesInOrder=").append(cardValuesInOrder);
            sb.append(", bid=").append(bid);
            sb.append(", handStrength=").append(handStrength());
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CamelHand camelHand = (CamelHand) o;
            return Objects.equals(handString, camelHand.handString) && Objects.equals(countedCards, camelHand.countedCards) && Objects.equals(cardValuesInOrder, camelHand.cardValuesInOrder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(handString, countedCards, cardValuesInOrder);
        }
    }
}
