package com.github.s7uxn37.phase10;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class Intelligence {
	public int numPlayers = 2;

    public ArrayList<Card> faceDown;
    public ArrayList<Card> faceUp;
    public ArrayList<Card>[] opponents;
    public ArrayList<Card> player;
    public ArrayList<Card> desired;
	
	private ActionListener updateListener;
	
	public void setUpdateListener(ActionListener listener) {
		updateListener = listener;
		updateListener.actionPerformed(null);
		log(
				"updateListener set\n" +
                "updateListener called\n" +
                "waiting for first input..."
		);
	}
	
	@SuppressWarnings("unchecked")
	public void init(int numPlayers, Card[] playerCards) {
	    this.numPlayers = numPlayers;

		this.faceDown = Card.getListUnknown(52 - 10*numPlayers - 5);
		this.faceUp = Card.getListUnknown(5);
		this.opponents = (ArrayList<Card>[]) new ArrayList<?>[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			this.opponents[i] = Card.getListUnknown(10);
		}
		this.player = new ArrayList<>();
        Collections.addAll(player, playerCards);
        this.desired = Card.getListUnknown(5);

		log(
				"playerNumber set\n" +
				"card pool initialized\n" +
				"hands initialized\n" +
                "player's hand set"
		);
	}

	static void log(String message) {
		System.out.println(message);
	}
}