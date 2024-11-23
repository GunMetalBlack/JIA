package org.gunmetalblack.input;

import org.gunmetalblack.Init;
import org.gunmetalblack.entity.Entity;
import org.gunmetalblack.entity.LivingEntitiyManager;
import org.gunmetalblack.entity.LivingEntity;
import org.gunmetalblack.events.CollisionEvent;
import org.gunmetalblack.events.GlobalEventManager;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputManager implements KeyListener {
    private Init window;
    private LivingEntitiyManager livingEntitiyManager;
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
        LivingEntity player = livingEntitiyManager.player;
        if (keyCode == KeyEvent.VK_W) {
            player.move(0,-1);
        }
        else if (keyCode == KeyEvent.VK_D)
        {
                player.move(1,0);
        }
        else if (keyCode == KeyEvent.VK_A) {
            player.move(-1,0);
        }
        else if (keyCode == KeyEvent.VK_S) {
                player.move(0,1);
            }
        }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key released event if needed
    }

    public void setLivingEntitiyManager(LivingEntitiyManager livingEntitiyManager) {
        this.livingEntitiyManager = livingEntitiyManager;
    }

}
