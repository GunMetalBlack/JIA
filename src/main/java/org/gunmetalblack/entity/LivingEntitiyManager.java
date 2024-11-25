package org.gunmetalblack.entity;

import org.gunmetalblack.render.ChildRenderLayer;
import org.gunmetalblack.render.RenderLayer;
import org.gunmetalblack.tools.JIALogger;

import java.awt.*;

public class LivingEntitiyManager {
    private static ChildRenderLayer livingLayer;
    public static Player player;
    public LivingEntitiyManager(ChildRenderLayer livingLayer)
    {
        this.livingLayer = livingLayer;
<<<<<<< Updated upstream
        Entity pSpawn = null;
        Entity[][] entities = livingLayer.getParentLayer().getEntitiesInLayer();
        for (int i = 0; i < entities.length; i++) {
            for (int j = 0; j < entities[i].length; j++) {
                Entity entity = entities[i][j];
                if (entity.getGraphic().character == '*') {
                    pSpawn = entity;
                }
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
=======
        //Instantiate All Living entities here
        player = new LivingEntity((char)1, Color.YELLOW,Color.black,0,0, livingLayer);
>>>>>>> Stashed changes
    }

    public void instantiateLivingEntity(RenderLayer layer, Entity e)
    {
        layer.getEntitiesInLayer()[e.getyPos()][e.getxPos()] = e;
    }
}
