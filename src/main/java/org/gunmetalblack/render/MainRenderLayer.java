package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainRenderLayer extends RenderLayer{

    private HashMap<RenderLayerName,ChildRenderLayer> layers = new HashMap<>();

    public MainRenderLayer(RenderLayerName layerName, Entity[][] renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
    }

    public ChildRenderLayer getChildLayer(RenderLayerName name) {
        return layers.get(name);
    }

    public HashMap<RenderLayerName, ChildRenderLayer> getLayers() {
        return layers;
    }

    public void addChildLayer(RenderLayerName name,ChildRenderLayer layer) {
        this.layers.put(name,layer);
    }
}
