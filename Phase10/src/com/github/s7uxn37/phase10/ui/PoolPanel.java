package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

import javax.swing.*;
import java.awt.*;

public class PoolPanel extends ModulePanel {
	CardView viewFaceUp, viewFaceDown;

	public PoolPanel(Intelligence intelligence) {
		super("Card pool", intelligence);

		// Initialize Content
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(0, 1));

		// Initialize CardViews
		viewFaceUp = new CardView();
		viewFaceDown = new CardView();

		// Initialize TitlePanels with CardViews, add to Content
		TitlePanel p1 = new TitlePanel("Face up");
		p1.addContent(viewFaceUp);
		content.add(p1);
		TitlePanel p2 = new TitlePanel("Face down");
		p2.addContent(viewFaceDown);
		content.add(p2);

		// Add Content to PlayerPanel
		addContent(content);
	}

	@Override
	public void update() {
		viewFaceDown.setCards(ai.faceDown);
		viewFaceUp.setCards(ai.faceUp);

//        invalidate();
//        repaint();
	}

}
