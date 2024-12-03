package org.gunmetalblack.entity;

import org.gunmetalblack.events.CollisionEvent;
import org.gunmetalblack.events.GlobalEventManager;
import org.gunmetalblack.render.ChildRenderLayer;
import org.gunmetalblack.render.RenderLayer;
import org.gunmetalblack.tools.JIALogger;

import java.awt.*;


public class LivingEntity extends Entity {
    private RenderLayer livingLayer;

    public LivingEntity(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, ChildRenderLayer livingLayer) {
        super(character, foregroundColor, backgroundColor, xPos, yPos, true);
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
        GlobalEventManager.collisionEventManager.ExecuteEvent(collisionEvent);
        collisionEvent.getCollisions().remove(this);
        JIALogger.log(JIALogger.LogLevel.WARN, collisionEvent.getCollisions() + "");
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
