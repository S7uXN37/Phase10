package com.github.s7uxn37.phase10;

import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Intelligence {
	private ArrayList<Card> faceDown;
	private ArrayList<Card> faceUp;
	private ArrayList<Card>[] opponents;
	private ArrayList<Card> hand;
	
	private ActionListener updateListener;
	
	public void setUpdateListener(ActionListener listener) {
		updateListener = listener;
	}
	
	@SuppressWarnings("unchecked")
	public void init(int numPlayers) {
		this.faceDown = Card.getListUnknown(52 - 10*numPlayers - 5);
		this.faceUp = Card.getListUnknown(5);
		this.opponents = (ArrayList<Card>[]) new ArrayList<?>[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			this.opponents[i] = Card.getListUnknown(10);
		}

		updateListener.actionPerformed(null);
	}
	
	public void setHand(Card[] cards) {
		for (Card c : cards) {
			hand.add(c);
		}
		updateListener.actionPerformed(null);
	}
}

class Card {
	int number = -1;
	char color = (char) -1;
	
	static ArrayList<Card> getListUnknown(int length) {
		ArrayList<Card> list = new ArrayList<>();
		for (int i = 0; i < length; i++)
			list.add(new Card());
		return list;
	}
}