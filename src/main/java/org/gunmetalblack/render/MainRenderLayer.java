package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class MainRenderLayer extends RenderLayer{
    private ArrayList<ChildRenderLayer> layers = new ArrayList<>();

    public MainRenderLayer(String layerName, Entity[][] renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
    }

    public ArrayList<ChildRenderLayer> getLayers() {
        return layers;
    }

    public void addLayer(ChildRenderLayer layer) {
        this.layers.add(layer);
    }
}
