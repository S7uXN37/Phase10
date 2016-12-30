package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends ModulePanel {
    CardView[] cardViews;

	public PlayerPanel(Intelligence intelligence) {
		super("Opponents' hands", intelligence);

        // Initialize Content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));

        // Initialize CardViews
        cardViews = new CardView[ai.numOpponents];
        for (int i = 0; i < cardViews.length; i++) {
            cardViews[i] = new CardView(CardView.SORTING.PROBABILITY);
        }

        // Initialize TitlePanels with CardViews, add to Content
        for (int i = 0; i < cardViews.length; i++) {
            TitlePanel p = new TitlePanel("Player " + (i+1));
            p.addContent(cardViews[i]);
            content.add(p);
        }

        // Add Content to PlayerPanel
        addContent(content);
	}

	@Override
	public void update() {
        for (int i = 0; i < ai.numOpponents; i++) {
            cardViews[i].setCards(ai.opponents[i]);
        }
	}

}
