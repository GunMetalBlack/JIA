package org.gunmetalblack.entity;

import org.gunmetalblack.render.ChildRenderLayer;
import org.gunmetalblack.render.RenderLayer;

import java.awt.*;

public class LivingEntity extends Entity{
    private RenderLayer livingLayer;
    public LivingEntity(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, ChildRenderLayer livingLayer) {
        super(character, foregroundColor, backgroundColor, xPos, yPos);
        this.livingLayer = livingLayer;
    }

    /**
     * Sets the x-coordinate of the entity.
     *
     * @param xPos the new x-coordinate of the entity.
     * And updates its position within the living entity layer.
     */
    @Override
    public void setxPos(int xPos) {
        livingLayer.GetEntitiesInLayer()[super.getyPos()][super.getxPos()] = null;
        super.setxPos(xPos);
        livingLayer.GetEntitiesInLayer()[super.getyPos()][super.getxPos()] = this;

    }

    /**
     * Sets the y-coordinate of the entity.
     *
     * @param yPos the new y-coordinate of the entity.
     * And updates its position within the living entity layer.
     */
    @Override
    public void setyPos(int yPos) {
        livingLayer.GetEntitiesInLayer()[super.getyPos()][super.getxPos()] = null;
        super.setyPos(yPos);
        livingLayer.GetEntitiesInLayer()[super.getyPos()][super.getxPos()] = this;
    }


}
