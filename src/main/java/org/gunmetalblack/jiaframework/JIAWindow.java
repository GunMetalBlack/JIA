package org.gunmetalblack.jiaframework;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;


import javax.swing.*;

public class JIAWindow extends JFrame {
    private static final long serialVersionUID = 1060623638149583738L;

    // The terminal that InputManager will use
    private AsciiPanel terminal;

    /**
     * Instantiates everything!
     */
    public JIAWindow() {
        super();
        terminal = new AsciiPanel(80, 60, AsciiFont.CP437_16x16);
        setFocusable(true);
        setResizable(false);
        add(terminal);
        pack();
        //Instantiate Event Listeners:
        // Temp testing
        //GlobalEventManager.collisionEventManager.addEventListener(GlobalEventManager.collisionEventListener);
    }

    // Getter for the terminal, so InputManager can write to it
    public AsciiPanel getTerminal() {
        return terminal;
    }
}
