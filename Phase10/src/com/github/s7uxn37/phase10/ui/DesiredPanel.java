package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DesiredPanel extends ModulePanel {
	CardView desiredCards;
	FieldScoreView fieldScores;

	public DesiredPanel(Intelligence intelligence) {
		super("Desired cards", intelligence);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        desiredCards = new CardView();
        fieldScores = new FieldScoreView();

		content.add(desiredCards);
		content.add(fieldScores);

		addContent(content);
	}

	@Override
	public void update() {
		desiredCards.setCards(ai.desired);
		fieldScores.setScores(ai.fieldScores);
	}
}

class FieldScoreView extends JPanel {
    HashMap<Intelligence.FIELD_TYPE, JLabel> scores = new HashMap<>();

    public FieldScoreView() {
        setLayout(new GridLayout(0,2));

        for (Intelligence.FIELD_TYPE t : Intelligence.FIELD_TYPE.values()) {
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(1,0));
            p.setBackground(Color.GRAY);

            JLabel field = new Label(t + ": ");
            JLabel label = new Label("0");
            scores.put(t, label);

            p.add(field);
            p.add(label);
            add(p);
        }
    }

    public void setScores(HashMap<Intelligence.FIELD_TYPE, Integer> fieldScores) {
        for (Map.Entry<Intelligence.FIELD_TYPE, Integer> e : fieldScores.entrySet()) {
            scores.get(e.getKey()).setText("" + e.getValue());
        }
    }
}
