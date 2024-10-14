package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class RenderLayer{

    private Entity[][] renderObjects;
    private String layerName;

    private int maxColumns;
    private int maxRows;

    public RenderLayer(String layerName, Entity[][] renderObjects, int maxColumns, int maxRows)
    {
        this.renderObjects = renderObjects;
        this.layerName = layerName;
        this.maxColumns = maxColumns;
        this.maxRows = maxRows;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }


    public Entity[][] getRenderObjects() {
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
