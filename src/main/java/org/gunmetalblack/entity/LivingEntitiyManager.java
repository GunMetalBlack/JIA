package org.gunmetalblack.entity;

import org.gunmetalblack.render.ChildRenderLayer;
import org.gunmetalblack.render.RenderLayer;

import java.awt.*;

public class LivingEntitiyManager {
    private static ChildRenderLayer livingLayer;
    public static LivingEntity player;
    public LivingEntitiyManager(ChildRenderLayer livingLayer)
    {
        this.livingLayer = livingLayer;
        player = new LivingEntity((char)1, Color.YELLOW,Color.black,0,0, livingLayer);
    }

    public static ChildRenderLayer getLivingLayer() {
        return livingLayer;
    }

    public void instantiateLivingEntity(RenderLayer layer, Entity e)
    {
        layer.getEntitiesInLayer()[e.getyPos()][e.getxPos()] = e;
    }
}
