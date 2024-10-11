package org.gunmetalblack.input;

import org.gunmetalblack.Init;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputManager implements KeyListener {
    private Init window;

    public InputManager(Init window) {
        this.window = window;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Handle key typed event if needed
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        // This method is called when a key is pressed
        int keyCode = keyEvent.getKeyCode();  // Get the keycode of the pressed key

        if (keyCode == KeyEvent.VK_W) {

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key released event if needed
    }
}
