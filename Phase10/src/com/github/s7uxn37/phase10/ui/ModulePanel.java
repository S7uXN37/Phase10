package com.github.s7uxn37.phase10.ui;

import com.github.s7uxn37.phase10.Intelligence;

import java.awt.*;

public abstract class ModulePanel extends TitlePanel {

	final Intelligence ai;

	ModulePanel(String title, Intelligence intelligence) {
		super(title);
		ai = intelligence;
	}
	
	final void addContent(Component comp) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.weighty = 0.9;
		super.add(comp, c);
	}
	
	public abstract void update();
}
