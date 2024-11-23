package org.gunmetalblack;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import org.gunmetalblack.events.GlobalEventManager;


import javax.swing.*;

public class Init extends JFrame {
    private static final long serialVersionUID = 1060623638149583738L;

    // The terminal that InputManager will use
    private AsciiPanel terminal;

    public Init() {
        super();
        terminal = new AsciiPanel(80, 60, AsciiFont.CP437_16x16);
        setFocusable(true);
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
