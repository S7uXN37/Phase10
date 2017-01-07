package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;
import com.github.s7uxn37.phase10.constructs.PartialTarget;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class DesiredPanel extends ModulePanel {
	private final JPanel targetPanel;
	private final JPanel completedPanel;
	private final FieldInfoView fieldScores;

	public DesiredPanel(Intelligence intelligence) {
		super("Desired cards", intelligence);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		targetPanel = new JPanel();
		targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
        fieldScores = new FieldInfoView();
        completedPanel = new JPanel();
        completedPanel.setLayout(new BoxLayout(completedPanel, BoxLayout.Y_AXIS));

		content.add(targetPanel);
		content.add(fieldScores);
		content.add(completedPanel);

		addContent(content);
	}

	@Override
	public void update() {
	    targetPanel.removeAll();
	    targetPanel.add(new Label("Partial Targets"));
		for (PartialTarget target : ai.partialTargets) {
		    JPanel partialTargetView = getPartialTargetView(target, !ai.getIsAddingToCompletedTargets());

		    targetPanel.add(partialTargetView);
        }

		fieldScores.setFieldInfo(ai.fieldInfo);

        completedPanel.removeAll();
        completedPanel.add(new Label("Completed Targets"));
        for (PartialTarget target : ai.completedTargets) {
            JPanel partialTargetView = getPartialTargetView(target, false);

            completedPanel.add(partialTargetView);
        }
	}

    private JPanel getPartialTargetView(PartialTarget target, boolean verboseView) {
        JPanel partialTargetView = new JPanel();

        CardView cardView = new CardView(CardView.SORTING.PROBABILITY);
        cardView.setCards(target.cards);
        CardView missingCardsView = new CardView(CardView.SORTING.PROBABILITY);
        missingCardsView.setCards(target.desiredCards);

        String partialTargetStr = verboseView ? target.target.toString() : target.target.toString().split(" ")[0];

        partialTargetView.add(new Label(partialTargetStr));
        partialTargetView.add(cardView);
        if (verboseView) partialTargetView.add(new Label("Cards missing: " + target.cardsMissing));
        partialTargetView.add(missingCardsView);

        return partialTargetView;
    }
}

final class FieldInfoView extends JPanel {
    private final HashMap<Intelligence.FIELD_TYPE, JLabel> scores = new HashMap<>();

    FieldInfoView() {
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

    final void setFieldInfo(HashMap<Intelligence.FIELD_TYPE, String> fieldScores) {
        for (Map.Entry<Intelligence.FIELD_TYPE, String> e : fieldScores.entrySet()) {
            scores.get(e.getKey()).setText(e.getValue());
        }
    }
}
