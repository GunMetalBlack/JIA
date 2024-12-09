package org.gunmetalblack;

import org.gunmetalblack.jiaframework.JIAWindow;
import org.gunmetalblack.jiaframework.input.InputManager;
import org.gunmetalblack.jiaframework.Engine;

import javax.swing.*;

public class Main {
    /**
     Horror : Genre

    Enemies As Weapons : Rule

    Distant Future : Setting

    Murder Mystery
    * */
    public static void main(String[] args)
    {
        JIAWindow window = new JIAWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        // Pass the Init instance to InputManager
        InputManager inputManager = new InputManager(window);

        // Add the InputManager as the KeyListener
        window.addKeyListener(inputManager);
        Engine engine = new Engine(window, inputManager);
    }
}
