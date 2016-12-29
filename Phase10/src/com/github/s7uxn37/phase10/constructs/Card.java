package com.github.s7uxn37.phase10.constructs;

import java.util.ArrayList;

public class Card {
    public enum Color {BLUE, PURPLE, GREEN, RED}

    public int number = -1;
    public int colorIndex = -1;
    public double prob = 1f;

    public Card() {
        number = -1;
        colorIndex = -1;
    }
    public Card(String s) {
        s = s.replaceAll(" ", "").toUpperCase();

        switch (s.charAt(s.length() - 1)) { // BLUE, PURPLE, GREEN, RED
            case 'B':
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
            case '?':
                colorIndex = -1;
                number = -1;
                return;
            default:
                colorIndex = 0;
                break;
        }
        number = Integer.parseInt("" + s.substring(0, s.length() - 1));
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

    public static Card[] parseCards(String text) {
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
            return number == c.number && colorIndex == c.colorIndex && (prob - c.prob) < 0.01f;
        } else {
            return super.equals(obj);
        }
    }
}