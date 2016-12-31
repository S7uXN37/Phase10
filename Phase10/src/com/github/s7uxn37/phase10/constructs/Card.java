package com.github.s7uxn37.phase10.constructs;

import java.util.ArrayList;

public class Card {
    public enum Color {YELLOW, PURPLE, GREEN, RED}

    public int number = -1;
    public int colorIndex = -1;
    public double prob = 1f;

    public Card() {
        number = -1;
        colorIndex = -1;
    }
    public Card(String s) {
        if (s.length() < 1) {
            number = -1;
            colorIndex = -1;
            return;
        }

        s = s.replaceAll(" ", "").toUpperCase();
        if (s.equals("?")) {
            colorIndex = -1;
            number = -1;
            return;
        }
        number = Integer.parseInt("" + s.substring(0, s.length() - 1));

        switch (s.charAt(s.length() - 1)) { // YELLOW, PURPLE, GREEN, RED
            case 'Y':
                colorIndex = 0;
                break;
            case 'P':
            case 'V':
                colorIndex = 1;
                break;
            case 'G':
                colorIndex = 2;
                break;
            case 'R':
                colorIndex = 3;
                break;
            default:
                colorIndex = -1;
                throw new IllegalArgumentException("Color not recognized");
        }
    }
    public Card(Card c) {
        number = c.number;
        colorIndex = c.colorIndex;
        prob = c.prob;
    }

    public static ArrayList<Card> getListUnknown(int length) {
        ArrayList<Card> list = new ArrayList<>();
        for (int i = 0; i < length; i++)
            list.add(new Card());
        return list;
    }

    public boolean isUnknown() {
        return number == -1 && colorIndex == -1;
    }

    public boolean probIsZero() {
        return probIsValue(0d);
    }
    public boolean probIsOne() {
        return probIsValue(1d);
    }
    public boolean probIsValue(double value) {
        Card card = new Card();
        card.number = number;
        card.colorIndex = colorIndex;
        card.prob = value;

        return card.equals(this);
    }

    public static Card[] parseCards(String text) {
        if (text.length() == 0)
            return new Card[0];

        String[] cardStrings = text.split(",");

        Card[] cards = new Card[cardStrings.length];
        for (int i = 0; i < cardStrings.length; i++) {
            cards[i] = new Card(cardStrings[i]);
        }

        return cards;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            Card c = (Card) obj;
            return number == c.number && colorIndex == c.colorIndex && hashCode() == c.hashCode();
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        String s = number + ";" + colorIndex + ";" + Math.round(prob*100);
        return s.hashCode();
    }

    @Override
    public String toString() {
        return (number == -1 ? "unknown number" : number) + " of " + (colorIndex == -1 ? "unknown color" : Color.values()[colorIndex].toString()) + " @ p=" + prob;
    }
}