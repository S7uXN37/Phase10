package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Optimizer;
import com.github.s7uxn37.phase10.constructs.Card;
import com.github.s7uxn37.phase10.Intelligence;
import com.github.s7uxn37.phase10.constructs.Move;
import com.github.s7uxn37.phase10.constructs.PartialTarget;
import com.github.s7uxn37.phase10.constructs.Target;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.StringJoiner;

public class InputPanel extends ModulePanel {
    TargetInputPanel[] inputFields;
    CardInputPanel cardPanel;
    CardsInputPanel cardsPanel;
    MoveInputPanel movePanel;

    JFrame frame;

	public InputPanel(Intelligence intelligence, JFrame parent) {
		super("Input", intelligence);

		frame = parent;

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

        JLabel deepModeLabel = new Label("Use deep scan?");
        JCheckBox deepModeBox = new JCheckBox();
        deepModeBox.addActionListener(e -> Optimizer.setDeepScanEnabled(deepModeBox.isSelected()));
        JButton submitTargets = new JButton("Update partialTargets");
        submitTargets.setForeground(Label.TEXT_COLOR);
        submitTargets.addActionListener(e -> causeUpdateDesires());

        JPanel submitContainer = new JPanel();
        submitContainer.add(deepModeLabel);
        submitContainer.add(deepModeBox);
        submitContainer.add(submitTargets);

        JLabel sourceLabel = new Label("Source: ");
        String[] options = new String[Intelligence.CARD_LOCATION.values().length];
        for (int i = 0; i < options.length; i++) {
            options[i] = Intelligence.CARD_LOCATION.values()[i].toString();
        }
        JComboBox<String> jComboBox = new JComboBox<>(options);
        jComboBox.setForeground(Label.TEXT_COLOR);
        JButton completeButton = new JButton("Complete targets");
        completeButton.setForeground(Label.TEXT_COLOR);
        completeButton.addActionListener(e -> causeCompleteTargets((String)jComboBox.getSelectedItem()));

        JPanel completeContainer = new JPanel();
        completeContainer.add(sourceLabel);
        completeContainer.add(jComboBox);
        completeContainer.add(completeButton);

        contentLeft.add(submitContainer);
        contentLeft.add(completeContainer);

        // RIGHT
        JPanel contentRight = new JPanel();
        contentRight.setLayout(new GridLayout(0, 1));
        contentRight.setBackground(Color.LIGHT_GRAY);

        cardPanel = new CardInputPanel();
        movePanel = new MoveInputPanel();
        JButton submitMove = new JButton("Update card");
        submitMove.setForeground(Label.TEXT_COLOR);
        submitMove.addActionListener(e -> causeUpdateCard());
        cardsPanel = new CardsInputPanel();
        JButton submitCards = new JButton("Update face up cards");
        submitCards.setForeground(Label.TEXT_COLOR);
        submitCards.addActionListener(e -> causeUpdateFaceUp());

        contentRight.add(cardPanel);
        contentRight.add(movePanel);
        contentRight.add(submitMove);
        contentRight.add(cardsPanel);
        contentRight.add(submitCards);

        // PARENT
        contentParent.add(contentLeft);
        contentParent.add(contentRight);

        addContent(contentParent);
    }

    private void causeCompleteTargets(String locStr) {
        ArrayList<Target> targets = getAllTargets();
        Intelligence.CARD_LOCATION source = Intelligence.CARD_LOCATION.valueOf(locStr);

        CompletionDialog dialog = new CompletionDialog(frame, targets, source, ai);
        dialog.makeVisible();
    }

    void causeUpdateDesires() {
        ArrayList<Target> targets = getAllTargets();

        ai.updateDesires(targets);
    }
    void causeUpdateCard() {
        Card c = cardPanel.getSelected();
        Move m = movePanel.getSelected();

        ai.updateCard(c, m);
    }
    void causeUpdateFaceUp() {
        Card[] cards = cardsPanel.getSelected();

        ai.updateFaceUp(cards);
    }

	@Override
	public void update() {
		// nothing to update, no info from ai needed
	}

    public ArrayList<Target> getAllTargets() {
        ArrayList<Target> targets = new ArrayList<>();
        for (TargetInputPanel tip : inputFields) {
            ArrayList<Target> selected = tip.getSelected();
            targets.addAll(selected);
        }
        return targets;
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
        jComboBox.setForeground(Label.TEXT_COLOR);

        SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 12, 1);
        argSpinner = new JSpinner(model);
        argSpinner.getEditor().getComponent(0).setForeground(Label.TEXT_COLOR);
        SpinnerNumberModel model2 = new SpinnerNumberModel(0, 0, 5, 1);
        countSpinner = new JSpinner(model2);
        countSpinner.getEditor().getComponent(0).setForeground(Label.TEXT_COLOR);

        add(new Label(label));
        add(jComboBox);
        add(argSpinner);
        add(new Label(" x "));
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
        jTextField.setForeground(Label.TEXT_COLOR);

        add(new Label("Card: "));
        add(jTextField);
    }

    public Card getSelected() {
        return new Card(jTextField.getText());
    }
}
class CardsInputPanel extends JPanel {
    JTextField jTextField;

    public CardsInputPanel() {
        setLayout(new FlowLayout());
        setBackground(Color.YELLOW);

        jTextField = new JTextField(40);
        jTextField.setForeground(Label.TEXT_COLOR);

        add(new Label("Cards: "));
        add(jTextField);
    }

    public Card[] getSelected() {
        return Card.parseCards(jTextField.getText());
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
        jComboBoxFrom.setForeground(Label.TEXT_COLOR);
        jComboBoxTo = new JComboBox<>(options);
        jComboBoxTo.setForeground(Label.TEXT_COLOR);

        add(new Label("Moved from: "));
        add(jComboBoxFrom);
        add(new Label("to: "));
        add(jComboBoxTo);
    }

    public Move getSelected() {
        return new Move(
                Intelligence.CARD_LOCATION.valueOf((String) jComboBoxFrom.getSelectedItem()),
                Intelligence.CARD_LOCATION.valueOf((String) jComboBoxTo.getSelectedItem())
        );
    }
}

class CompletionDialog extends JDialog {
    Intelligence ai;
    Intelligence.CARD_LOCATION source;
    ArrayList<Target> targets;
    ArrayList<JTextField> textFields;

    public CompletionDialog(JFrame parent, ArrayList<Target> targets, Intelligence.CARD_LOCATION source, Intelligence ai) {
        super(parent, "Complete targets using " + source.toString(), true);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        this.ai = ai;
        this.source = source;
        this.targets = targets;
        this.textFields = new ArrayList<>();

        JLabel label = new Label("Enter cards to complete specified targets, all cards must be present in " + source.toString());
        contentPanel.add(label);

        for (Target target : targets) {
            JPanel panel = new JPanel();

            JLabel targetName = new Label(target.toString() + ": ");
            JTextField textField = new JTextField(50);
            textField.setForeground(Label.TEXT_COLOR);
            textFields.add(textField);

            panel.add(targetName);
            panel.add(textField);

            contentPanel.add(panel);
        }

        JButton submit = new JButton("Submit");
        submit.setForeground(Label.TEXT_COLOR);
        submit.addActionListener(e -> {
            dispose();
            submitCompletion();
        });

        contentPanel.add(submit);

        add(contentPanel);
    }

    private void submitCompletion() {
        ArrayList<PartialTarget> partialTargets = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            PartialTarget partialTarget = new PartialTarget(
                    targets.get(i),
                    Card.parseCards(textFields.get(i).getText()),
                    new ArrayList<>()
            );
            partialTargets.add(partialTarget);
        }

        ai.completeTargets(partialTargets, source);
    }

    public void makeVisible() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}