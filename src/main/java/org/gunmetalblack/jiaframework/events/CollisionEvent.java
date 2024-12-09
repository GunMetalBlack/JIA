package org.gunmetalblack.jiaframework.events;

import org.gunmetalblack.jiaframework.entity.Entity;

import java.util.ArrayList;

public class CollisionEvent {
    /**
     * Creates and stores data for the collision event
     */
    private int xPos;
    private int yPos;
    private ArrayList<Entity> collisions = new ArrayList<>();

    public CollisionEvent(int xPos,int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public ArrayList<Entity> getCollisions() {
        return collisions;
    }
    public int getyPos() {
        return yPos;
    }
    public int getxPos() {
        return xPos;
    }
}
