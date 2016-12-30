package com.github.s7uxn37.phase10.ui;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {
    public static final Color TEXT_COLOR = Color.BLACK;

    public Label(String text) {
        super(text);
        setForeground(TEXT_COLOR);
    }

    public Label() {
        super();
    }
}
