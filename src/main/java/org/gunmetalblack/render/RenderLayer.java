package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class RenderLayer{

    private Entity[][] renderObjects;
    private String layerName;
    public RenderLayer(String layerName, Entity[][] renderObjects)
    {
        this.renderObjects = renderObjects;
        this.layerName = layerName;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }


}
