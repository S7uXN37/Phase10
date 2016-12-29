import com.github.s7uxn37.phase10.Intelligence;
import com.github.s7uxn37.phase10.constructs.Card;
import org.junit.Assert;
import org.junit.Test;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IntelligenceTest {
    private CountDownLatch lock = new CountDownLatch(1);
    private boolean success = false;
    @Test
    public void testCauseUpdate() {
        ActionListener actionListener = e -> {
            lock.countDown();
            success = true;
        };
        Intelligence ai = new Intelligence();
        ai.setUpdateListener(actionListener);

        try {
            Assert.assertTrue(lock.await(100, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Assert.assertTrue(false);
        }

        Assert.assertTrue(success);
    }

    @Test
    public void testCountCards() {
        ArrayList<Card> cards = Card.getListUnknown(500);
        Random rand = new Random();
        int shouldBe = cards.size();
        for (int i = 0; i < 10000; i++) {
            System.out.print("Pass: " + (i+1) + "  -  ");
            if (rand.nextDouble() < 0.5f) {
                Card c = new Card();
                c.number = 2 + rand.nextInt(11);
                c.colorIndex = rand.nextInt(5);
                int startIndex = rand.nextInt(cards.size() - 5);
                Assert.assertTrue(Intelligence.probDecrease(cards, "", c, startIndex));
                shouldBe--;
                System.out.print("decreased prob of " + (cards.size()-startIndex) + " cards");
            } else {
                float r = rand.nextFloat();
                if (r < 0.3f) {
                    cards.add(new Card());
                    shouldBe++;
                    System.out.print("added card");
                } else {
                    int num = 1 + rand.nextInt(5);
                    for (int j = 0; j < num; j++) {
                        Card c = new Card();
                        c.number = 2 + rand.nextInt(11);
                        c.colorIndex = rand.nextInt(5);
                        c.prob = 1f/((float) num);
                        cards.add(c);
                    }
                    shouldBe++;
                    System.out.print("added " + num + " cards with decreased prob");
                }
            }
            double actual = Intelligence.countCards(cards);
            System.out.println(", shouldBe: " + shouldBe + ", actual: " + actual);
            Assert.assertEquals(shouldBe, actual, 0.4f);
        }
    }
}
