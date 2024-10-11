package org.gunmetalblack;

import org.gunmetalblack.input.InputManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args)
    {
        Init window = new Init();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        // Pass the Init instance to InputManager
        InputManager inputManager = new InputManager(window);

        // Add the InputManager as the KeyListener
        window.addKeyListener(inputManager);
        Engine engine = new Engine(window);
    }
}
