package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class MainRenderLayer extends RenderLayer{
    private ArrayList<RenderLayer> layers;

    public MainRenderLayer(String layerName, Entity[][] renderObjects) {
        super(layerName, renderObjects);
    }

    public ArrayList<RenderLayer> getLayers() {
        return layers;
    }

    public void addLayer(RenderLayer layer) {
        this.layers.add(layer);
    }
}
