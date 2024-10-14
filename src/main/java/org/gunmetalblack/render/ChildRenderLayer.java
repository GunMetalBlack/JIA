package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

public class ChildRenderLayer extends RenderLayer{
    public ChildRenderLayer(RenderLayerName layerName, Entity[][] renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
    }
}
