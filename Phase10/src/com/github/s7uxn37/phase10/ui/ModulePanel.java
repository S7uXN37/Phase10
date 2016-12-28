package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

public abstract class ModulePanel extends TitlePanel {

	Intelligence ai;
	
	public ModulePanel(String title, Intelligence intelligence) {
		super(title);
		ai = intelligence;
	}
	
	protected void addContent(Component comp) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.weighty = 0.9;
		super.add(comp, c);
	}
	
	public abstract void update();
}
