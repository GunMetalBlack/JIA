package org.gunmetalblack.jiaframework.entity;

import org.gunmetalblack.jiaframework.render.layers.ChildRenderLayer;

import java.awt.*;

public class Player extends LivingEntity{
    public Player(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, ChildRenderLayer livingLayer) {
        super(character, foregroundColor, backgroundColor, xPos, yPos, livingLayer);
    }
}
