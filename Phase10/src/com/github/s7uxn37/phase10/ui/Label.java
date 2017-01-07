package com.github.s7uxn37.phase10.ui;

import javax.swing.*;
import java.awt.*;

final class Label extends JLabel {
    static final Color TEXT_COLOR = Color.BLACK;

    Label(String text) {
        super(text);
        setForeground(TEXT_COLOR);
    }

    Label() {
        super();
    }
}
