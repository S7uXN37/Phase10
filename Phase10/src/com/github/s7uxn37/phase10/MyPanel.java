package com.github.s7uxn37.phase10;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class MyPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6654457526116387323L;
	
	Intelligence ai;
	
	public MyPanel(String title, Intelligence intelligence) {
		ai = intelligence;
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.1;
		JLabel label = new JLabel(title);
		super.add(label, c);
	}
	
	public void addContent(Component comp) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.weighty = 0.9;
		super.add(comp, c);
	}
	
	public abstract void update();
}
