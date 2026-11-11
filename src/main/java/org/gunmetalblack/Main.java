package org.gunmetalblack;

import org.gunmetalblack.rkis.RKISGraphicalGame;

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
        // Must be selected before AWT initializes: textures, gradients and transforms
        // are then composited by the GPU instead of Java2D's software renderer.
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.accthreshold", "0");
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("R.K.I.S. — GPU Tactical Interface");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(new RKISGraphicalGame());
            window.setMinimumSize(new java.awt.Dimension(980, 650));
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setVisible(true);
            window.getContentPane().requestFocusInWindow();
        });
    }
}
