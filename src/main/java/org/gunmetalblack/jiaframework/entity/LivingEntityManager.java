package org.gunmetalblack.jiaframework.entity;

import org.gunmetalblack.jiaframework.render.layers.ChildRenderLayer;
import org.gunmetalblack.jiaframework.render.layers.RenderLayer;

import java.awt.*;
import java.util.ArrayList;

public class LivingEntityManager {
    private static ChildRenderLayer livingLayer;
    public static Player player;
    public LivingEntityManager(ChildRenderLayer livingLayer)
    {
        /*
        * Sets the spawn of the player based off the gamelayer position
        * */
        this.livingLayer = livingLayer;
        Entity pSpawn = null;
        ArrayList<Entity> entities = livingLayer.getParentLayer().getEntitiesInLayer();
        for (Entity entity : entities) {
                if (entity.getGraphic().character == '*') {
                    pSpawn = entity;
                }
        }
        if(pSpawn == null)
        {
            throw new IllegalStateException("No Player Spawn In Level!");
        }
        player = new Player((char)1, Color.YELLOW,Color.black,pSpawn.getxPos(),pSpawn.getyPos(), livingLayer);
    }

    public static ChildRenderLayer getLivingLayer() {
        return livingLayer;
    }

    public void instantiateLivingEntity(RenderLayer layer, Entity e)
    {
        layer.getEntitiesInLayer().add(e);
    }
}
