package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.constructs.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class CardView extends JPanel {
    public enum SORTING {NONE, NUMBER, COLOR, PROBABILITY}

    public static final java.awt.Color PURPLE = new java.awt.Color(128, 40, 128);
    private int sortingIndex;

    CardView(SORTING sorting) {
        this();
        ArrayList<SORTING> list = new ArrayList<>();
        Collections.addAll(list, SORTING.values());
        sortingIndex = list.indexOf(sorting);
    }
    CardView() {
        setBackground(Color.GRAY);
        setLayout(new FlowLayout());
        setVisible(true);
        sortingIndex = 0;
    }

    public final void setCards(Card[] cards) {
        removeAll();

        Arrays.sort(cards, (o1, o2) -> {
            int result;
            int alg = sortingIndex;
            do {
                switch (SORTING.values()[alg]) {
                    case COLOR: // doesn't matter
                        result = Integer.compare(o1.colorIndex, o2.colorIndex);
                        break;
                    case NUMBER: // ascending
                        result = Integer.compare(o1.number, o2.number);
                        break;
                    case PROBABILITY: // descending
                        result = Double.compare(o2.prob, o1.prob);
                        break;
                    default:
                        result = 0;
                        break;
                }
                alg++;
                alg = alg % SORTING.values().length;
            } while (sortingIndex != 0 && result == 0 && alg != sortingIndex);
            return result;
        });

        int i = 0;
        while (i < cards.length) {
            Card c = cards[i];
            SingleCardView v = new SingleCardView(c);

            if (c.isUnknown()) {
                int length = 1;
                for (int j = i+1; j < cards.length; j++) {
                    if (cards[j].isUnknown())
                        length++;
                    else
                        break;
                }

                if (length > 15) {
                    Card replacement = new Card();
                    replacement.number = length;

                    v = new SingleCardView(replacement);
                    add(v);

                    i += length;
                    continue;
                }
            }

            add(v);
            i++;
        }
    }

    public final void setCards(ArrayList<Card> cards) {
        Card[] c = new Card[cards.size()];
        for (int i = 0; i < c.length; i++)
            c[i] = cards.get(i);
        setCards(c);
    }
}

final class SingleCardView extends JPanel {
    private final Card card;
    private final JLabel label;

    SingleCardView(Card c) {
        card = c;

        setSize(50,100);

        label = new Label();
        label.setFont(label.getFont().deriveFont(40f));
        label.setForeground(Color.BLACK);
        add(label);

        setBackground(Color.LIGHT_GRAY);

        update();
    }

    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color c = Color.WHITE;
        if (card.colorIndex != -1) {
            switch (Card.Color.values()[card.colorIndex]) {
                case YELLOW:
                    c = Color.YELLOW;
                    break;
                case GREEN:
                    c = Color.GREEN;
                    break;
                case PURPLE:
                    c = CardView.PURPLE;
                    break;
                case RED:
                    c = Color.RED;
                    break;
            }
        }
        g.setColor(c);
        int height = (int) (getHeight() * card.prob);
        g.fillRect(0, getHeight() - height , getWidth(), height);
    }

    private void update() {
        label.setText(card.number == -1 ? "?" : ("" + card.number));
    }
}
