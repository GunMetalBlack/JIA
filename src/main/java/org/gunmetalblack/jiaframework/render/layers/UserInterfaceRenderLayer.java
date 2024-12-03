package org.gunmetalblack.jiaframework.render.layers;

import org.gunmetalblack.jiaframework.entity.Entity;
import org.gunmetalblack.jiaframework.render.RenderLayerName;

import java.awt.*;
import java.util.ArrayList;

public class UserInterfaceRenderLayer extends MainRenderLayer{
    private Entity[][] renderObjectsUI;
    public UserInterfaceRenderLayer(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
        renderObjectsUI = new Entity[maxRows][maxColumns];
        buildBoundingBox();
    }

    public void buildBoundingBox()
    {
        Entity boarderEntity =  new Entity('#', Color.GRAY, Color.BLACK);
        for(int i = 0; i < getMaxRows(); i++)
        {
            renderObjectsUI[i][0] = boarderEntity;
        }
        for(int i = 0; i < getMaxRows(); i++)
        {
            renderObjectsUI[i][getMaxColumns()-1] = boarderEntity;
        }
    }

    @Override
    public Entity[][] getEntitiesInLayerAsArray()
    {
        return renderObjectsUI;
    }
}
