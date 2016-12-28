package com.github.s7uxn37.phase10;

import java.util.ArrayList;

public class Card {
    public enum Color {BLUE, PURPLE, GREEN, RED}

    public int number = -1;
    public int colorIndex = -1;

    public static ArrayList<Card> getListUnknown(int length) {
        ArrayList<Card> list = new ArrayList<>();
        for (int i = 0; i < length; i++)
            list.add(new Card());
        return list;
    }
}