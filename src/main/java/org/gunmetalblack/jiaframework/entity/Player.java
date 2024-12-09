package org.gunmetalblack.jiaframework.entity;

import org.gunmetalblack.jiaframework.render.layers.ChildRenderLayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Player extends LivingEntity {
    private static FacingDirection currentDirection;

    public Player(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, ChildRenderLayer livingLayer) {
        super(character, foregroundColor, backgroundColor, xPos, yPos, livingLayer);
        currentDirection = FacingDirection.UP;
    }

    public void highlightCurrentDirection() {
        int relativeX = getxPos() + currentDirection.getX();
        int relativeY = getyPos() + currentDirection.getY();
        for (Entity item : getLivingLayer().getParentLayer().getEntitiesInLayer()) {
            if (item.getxPos() == relativeX && item.getyPos() == relativeY) {
                item.getGraphic().backgroundColor = Color.yellow;
            }
        }
    }

    public void manipulateBlock(Boolean shouldPlace) {
        int relativeX = getxPos() + currentDirection.getX();
        int relativeY = getyPos() + currentDirection.getY();

        // good lord
        ArrayList<Entity> toRemove = new ArrayList<>();
        ArrayList<Entity> toAdd = new ArrayList<>();

        // https://stackoverflow.com/questions/10431981/remove-elements-from-collection-while-iterating
        for (Entity item : getLivingLayer().getParentLayer().getEntitiesInLayer()) {
            if (item.getxPos() == relativeX && item.getyPos() == relativeY) {
                if (shouldPlace && item.getGraphic().character == (char)176) {
                    toAdd.add(new Entity((char)219, new Color(51, 24, 0), Color.black, item.getxPos(), item.getyPos(), true, true));
                    toRemove.add(item);  // Mark for removal
                } else if (!shouldPlace && item.isBreakable()) {
                    toAdd.add(new Entity((char)176, new Color(58, 191, 22), Color.black, item.getxPos(), item.getyPos(), false, false));
                    toRemove.add(item);  // Mark for removal
                }
            }
        }

        // This is done to prevent Concurrent Modification exception
        getLivingLayer().getParentLayer().getEntitiesInLayer().removeAll(toRemove);
        getLivingLayer().getParentLayer().getEntitiesInLayer().addAll(toAdd);
    }



    public FacingDirection getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(FacingDirection currentDirection) {
        Player.currentDirection = currentDirection;
    }


    public enum FacingDirection {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        private final int x;
        private final int y;

        FacingDirection(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Getter method to retrieve the integer value
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
