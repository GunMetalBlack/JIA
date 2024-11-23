package org.gunmetalblack.events;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class CollisionEvent {
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
