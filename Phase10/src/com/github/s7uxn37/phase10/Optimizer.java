package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.constructs.Target;

import java.util.*;
import java.util.function.Function;

public final class Optimizer {
    private static final int PARTITION_TIMEOUT = 10;
    private static final int PARTITION_TIMEOUT_DEEP = 60;

    private static boolean deepScanEnabled = false;

    /**
     * @param cards Cards to partition
     * @param targets Targets to fulfill
     * @return The partition: cards[i] belongs to targets[int[i]]
     */
    static int[] partition(ArrayList<Card> cards, ArrayList<Target> targets) {
        return partition(cards, targets, true);
    }
    /**
     * @param cards Cards to partition
     * @param targets Targets to fulfill
     * @param verbose Log status?
     * @return The partition: cards[i] belongs to targets[int[i]]
     */
    @SuppressWarnings("SameParameterValue")
    private static int[] partition(ArrayList<Card> cards, ArrayList<Target> targets, boolean verbose) {
        return partition(cards.toArray(new Card[0]), targets.toArray(new Target[0]), verbose);
    }
    /**
     * @param cards Cards to partition
     * @param targets Targets to fulfill
     * @return The partition: cards[i] belongs to targets[int[i]]
     */
    static int[] partition(Card[] cards, Target[] targets) {
        return partition(cards, targets, true);
    }
    /**
     * @param cards Cards to partition
     * @param targets Targets to fulfill
     * @param verbose Log status?
     * @return The partition: cards[i] belongs to targets[int[i]]
     */
    static int[] partition(Card[] cards, Target[] targets, boolean verbose) {
        int minTotDist = Integer.MAX_VALUE;
        int[] bestPartition = new int[cards.length];

        long endMillis = System.currentTimeMillis() + 1000 * (deepScanEnabled ? PARTITION_TIMEOUT_DEEP : PARTITION_TIMEOUT);
        double possibilities = Math.pow(targets.length, cards.length);

        for (int partition = 0; partition < possibilities; partition++) {
            // Allocation is defined by the representation of "partition" in base(targets.length)
            int[] allocations = toDigitArr(partition, targets.length, cards.length);

            // Split cards into ArrayLists
            ArrayList<Card>[] split = split(allocations, cards, targets.length);

            // Evaluate distance to each target, calculate sum
            int totDist = 0;
            for (int i = 0; i < targets.length; i++) {
                int dist = distanceToTarget(split[i], targets[i]);
                totDist += dist;
            }

            if (totDist < minTotDist) {
                minTotDist = totDist;
                bestPartition = allocations;
            }

            if (minTotDist == 0) {
                if(verbose) Intelligence.log(
                        "Partitioning completed on pass " + partition + "/" + (int)possibilities + " (" + (int)(100*partition/possibilities) + "%) - a perfect partitioning has been found"
                );
                break;
            }
            if (System.currentTimeMillis() >= endMillis) {
                if(verbose) Intelligence.log(
                        "Partitioning aborted on pass " + partition + "/" + (int)possibilities + " (" + (int)(100*partition/possibilities) + "%) - the timeout limit has been surpassed"
                );
                break;
            }
            if (partition == Math.round(possibilities) - 1) {
                if(verbose) Intelligence.log(
                        "Partitioning completed on pass " + (int)possibilities + "/" + (int)possibilities + " (100%) - all possibilities have been exhausted"
                );
            }
        }

        return bestPartition;
    }

    static ArrayList<Card>[] split(int[] partitioning, Card[] cards, int partitionCount) {
        @SuppressWarnings("unchecked")
        ArrayList<Card>[] split = (ArrayList<Card>[]) new ArrayList[partitionCount];
        if (partitionCount == 0){
            return split;
        }

        for (int i = 0; i < split.length; i++) {
            split[i] = new ArrayList<>();
        }
        for (int i = 0; i < cards.length; i++) {
            split[partitioning[i]].add(cards[i]);
        }
        return split;
    }

    static int[] toDigitArr(int i, int radix, int arrLength) {
        int[] arr = new int[arrLength];
        int charPos = arrLength - 1;
        i *= -1;

        while(i <= -radix) {
            arr[charPos--] = -(i % radix);
            i = i / radix;
        }

        arr[charPos] = -i;
        return arr;
    }

    /**
     * @param cards partitioned cards
     * @param target assigned target
     * @return the minimum number of missing cards to achieve the target
     */
    public static int distanceToTarget(ArrayList<Card> cards, Target target) {
        switch(target.type) {
            case RUN:
                HashSet<Integer> numbers = new HashSet<>();
                for (Card c : cards) {
                    if (!numbers.contains(c.number))
                        numbers.add(c.number);
                }

                int minDist = target.cardCount; // worst case: draw all cards
                for (int i = 0; i < cards.size() && minDist > 0; i++) { // card[i] is start of run
                    int dist = 0;
                    int runStart = cards.get(i).number;
                    for (int value = runStart + 1; value < runStart + target.cardCount; value++) {
                        if (!numbers.contains(value))
                            dist++;
                    }
                    if (dist < minDist)
                        minDist = dist;
                }

                return minDist;
            case SAME_COLOR: {
                HashMap<Integer, Integer> colorCounts = countValues(cards, card -> card.colorIndex);

                int maxCount = 0;
                for (Map.Entry<Integer, Integer> e : colorCounts.entrySet()) {
                    int count = e.getValue();
                    if (count > maxCount)
                        maxCount = count;
                }

                return Math.max(0, target.cardCount - maxCount);
            }
            case SAME_NUMBER:
                HashMap<Integer, Integer> numberCounts = new HashMap<>();
                for (Card c : cards) {
                    int prev = numberCounts.getOrDefault(c.number, 0);
                    numberCounts.put(c.number, prev + 1);
                }

                int maxCount = 0;
                for (Map.Entry<Integer, Integer> e : numberCounts.entrySet()) {
                    int count = e.getValue();
                    if (count > maxCount)
                        maxCount = count;
                }

                return Math.max(0, target.cardCount - maxCount);
        }

        return target.cardCount;
    }

    public static HashMap<Integer,Integer> countValues(ArrayList<Card> cards, Function<Card, Integer> keyGetter) {
        HashMap<Integer, Integer> valueCounts = new HashMap<>();
        for (Card c : cards) {
            int key = keyGetter.apply(c);
            int prev = valueCounts.getOrDefault(key, 0);
            valueCounts.put(key, prev + 1);
        }
        return valueCounts;
    }

    public static void setDeepScanEnabled(boolean deepScanEnabled) {
        Optimizer.deepScanEnabled = deepScanEnabled;
        Intelligence.log(
                "Deep scan enabled set to '%1$b'", deepScanEnabled
        );
    }
}
