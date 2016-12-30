package com.github.s7uxn37.phase10.constructs;

import com.github.s7uxn37.phase10.MultiOptimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Saves Card[] 'cards' that should be used to achieve Target 'target' to minimize missing cards
 */
public class PartialTarget {
    public Target target;
    /**
     * Cards included in this partialTarget with Card.prob set to the percentage of cases in which the card is necessary
     */
    public Card[] cards;

    private PartialTarget(Target t, Card[] assigned) {
        target = t;
        cards = assigned;
    }

    public static PartialTarget getDesire(Target target, ArrayList<Card> assigned) {
        switch (target.type) {
            case SAME_COLOR:
                return setupSameTypeDesire(target, assigned, card -> card.colorIndex);
            case SAME_NUMBER:
                return setupSameTypeDesire(target, assigned, card -> card.number);
            case RUN:
                // Calculate best distance for cards
                int targetDist = MultiOptimizer.distanceToTarget(assigned, target);

                // Set up Map for number counts -> amount of cards with that number
                HashMap<Integer, Integer> numberCounts = MultiOptimizer.countValues(assigned, card -> card.number);
                // Set up Map for number occurrences -> amount of desirable runs that number occurs in
                HashMap<Integer, Integer> numberOccurrences = new HashMap<>();
                for (Integer key : numberCounts.keySet()) {
                    numberOccurrences.put(key, 0);
                }

                // iterate through all possible runs and count desirable runs
                int possibilities = 0;
                for (int runStart = 1; runStart <13; runStart++) {
                    // Calculate distanceToTarget
                    int dist = 0;
                    for (int value = runStart; value < runStart + target.cardCount; value++) {
                        if (!numberCounts.containsKey(value))
                            dist++;
                    }

                    // If distance desirable, add occurrences to sum
                    if (dist == targetDist) {
                        for (int value = runStart; value < runStart + target.cardCount; value++) {
                            // calculate numbers of occurrences for each number
                            int prev = numberOccurrences.getOrDefault(value, 0);
                            numberOccurrences.put(value, prev + 1);
                        }
                        possibilities++;
                    }
                }

                ArrayList<Card> cards = new ArrayList<>();
                for (Card c : assigned) {
                    // set total probabilities for each number = occurrences/possibilities
                    double totNumberProb = (double)numberOccurrences.get(c.number) / (double)possibilities;

                    // set probabilities for each card = totProbOfNumber/amountOfCardsWithNumber
                    Card card = new Card(c);
                    card.prob = totNumberProb / (double)numberCounts.get(c.number);

                    cards.add(card);
                }

                // remove cards with probability 0
                cards.removeIf(Card::probIsZero);

                return new PartialTarget(target, cards.toArray(new Card[0]));
        }
        return new PartialTarget(target, assigned.toArray(new Card[0]));
    }

    private static PartialTarget setupSameTypeDesire(Target target, ArrayList<Card> assigned, Function<Card, Integer> keyGetter) {
        HashMap<Integer, Integer> typeCounts = MultiOptimizer.countValues(assigned, keyGetter);
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> e : typeCounts.entrySet()) {
            if (e.getValue() > maxCount)
                maxCount = e.getValue();
        }

        // Remove cards that don't have the right color
        int finalMaxCount = maxCount;
        assigned.removeIf(card -> typeCounts.get(keyGetter.apply(card)) < finalMaxCount);

        // Set probabilities of remaining cards (needed/available) and copy into PartialTarget
        double importance = (double)target.cardCount / (double)finalMaxCount;

        Card[] cards = new Card[assigned.size()];
        for (int i = 0; i < cards.length; i++) {
            Card c = assigned.get(i);

            cards[i] = new Card(c);
            cards[i].prob = importance;
        }

        return new PartialTarget(target, cards);
    }
}
