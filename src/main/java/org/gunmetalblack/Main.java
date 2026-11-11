package org.gunmetalblack;

import org.gunmetalblack.jiaframework.JIAWindow;
import org.gunmetalblack.rkis.RKISGame;

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
        RKISGame game = new RKISGame(window.getTerminal());
        window.addKeyListener(game);
        game.render();
        window.requestFocusInWindow();
    }
}
