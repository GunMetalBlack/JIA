package org.gunmetalblack.jiaframework.input;

import org.gunmetalblack.Init;
import org.gunmetalblack.jiaframework.entity.LivingEntityManager;
import org.gunmetalblack.jiaframework.entity.Player;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputManager implements KeyListener {
    private Init window;
    private LivingEntityManager livingEntityManager;

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

        //TODO: Write collison for the player

        //Ps this is jank as sin
        //This is an attempt to make the code more readable! Please Forgive my war crimes
        //Getting entites on the mainlayer
        Player player = livingEntityManager.player;
        //player.highlightCurrentDirection();
        if (keyCode == KeyEvent.VK_W) {
            player.move(0, -1);
        } else if (keyCode == KeyEvent.VK_D) {
            player.move(1, 0);
        } else if (keyCode == KeyEvent.VK_A) {
            player.move(-1, 0);
        } else if (keyCode == KeyEvent.VK_S) {
            player.move(0, 1);
        } else if (keyCode == KeyEvent.VK_UP) {
            player.setCurrentDirection(Player.FacingDirection.UP);

        } else if (keyCode == KeyEvent.VK_DOWN) {
            player.setCurrentDirection(Player.FacingDirection.DOWN);

        } else if (keyCode == KeyEvent.VK_LEFT) {
            player.setCurrentDirection(Player.FacingDirection.LEFT);

        } else if (keyCode == KeyEvent.VK_RIGHT) {
            player.setCurrentDirection(Player.FacingDirection.RIGHT);

        } else if (keyCode == KeyEvent.VK_ENTER) {
            player.manipulateBlock(true);

        } else if (keyCode == KeyEvent.VK_SHIFT) {
            player.manipulateBlock(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key released event if needed
    }

    public void setLivingEntitiyManager(LivingEntityManager livingEntityManager) {
        this.livingEntityManager = livingEntityManager;
    }

}
