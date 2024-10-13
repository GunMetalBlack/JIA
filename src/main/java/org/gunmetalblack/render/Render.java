package org.gunmetalblack.render;

import org.gunmetalblack.Init;
import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;
import java.util.Stack;

public class Render {
    private Init window;
    public ArrayList<MainRenderLayer> layerToBeRendered;
    //TODO: Make a render stack that each layer is on
    public Render(Init window)
    {
        this.window = window;
    }
    //game screen 40 long and 30 wide
    public void createMainRenderLayer(String name, Entity[][] renderObjects)
    {
        MainRenderLayer mLayer = new MainRenderLayer(name,renderObjects);
    }
    public void createChildRenderLayer()
    {

    }
}
