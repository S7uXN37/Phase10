package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

public final class HandPanel extends ModulePanel {
    private final CardView handCards;

	public HandPanel(Intelligence intelligence) {
		super("Your player", intelligence);

		// Initialize CardView, add to HandPanel
		handCards = new CardView(CardView.SORTING.NUMBER);
//		handCards.setLayout(new GridLayout(0,8, 5, 5));
		addContent(handCards);
	}

	@Override
	public void update() {
		handCards.setCards(ai.player);
	}

}
