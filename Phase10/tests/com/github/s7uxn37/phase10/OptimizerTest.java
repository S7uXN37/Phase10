package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.constructs.Target;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class OptimizerTest {
    @Test
    public void toDigitArr() throws Exception {
        for (int radix = 0; radix < 11; radix++) {
            int arrLength = 5;
            for (int number = 0; number < Math.pow(radix, arrLength); number++) {
                int[] actual = Optimizer.toDigitArr(number, radix, arrLength);

                String s = Integer.toString(number, radix);
                while (s.length() < arrLength)
                    s = "0" + s;
                int[] expected = new int[arrLength];
                for (int i = 0; i < arrLength; i++) {
                    expected[i] = Integer.parseInt("" + s.charAt(i));
                }

//                System.out.println("Asserting output for i=" + number + " in base=" + radix + "...");
                Assert.assertArrayEquals(expected, actual);
            }
        }
    }

    @Test
    public void distanceToTarget() throws Exception {
        String[] inCards = new String[]{"1Y,2R,4G,3P", "1Y,3R,2G,4P", "4Y,2R,1G,3P", "2Y,4R,1G,3P", "2Y,1Y,4G,3Y", "2Y,2R,4G,4P,2G"};
        Target[] inTargets = new Target[]{
                new Target(Intelligence.TARGET_TYPE.RUN, 4),
                new Target(Intelligence.TARGET_TYPE.RUN, 5),
                new Target(Intelligence.TARGET_TYPE.RUN, 6),
                new Target(Intelligence.TARGET_TYPE.RUN, 3),
                new Target(Intelligence.TARGET_TYPE.SAME_COLOR, 4),
                new Target(Intelligence.TARGET_TYPE.SAME_NUMBER, 4)
        };
        int[] outputs = new int[]{0, 1, 2, 0, 1, 1};

        for (int i = 0; i < inCards.length && i < outputs.length && i < inTargets.length; i++) {
            ArrayList<Card> in = new ArrayList<>();
            Collections.addAll(in, Card.parseCards(inCards[i]));

//            System.out.println("Pass " + i + " - Cards:" + inCards[i] + " ; Target:" + inTargets[i] + " ; Expected:" + outputs[i]);
            int actual = Optimizer.distanceToTarget(in, inTargets[i]);

            Assert.assertEquals(outputs[i], actual);
        }
    }

    @Test
    public void partition() throws Exception {
        String[] inCards = new String[]{
                "2R,5R,1Y,7G,12P,8P",
                "1Y,2R,3Y,4R,5R,6R,6Y,7G,9Y,10Y",
                "1Y, 3Y, 6R, 6Y, 7P, 7G",
                "7Y,8P,3Y,4R,5R,1R,10R,11R"
        };
        Target[][] inTargets = new Target[][]{
                new Target[]{
                        new Target(Intelligence.TARGET_TYPE.RUN, 3),
                        new Target(Intelligence.TARGET_TYPE.RUN, 4)
                },
                new Target[]{
                        new Target(Intelligence.TARGET_TYPE.RUN, 3),
                        new Target(Intelligence.TARGET_TYPE.SAME_COLOR, 5)
                },
                new Target[]{
                        new Target(Intelligence.TARGET_TYPE.SAME_NUMBER, 3),
                        new Target(Intelligence.TARGET_TYPE.SAME_COLOR, 4)
                },
                new Target[]{
                        new Target(Intelligence.TARGET_TYPE.RUN, 3),
                        new Target(Intelligence.TARGET_TYPE.RUN, 2),
                        new Target(Intelligence.TARGET_TYPE.SAME_COLOR, 3)
                }
        };
        int[][] outputs = new int[][]{
                new int[]{0, 1, 0, 1, -1, 1},
                new int[]{1, -1, 1, 0, 0, 0, 1, -1, 1, 1},
                new int[]{1, 1, -1, 1, 0, 0},
                new int[]{1, 1, 0, 0, 0, 2, 2, 2}
        };

        for (int i = 0; i < inCards.length && i < outputs.length && i < inTargets.length; i++) {
            Card[] cards = Card.parseCards(inCards[i]);
            int[] actual = Optimizer.partition(cards, inTargets[i], false);

            double poss = Math.pow(inTargets[i].length, cards.length);
            System.out.print("Pass " + i + "; " + poss + " possibilities - Cards:");
            System.out.print(inCards[i]);
            System.out.print(" ; Target:");
            System.out.print(Arrays.toString(inTargets[i]));
            System.out.print(" ; Expected:");
            System.out.print(Arrays.toString(outputs[i]));
            System.out.print(" ; Actual:");
            System.out.println(Arrays.toString(actual));

            for (int j = 0; j < actual.length && j < outputs[i].length; j++) {
                if (outputs[i][j] != -1)
                    Assert.assertEquals(outputs[i][j], actual[j]);
            }
        }
    }

    @Test
    public void partitionStress() {
        int[] cardCounts = new int[]{5,7,9,10,13,16};
        int[] targetCounts = new int[]{2,3};
        Card[] cards = Card.parseCards("2Y,11R,11P,5G,6R,7Y,8P,9G,10G,11R,2Y,11R,2R,5Y,6G,8R");
        Target[] targets = new Target[]{
                new Target(Intelligence.TARGET_TYPE.RUN, 3),
                new Target(Intelligence.TARGET_TYPE.SAME_COLOR, 2),
                new Target(Intelligence.TARGET_TYPE.SAME_NUMBER, 5)
        };

        int pass = 0;
        for (int cardCount : cardCounts) {
            for (int targetCount : targetCounts) {
                Card[] c = new Card[cardCount];
                System.arraycopy(cards, 0, c, 0, c.length);

                Target[] t = new Target[targetCount];
                System.arraycopy(targets, 0, t, 0, t.length);

                long start = System.currentTimeMillis();
                int[] partition = Optimizer.partition(c, t);
                long end = System.currentTimeMillis();
                System.out.println("Stress test #" + (++pass) + " - config:("+cardCount+","+targetCount+") time: " + (end-start) + "ms; result:" + Arrays.toString(partition) + "\n");
            }
        }
    }
}