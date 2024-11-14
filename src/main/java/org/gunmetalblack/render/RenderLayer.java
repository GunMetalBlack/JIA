package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

public class RenderLayer{

    private Entity[][] renderObjects;
    private RenderLayerName layerName;

    private int maxColumns;
    private int maxRows;

    public RenderLayer(RenderLayerName layerName, Entity[][] renderObjects, int maxColumns, int maxRows)
    {
        this.renderObjects = renderObjects;
        this.layerName = layerName;
        this.maxColumns = maxColumns;
        this.maxRows = maxRows;
    }

    public RenderLayerName getLayerName() {
        return layerName;
    }

    public void setLayerName(RenderLayerName layerName) {
        this.layerName = layerName;
    }


    public Entity[][] getEntitiesInLayer() {
        return renderObjects;
    }

    public void setRenderObjects(Entity[][] renderObjects) {
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
