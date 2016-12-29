package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.Intelligence;
import com.github.s7uxn37.phase10.constructs.Move;
import com.github.s7uxn37.phase10.constructs.Target;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InputPanel extends ModulePanel {
    TargetInputPanel[] inputFields;
    CardInputPanel cardPanel;
    MoveInputPanel movePanel;

	public InputPanel(Intelligence intelligence) {
		super("Input", intelligence);

        JPanel contentParent = new JPanel();
        contentParent.setLayout(new FlowLayout());

        // LEFT
        JPanel contentLeft = new JPanel();
        contentLeft.setLayout(new GridLayout(0, 1));
        contentLeft.setBackground(Color.GRAY);

        inputFields = new TargetInputPanel[4];
        for(int i = 0; i < inputFields.length; i++) {
            inputFields[i] = new TargetInputPanel("Target " + (i+1) + ": ");
            contentLeft.add(inputFields[i]);
        }

        JButton submitTargets = new JButton("Update desires");
        submitTargets.addActionListener(e -> causeUpdateDesires());

        contentLeft.add(submitTargets);

        // RIGHT
        JPanel contentRight = new JPanel();
        contentRight.setLayout(new GridLayout(0, 1));
        contentRight.setBackground(Color.LIGHT_GRAY);

        cardPanel = new CardInputPanel();
        movePanel = new MoveInputPanel();
        JButton submitMove = new JButton("Update card");
        submitMove.addActionListener(e -> causeUpdateCard());

        contentRight.add(cardPanel);
        contentRight.add(movePanel);
        contentRight.add(submitMove);

        // PARENT
        contentParent.add(contentLeft);
        contentParent.add(contentRight);

        addContent(contentParent);
    }

    void causeUpdateDesires() {
        ArrayList<Target> targets = new ArrayList<>();
        for (TargetInputPanel tip : inputFields) {
            ArrayList<Target> selected = tip.getSelected();
            targets.addAll(selected);
        }

        ai.updateDesires(targets);
    }

    void causeUpdateCard() {
        Card c = cardPanel.getSelected();
        Move m = movePanel.getSelected();

        ai.updateCard(c, m);
    }

	@Override
	public void update() {
		// nothing to update, no info from ai needed
	}

}

class TargetInputPanel extends JPanel {
    static String[] options;
    static {
        options = new String[Intelligence.TARGET_TYPE.values().length];
        for (int i = 0; i < options.length; i++) {
            options[i] = Intelligence.TARGET_TYPE.values()[i].toString();
        }
    }

    JComboBox<String> jComboBox;
    JSpinner argSpinner;
    JSpinner countSpinner;

    public TargetInputPanel(String label) {
        setLayout(new FlowLayout());
        setBackground(Color.YELLOW);

        jComboBox = new JComboBox<>(options);

        SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 10, 1);
        argSpinner = new JSpinner(model);
        SpinnerNumberModel model2 = new SpinnerNumberModel(0, 0, 10, 1);
        countSpinner = new JSpinner(model2);

        add(new JLabel(label));
        add(jComboBox);
        add(argSpinner);
        add(new JLabel(" x "));
        add(countSpinner);
    }

    public ArrayList<Target> getSelected() {
        ArrayList<Target> selected = new ArrayList<>();
        for (int i = 0; i < (Integer) countSpinner.getValue(); i++)
            selected.add(new Target((String) jComboBox.getSelectedItem(), (Integer) argSpinner.getValue()));
        return selected;
    }
}

class CardInputPanel extends JPanel {
    JTextField jTextField;

    public CardInputPanel() {
        setLayout(new FlowLayout());
        setBackground(Color.YELLOW);

        jTextField = new JTextField(5);

        add(new JLabel("Card: "));
        add(jTextField);
    }

    public Card getSelected() {
        return new Card(jTextField.getText());
    }
}

class MoveInputPanel extends JPanel {
    static String[] options;
    static {
        options = new String[Intelligence.CARD_LOCATION.values().length];
        for (int i = 0; i < options.length; i++) {
            options[i] = Intelligence.CARD_LOCATION.values()[i].toString();
        }
    }

    JComboBox<String> jComboBoxFrom;
    JComboBox<String> jComboBoxTo;

    public MoveInputPanel() {
        setLayout(new FlowLayout());
        setBackground(Color.YELLOW);

        jComboBoxFrom = new JComboBox<>(options);
        jComboBoxTo = new JComboBox<>(options);

        add(new JLabel("Moved from: "));
        add(jComboBoxFrom);
        add(new JLabel("to: "));
        add(jComboBoxTo);
    }

    public Move getSelected() {
        return new Move(
                Intelligence.CARD_LOCATION.valueOf((String) jComboBoxFrom.getSelectedItem()),
                Intelligence.CARD_LOCATION.valueOf((String) jComboBoxTo.getSelectedItem())
        );
    }
}