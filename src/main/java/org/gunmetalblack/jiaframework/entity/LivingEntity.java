package org.gunmetalblack.jiaframework.entity;

import org.gunmetalblack.jiaframework.events.CollisionEvent;
import org.gunmetalblack.jiaframework.events.GlobalEventManager;
import org.gunmetalblack.jiaframework.render.layers.ChildRenderLayer;
import org.gunmetalblack.jiaframework.render.layers.RenderLayer;

import java.awt.*;


public class LivingEntity extends Entity {

    private ChildRenderLayer livingLayer;

    public LivingEntity(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, ChildRenderLayer livingLayer) {
        super(character, foregroundColor, backgroundColor, xPos, yPos, true,false);
        this.livingLayer = livingLayer;
    }

    /**
     * Moves a livingEntity towards a new position and validates collision
     *
     * @param relativeX the new x-coordinate of the entity to move towards.
     * @param relativeY the new y-coordinate of the entity to move towards.
     */
    public void move(int relativeX, int relativeY) {
        CollisionEvent collisionEvent = new CollisionEvent(getxPos() + relativeX, getyPos() + relativeY);
        GlobalEventManager.collisionEventManager.<CollisionEvent>ExecuteEvent(collisionEvent);
        collisionEvent.getCollisions().remove(this);
        //JIALogger.log(JIALogger.LogLevel.WARN, collisionEvent.getCollisions() + "");
        if (collisionEvent.getCollisions().isEmpty()) {
            setxPos(getxPos() + relativeX);
            setyPos(getyPos() + relativeY);
        }
    }


    /**
     * Sets the x-coordinate of the entity.
     *
     * @param xPos the new x-coordinate of the entity.
     *             And updates its position within the living entity layer.
     */
    @Override
    public void setxPos(int xPos) {
        for (Entity livingEntity : livingLayer.getEntitiesInLayer()) {
            if (livingEntity.equals(this)) {
                super.setxPos(xPos);
            }
        }
    }

    /*
    * Getter for the render Layer
    * */
    public ChildRenderLayer getLivingLayer() {
        return livingLayer;
    }

    /**
     * Sets the y-coordinate of the entity.
     *
     * @param yPos the new y-coordinate of the entity.
     *             And updates its position within the living entity layer.
     */
    @Override
    public void setyPos(int yPos) {
        for (Entity livingEntity : livingLayer.getEntitiesInLayer()) {
            if (livingEntity.equals(this)) {
                super.setyPos(yPos);
            }
        }
    }
}
