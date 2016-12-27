package com.github.s7uxn37.phase10;

import com.github.s7uxn37.phase10.ui.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainClass {
	public static void main(String args[]) {
		Intelligence intelligence = new Intelligence();
		
		JFrame frame = new JFrame("Phase 10 AI");
		frame.setSize(1200, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints(
				0, 0, // gridX, gridY -> upper left cell
				2, 1, // width, height in cells
				0.5, 0.5, // weightX, weightY
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
		c.gridwidth = 1;
		c.gridheight = 1;
		frame.add(hand, c);

		InputPanel input = new InputPanel(intelligence);
		input.setBackground(Color.YELLOW);
		c = constraints;
		c.gridx = 2;
		c.gridy = 2;
		c.weighty = 0.35;
		c.gridwidth = 1;
		c.gridheight = 1;
		frame.add(input, c);

		frame.setVisible(true);
		
		final MyPanel[] toUpdate = new MyPanel[]{pool, desired, hand, players, input};
		intelligence.setUpdateListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (MyPanel p : toUpdate)
					p.update();
			}
		});
		
		numPlayersDialog(frame, intelligence);
	}
	
	static void numPlayersDialog(JFrame frame, Intelligence intelligence) {
		JDialog numPlayersDialog = new JDialog(frame, "Number of players", true);
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 2, 6, 4);
		JLabel label = new JLabel("4");
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				label.setText(""+slider.getValue());
			}
		});
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				intelligence.init(slider.getValue());
				numPlayersDialog.dispose();
				playerHandDialog(frame, intelligence);
			}
		});
		numPlayersDialog.setLayout(new FlowLayout());
		numPlayersDialog.add(label);
		numPlayersDialog.add(slider);
		numPlayersDialog.add(button);
		numPlayersDialog.setSize(300, 70);
		numPlayersDialog.setLocationRelativeTo(null);
		numPlayersDialog.setVisible(true);
	}
	
	static void playerHandDialog(JFrame frame, Intelligence intelligence) {
		JDialog numPlayersDialog = new JDialog(frame, "Your hand", true);
		JLabel label = new JLabel("Cards in your hand, separated by commas;"
				+ " Colors are letters, numbers come before colors (e.g. 2B for a blue 2)");
		JTextField textField = new JTextField(50);
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Card[] cards = toCards(textField.getText());
				intelligence.setHand(cards);
				numPlayersDialog.dispose();
			}
		});
		numPlayersDialog.setLayout(new FlowLayout());
		numPlayersDialog.add(label);
		numPlayersDialog.add(textField);
		numPlayersDialog.add(button);
		numPlayersDialog.setSize(650, 100);
		numPlayersDialog.setLocationRelativeTo(null);
		numPlayersDialog.setVisible(true);
	}
	
	static Card[] toCards(String text) {
		String[] strs = text.split(",");
		Card[] cards = new Card[strs.length];
		for (int i = 0; i < strs.length; i++) {
			String s = strs[i];
			s.replaceAll(" ", "");
			Card c = new Card();
			c.color = s.charAt(1);
			c.number = s.charAt(0);
			cards[i] = c;
		}
		return cards;
	}
}
