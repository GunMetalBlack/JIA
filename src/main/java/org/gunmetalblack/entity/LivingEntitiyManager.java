package org.gunmetalblack.entity;

import org.gunmetalblack.render.RenderLayer;

import java.awt.*;

public class LivingEntitiyManager {
    public static PlayerEntity player = new PlayerEntity((char)1, Color.YELLOW,Color.black,4,5);

    public void instantiateLivingEntity(RenderLayer layer, Entity e)
    {
        layer.GetEntitiesInLayer()[e.getyPos()][e.getxPos()] = e;
    }
}
