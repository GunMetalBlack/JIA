package org.gunmetalblack.entity;

import org.gunmetalblack.events.CollisionEvent;
import org.gunmetalblack.events.JIAGenericEvent;
import org.gunmetalblack.render.ChildRenderLayer;

import java.awt.*;

public class Player extends LivingEntity{
    public Player(char character, Color foregroundColor, Color backgroundColor, int xPos, int yPos, ChildRenderLayer livingLayer) {
        super(character, foregroundColor, backgroundColor, xPos, yPos, livingLayer);
    }
}
