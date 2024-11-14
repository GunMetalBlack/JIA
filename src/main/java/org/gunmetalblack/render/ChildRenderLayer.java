package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

public class ChildRenderLayer extends RenderLayer{
    private RenderLayer ParentLayer;
    public ChildRenderLayer(RenderLayerName layerName, Entity[][] renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
    }

    public RenderLayer getParentLayer() {
        return ParentLayer;
    }

    public void setParentLayer(RenderLayer getParentLayer) {
        this.ParentLayer = getParentLayer;
    }
}
