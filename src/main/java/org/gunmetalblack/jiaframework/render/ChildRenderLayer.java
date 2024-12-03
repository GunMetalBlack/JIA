package org.gunmetalblack.jiaframework.render;

import org.gunmetalblack.jiaframework.entity.Entity;

import java.util.ArrayList;

public class ChildRenderLayer extends RenderLayer{
    private RenderLayer ParentLayer;
    public ChildRenderLayer(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
    }

    public RenderLayer getParentLayer() {
        return ParentLayer;
    }

    public void setParentLayer(RenderLayer getParentLayer) {
        this.ParentLayer = getParentLayer;
    }
}
