package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

public class DesiredPanel extends ModulePanel {
	CardView desiredCards;

	public DesiredPanel(Intelligence intelligence) {
		super("Desired cards", intelligence);

		// Initialize CardView, add to HandPanel
		desiredCards = new CardView();
		addContent(desiredCards);
	}

	@Override
	public void update() {
		desiredCards.setCards(ai.desired);
	}

}
