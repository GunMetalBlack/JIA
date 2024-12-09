package org.gunmetalblack.jiaframework.render.layers;

import org.gunmetalblack.jiaframework.entity.Entity;
import org.gunmetalblack.jiaframework.render.RenderLayerName;

import java.util.ArrayList;

public class ChildRenderLayer extends RenderLayer {
    /**
     * Acts as a child to the main render layer allowing for convenient structer
     */
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
