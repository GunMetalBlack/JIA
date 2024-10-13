package org.gunmetalblack.render;

import org.gunmetalblack.Init;
import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class Render {
    private Init window;
    public ArrayList<MainRenderLayer> layerToBeRendered = new ArrayList<>();
    //TODO: Make a render stack that each layer is on
    public Render(Init window)
    {
        this.window = window;
        /*
         * Used To make a Main layer which all the sub-layers will be drawn on top of
         * */
        MainRenderLayer mainGameLayer = new MainRenderLayer("GameLayer", Level.testLevel.getLevel());
        layerToBeRendered.add(mainGameLayer);
    }
    //game screen 40 long and 30 wide

    public void createChildRenderLayer(MainRenderLayer mLayer,String name, Entity[][] renderObjects)
    {
        ChildRenderLayer cLayer = new ChildRenderLayer(name, renderObjects);
        mLayer.addLayer(cLayer);
    }
}
