package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;
import com.github.s7uxn37.phase10.constructs.PartialTarget;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DesiredPanel extends ModulePanel {
	JPanel targetPanel;
	FieldInfoView fieldScores;

	public DesiredPanel(Intelligence intelligence) {
		super("Desired cards", intelligence);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		targetPanel = new JPanel();
		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
        fieldScores = new FieldInfoView();

		content.add(targetPanel);
		content.add(fieldScores);

		addContent(content);
	}

	@Override
	public void update() {
	    targetPanel.removeAll();

		for (PartialTarget d : ai.partialTargets) {
		    JPanel desireView = new JPanel();

		    CardView cardView = new CardView(CardView.SORTING.PROBABILITY);
		    cardView.setCards(d.cards);
            CardView missingCardsView = new CardView(CardView.SORTING.PROBABILITY);
            missingCardsView.setCards(d.desiredCards);

		    desireView.add(new Label(d.target.toString()));
		    desireView.add(cardView);
		    desireView.add(new Label("Cards missing: " + d.cardsMissing));
            desireView.add(missingCardsView);

		    targetPanel.add(desireView);
        }
		fieldScores.setFieldInfo(ai.fieldInfo);
	}
}

class FieldInfoView extends JPanel {
    HashMap<Intelligence.FIELD_TYPE, JLabel> scores = new HashMap<>();

    public FieldInfoView() {
        setLayout(new GridLayout(0,2, 10, 0));

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

    public void setFieldInfo(HashMap<Intelligence.FIELD_TYPE, String> fieldScores) {
        for (Map.Entry<Intelligence.FIELD_TYPE, String> e : fieldScores.entrySet()) {
            scores.get(e.getKey()).setText(e.getValue());
        }
    }
}
