package org.gunmetalblack;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

import javax.swing.*;

public class Init extends JFrame {
    private static final long serialVersionUID = 1060623638149583738L;

    // The terminal that InputManager will use
    private AsciiPanel terminal;

    public Init() {
        super();
        terminal = new AsciiPanel(80, 60, AsciiFont.TALRYTH_15_15);
        setFocusable(true);
        add(terminal);
        pack();
    }

    // Getter for the terminal, so InputManager can write to it
    public AsciiPanel getTerminal() {
        return terminal;
    }
}
