package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.constructs.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CardView extends JPanel {

    public CardView() {
        setBackground(Color.GRAY);
        setLayout(new FlowLayout());
        setVisible(true);
    }

    public void setCards(Card[] cards) {
        removeAll();

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

    public void setCards(ArrayList<Card> cards) {
        Card[] c = new Card[cards.size()];
        for (int i = 0; i < c.length; i++)
            c[i] = cards.get(i);
        setCards(c);
    }
}

class SingleCardView extends JPanel {
    Card card;
    JLabel label;

    public SingleCardView(Card c) {
        setSize(50,100);

        card = c;

        label = new JLabel();
        label.setFont(label.getFont().deriveFont(80f));
        add(label);

        update();
    }

    void update() {
        Color c = Color.WHITE;
        if (card.colorIndex != -1) {
            switch (Card.Color.values()[card.colorIndex]) {
                case BLUE:
                    c = Color.BLUE;
                    break;
                case GREEN:
                    c = Color.GREEN;
                    break;
                case PURPLE:
                    c = Color.MAGENTA;
                    break;
                case RED:
                    c = Color.RED;
                    break;
            }
        }

        setBackground(c);
        label.setText(card.number == -1 ? "?" : ("" + card.number));

        invalidate();
        repaint();
    }
}
