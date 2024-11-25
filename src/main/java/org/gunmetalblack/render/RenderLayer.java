package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class RenderLayer{

    private ArrayList<Entity> renderObjects;
    private RenderLayerName layerName;

    private int maxColumns;
    private int maxRows;

    public RenderLayer(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows)
    {
        this.renderObjects = renderObjects;
        this.layerName = layerName;
        this.maxColumns = maxColumns;
        this.maxRows = maxRows;
        if(renderObjects.size() > (maxColumns * maxRows))
        {
            throw new Error("Catastrophic Render Error: the expected render size is exceeded by renderObjectSize!");
        }
    }

    public RenderLayerName getLayerName() {
        return layerName;
    }

    public void setLayerName(RenderLayerName layerName) {
        this.layerName = layerName;
    }


    public ArrayList<Entity> getEntitiesInLayer() {
        return renderObjects;
    }

    public void setRenderObjects(ArrayList<Entity> renderObjects) {
        this.renderObjects = renderObjects;
    }

    public int getMaxColumns() {
        return maxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }

    public int getMaxRows() {
        return maxRows;
    }
}
