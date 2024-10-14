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
        MainRenderLayer mainGameLayer = new MainRenderLayer("GameLayer", Level.testLevel.getLevel(),40,30);
        layerToBeRendered.add(mainGameLayer);
    }


    public void renderLayerByName(String layerName)
    {
        for(MainRenderLayer layer : layerToBeRendered)
        {
            if(layer.getLayerName().equals(layerName))
            {
                //Renders the main render layer then renders the child layers on top!
                renderEntityArray(layer.getRenderObjects(),layer.getMaxColumns(), layer.getMaxRows());
                for(ChildRenderLayer childLayer : layer.getLayers())
                {
                    renderEntityArray(childLayer.getRenderObjects(),childLayer.getMaxColumns(),childLayer.getMaxRows());
                }
            }
        }
    }

    /*
       Renders a 2d array of entities based on a width and height
     */
    public void renderEntityArray(Entity[][] objectToBeRendered, int maxColumns, int maxRows)
    {
        int columns = 0;
        int rows = 0;

        for (int i = 0; i < objectToBeRendered.length; i++) {
            for (int j = 0; j < objectToBeRendered[i].length; j++) {
                window.getTerminal().write(objectToBeRendered[i][j].getGraphic(), columns, rows);

                // Move to the next column
                columns++;

                // Check if we've reached the maxColumns limit
                if (columns >= maxColumns) {
                    columns = 0;  // Reset to the first column
                    rows++;       // Move to the next row
                }

                // Check if we've reached the maxRows limit
                if (rows >= maxRows) {
                    return;  // Exit if we've reached the row limit
                }
            }
        }
    }

    public void createChildRenderLayer(MainRenderLayer mLayer,String name, Entity[][] renderObjects)
    {
        ChildRenderLayer cLayer = new ChildRenderLayer(name, renderObjects, mLayer.getMaxColumns(), mLayer.getMaxRows());
        mLayer.addLayer(cLayer);
    }
}
