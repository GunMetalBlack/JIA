package org.gunmetalblack.jiaframework.render.layers;

import org.gunmetalblack.jiaframework.entity.Entity;
import org.gunmetalblack.jiaframework.render.RenderLayerName;

import java.util.ArrayList;
import java.util.HashMap;

public class FrameBufferRenderLayer extends MainRenderLayer {
    private Entity[][] renderObjectsFrameBuffer;

    private HashMap<RenderLayerName, ChildRenderLayer> layers = new HashMap<>();

    public FrameBufferRenderLayer(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
        renderObjectsFrameBuffer = new Entity[maxRows][maxColumns];
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

    @Override
    public Entity[][] getEntitiesInLayerAsArray()
    {
         return renderObjectsFrameBuffer;
    }
}
