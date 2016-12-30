package com.github.s7uxn37.phase10.ui;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TitlePanel extends JPanel {
    public TitlePanel(String title) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.1;
        JLabel label = new Label(title);
        super.add(label, c);
        setVisible(true);
    }

    protected void addContent(Component comp) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.weighty = 0.9;
        super.add(comp, c);
        comp.setVisible(true);
    }
}
