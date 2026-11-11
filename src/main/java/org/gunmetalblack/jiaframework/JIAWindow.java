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
    public static final int RKIS_COLUMNS = 140;
    public static final int RKIS_ROWS = 58;

    public JIAWindow() {
        super();
        setTitle("R.K.I.S. — Recovery Kernel Integration System");
        terminal = new AsciiPanel(RKIS_COLUMNS, RKIS_ROWS, AsciiFont.CP437_10x10);
        setFocusable(true);
        setResizable(false);
        add(terminal);
        pack();
        setLocationRelativeTo(null);
        //Instantiate Event Listeners:
        // Temp testing
        //GlobalEventManager.collisionEventManager.addEventListener(GlobalEventManager.collisionEventListener);
    }

    // Getter for the terminal, so InputManager can write to it
    public AsciiPanel getTerminal() {
        return terminal;
    }
}
