package org.gunmetalblack.input;

import org.gunmetalblack.Init;
import org.gunmetalblack.entity.Entity;
import org.gunmetalblack.entity.LivingEntitiyManager;
import org.gunmetalblack.entity.LivingEntity;

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

        //Ps this shit is jank as sin
        //This is an attempt to make the code more readable! Please Forgive my war crimes
        //Getting entites on the mainlayer
        Entity[][] entitiesOnTheMainGameLayer = livingEntitiyManager.getLivingLayer().getParentLayer().getEntitiesInLayer();
        Entity possibleCollisonObject;
        LivingEntity player = livingEntitiyManager.player;
        if (keyCode == KeyEvent.VK_W) {
            possibleCollisonObject = entitiesOnTheMainGameLayer[player.getyPos()-1][player.getxPos()];
            if(!(possibleCollisonObject.canCollide()))
            {
                player.setyPos(livingEntitiyManager.player.getyPos()-1);
            }
        }
        else if (keyCode == KeyEvent.VK_D){
            possibleCollisonObject = entitiesOnTheMainGameLayer[player.getyPos()][player.getxPos()+1];
            if(!(possibleCollisonObject.canCollide()))
                player.setxPos(livingEntitiyManager.player.getxPos()+1);
        } else if (keyCode == KeyEvent.VK_A) {
            possibleCollisonObject = entitiesOnTheMainGameLayer[player.getyPos()][player.getxPos()-1];
            if(!(possibleCollisonObject.canCollide()))
                player.setxPos(livingEntitiyManager.player.getxPos()-1);
        } else if (keyCode == KeyEvent.VK_S) {
            possibleCollisonObject = entitiesOnTheMainGameLayer[player.getyPos()+1][player.getxPos()];
            if(!(possibleCollisonObject.canCollide()))
                player.setyPos(livingEntitiyManager.player.getyPos()+1);
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
