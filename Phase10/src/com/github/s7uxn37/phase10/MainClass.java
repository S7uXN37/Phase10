package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.ui.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class MainClass {
	public static void main(String args[]) {
		Intelligence intelligence = new Intelligence();

		SwingUtilities.invokeLater(() -> numPlayersDialog(intelligence));
	}
	
	static void numPlayersDialog(Intelligence intelligence) {
		JDialog dialog = new JDialog((JFrame)null, "Number of players", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 2, 6, 4);
		JLabel label = new JLabel("4");
		slider.addChangeListener(e -> label.setText(""+slider.getValue()));

		JButton button = new JButton("OK");
		button.addActionListener(e -> {
            dialog.dispose();
            playerHandDialog(intelligence, slider.getValue());
        });

		dialog.setLayout(new FlowLayout());

		dialog.add(label);
		dialog.add(slider);
		dialog.add(button);


        dialog.pack();

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
	static void playerHandDialog(Intelligence intelligence, int numPlayers) {
		JDialog dialog = new JDialog((JFrame)null, "Your player", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JLabel label = new JLabel("Cards in your player, separated by commas;"
				+ " Colors are letters, numbers come before colors (e.g. 2B for a blue 2)");

		JTextField textField = new JTextField(50);

		JButton button = new JButton("OK");
		button.addActionListener(e -> {
            Card[] cards = toCards(textField.getText());
            intelligence.init(numPlayers, cards);
            dialog.dispose();

            initMain(intelligence);
        });

		dialog.setLayout(new FlowLayout());

		dialog.add(label);
		dialog.add(textField);
		dialog.add(button);

		dialog.setSize(660, 100);

		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

    static void initMain(Intelligence intelligence) {
        JFrame frame = new JFrame("Phase 10 AI");
        frame.setSize(1600, 950);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints(
                0, 0, // upper left cell
                2, 1, // in cells
                0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, // alignment and fill
                new Insets(0, 0, 0, 0), 0, 0 // insets and padding
        );

        PoolPanel pool = new PoolPanel(intelligence);
        pool.setBackground(Color.CYAN);
        GridBagConstraints c = constraints;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        frame.add(pool, c);

        DesiredPanel desired = new DesiredPanel(intelligence);
        desired.setBackground(Color.RED);
        c = constraints;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.3;
        c.gridwidth = 1;
        c.gridheight = 1;
        frame.add(desired, c);

        PlayerPanel players = new PlayerPanel(intelligence);
        players.setBackground(Color.BLUE);
        c = constraints;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 2;
        frame.add(players, c);

        HandPanel hand = new HandPanel(intelligence);
        hand.setBackground(Color.GREEN);
        c = constraints;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.3;
        c.gridwidth = 1;
        c.gridheight = 1;
        frame.add(hand, c);

        InputPanel input = new InputPanel(intelligence);
        input.setBackground(Color.YELLOW);
        c = constraints;
        c.gridx = 2;
        c.gridy = 2;
        c.weighty = 0.35;
        c.weightx = 0.3;
        c.gridwidth = 1;
        c.gridheight = 1;
        frame.add(input, c);

        frame.setVisible(true);

        final ModulePanel[] toUpdate = new ModulePanel[]{pool, desired, hand, players, input};
        intelligence.setUpdateListener(e -> {
            for (ModulePanel p : toUpdate)
                p.update();
        });
    }
	
	static Card[] toCards(String text) {
		String[] cardStrings = text.split(",");
		Card[] cards = new Card[cardStrings.length];
		for (int i = 0; i < cardStrings.length; i++) {
			String s = cardStrings[i];
			s = s.replaceAll(" ", "").toUpperCase();

			Card c = new Card();
			switch (s.charAt(s.length() - 1)) { // BLUE, PURPLE, GREEN, RED
                case 'B':
                    c.colorIndex = 0;
                    break;
                case 'P':
                case 'V':
                    c.colorIndex = 1;
                    break;
                case 'G':
                    c.colorIndex = 2;
                    break;
                case 'R':
                    c.colorIndex = 3;
                    break;
				default:
					c.colorIndex = 0;
					break;
			}
			c.number = Integer.parseInt("" + s.substring(0, s.length() - 1));

			cards[i] = c;
		}
		return cards;
	}
}

class SimpleKeyListener implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {    }

    @Override
    public void keyPressed(KeyEvent e) {    }

    @Override
    public void keyReleased(KeyEvent e) {    }
}
