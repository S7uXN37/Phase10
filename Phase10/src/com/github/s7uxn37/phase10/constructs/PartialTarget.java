package com.github.s7uxn37.phase10.constructs;

import com.github.s7uxn37.phase10.Optimizer;

import java.util.*;
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
    public ArrayList<Card> desiredCards;
    public int cardsMissing;

    private PartialTarget(Target t, Card[] assigned) {
        target = t;
        cards = assigned;
        desiredCards = new ArrayList<>();

        ArrayList<Card> cards = new ArrayList<>();
        Collections.addAll(cards, assigned);
        cardsMissing = Optimizer.distanceToTarget(cards, t);
    }
    public PartialTarget(Target t, Card[] assigned, ArrayList<Card> desiredCards) {
        this(t, assigned);
        this.desiredCards = desiredCards;
    }

    @Override
    public String toString() {
        return "Target: " + target.toString() + " using: " + desiredCards.toString() + " distance: " + cardsMissing + " desired: " + desiredCards.toString();
    }

    public static PartialTarget getPartialTarget(Target target, ArrayList<Card> assigned) {
        switch (target.type) {
            case SAME_COLOR:
                return setupSameTypePartialTarget(target, assigned, card -> card.colorIndex, integer -> {
                    Card c = new Card();
                    c.colorIndex = integer;
                    return c;
                });
            case SAME_NUMBER:
                return setupSameTypePartialTarget(target, assigned, card -> card.number, integer -> {
                    Card c = new Card();
                    c.number = integer;
                    return c;
                });
            case RUN:
                // Calculate best distance for cards
                int targetDist = Optimizer.distanceToTarget(assigned, target);

                // Set up Map for number counts -> amount of cards with that number
                HashMap<Integer, Integer> numberCounts = Optimizer.countValues(assigned, card -> card.number);
                // Set up Map for number occurrences -> amount of desirable runs that number occurs in
                HashMap<Integer, Integer> numberOccurrences = new HashMap<>();
                for (Integer key : numberCounts.keySet()) {
                    numberOccurrences.put(key, 0);
                }
                // Set up ArrayList for missing numbers -> list of missing numbers for each desirable runStart
                HashSet<Integer> numbersMissing = new HashSet<>();

                // iterate through all possible runs and count desirable runs
                int possibilities = 0;
                for (int runStart = 1; runStart <13; runStart++) {
                    // Save missing numbers into ArrayList
                    ArrayList<Integer> missing = new ArrayList<>();
                    // Calculate distanceToTarget
                    int dist = 0;
                    for (int value = runStart; value < runStart + target.cardCount; value++) {
                        if (!numberCounts.containsKey(value)) {
                            // Add missing card, increase distance
                            missing.add(value);
                            dist++;
                        }
                    }

                    // If distance desirable, add occurrences to sum
                    if (dist == targetDist) {
                        for (int value = runStart; value < runStart + target.cardCount; value++) {
                            // Calculate numbers of occurrences for each number
                            int prev = numberOccurrences.getOrDefault(value, 0);
                            numberOccurrences.put(value, prev + 1);
                        }
                        possibilities++;

                        // Add missing numbers into numbersMissing
                        numbersMissing.addAll(missing);
                    }
                }

                ArrayList<Card> cards = new ArrayList<>();
                for (Card c : assigned) {
                    // Set total probabilities for each number = occurrences/possibilities
                    double totNumberProb = (double)numberOccurrences.get(c.number) / (double)possibilities;

                    // Set probabilities for each card = totProbOfNumber/amountOfCardsWithNumber
                    Card card = new Card(c);
                    card.prob = Math.min(1d, totNumberProb / (double)numberCounts.get(c.number));

                    cards.add(card);
                }
                ArrayList<Card> missingCards = new ArrayList<>();
                for (Integer missingNumber : numbersMissing) {
                    // Set total probabilities for each number = occurrences/possibilities
                    double totNumberProb = (double)numberOccurrences.get(missingNumber) / (double)possibilities;

                    // Create corresponding card, add to ArrayList
                    Card card = new Card();
                    card.number = missingNumber;
                    card.prob = Math.min(1d, totNumberProb);

                    missingCards.add(card);
                }

                // Remove cards with probability 0
                cards.removeIf(Card::probIsZero);
                missingCards.removeIf(Card::probIsZero);

                PartialTarget partialTarget = new PartialTarget(target, cards.toArray(new Card[0]));
                partialTarget.desiredCards = missingCards;
                return partialTarget;
        }
        return new PartialTarget(target, assigned.toArray(new Card[0]));
    }

    private static PartialTarget setupSameTypePartialTarget(Target target, ArrayList<Card> assigned, Function<Card, Integer> keyGetter, Function<Integer, Card> cardSupplier) {
        HashMap<Integer, Integer> typeCounts = Optimizer.countValues(assigned, keyGetter);
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
            cards[i].prob = Math.min(1d, importance);
        }

        PartialTarget partialTarget = new PartialTarget(target, cards);
        for (Map.Entry<Integer, Integer> e : typeCounts.entrySet()) {
            if (e.getValue() == finalMaxCount) {
                for (int i = 0; i < target.cardCount - finalMaxCount; i++)
                    partialTarget.desiredCards.add(cardSupplier.apply(e.getKey()));
            }
        }

        return partialTarget;
    }
}
