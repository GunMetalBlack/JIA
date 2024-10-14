package org.gunmetalblack.render;

import org.gunmetalblack.Init;
import org.gunmetalblack.entity.Entity;

import java.util.HashMap;

public class Render {
    private Init window;
    public HashMap<RenderLayerName,MainRenderLayer> layerToBeRendered = new HashMap<>();
    public static MainRenderLayer mainGameLayer;
    //TODO: Make a render stack that each layer is on
    public Render(Init window)
    {
        this.window = window;
        /*
         * Used To make a Main layer which all the sub-layers will be drawn on top of
         * */
        //Main Game layers and Sub layers --------------------------------------------------------------------------------------------------
        mainGameLayer = new MainRenderLayer(RenderLayerName.GAME_LAYER, Level.testLevel.getLevel(),40,30);
        createChildRenderLayer(mainGameLayer,RenderLayerName.GL_LIVING_ENTITY_LAYER, new Entity[mainGameLayer.getMaxColumns()][mainGameLayer.getMaxRows()]);
        layerToBeRendered.put(RenderLayerName.GAME_LAYER,mainGameLayer);
        //----------------------------------------------------------------------------------------------------------------------------------
    }


    public void renderLayerByName(RenderLayerName layerName)
    {
        for(MainRenderLayer layer : layerToBeRendered.values())
        {
            if(layer.getLayerName().equals(layerName))
            {
                //Renders the main render layer then renders the child layers on top!
                renderEntityArray(layer.GetEntitiesInLayer(),layer.getMaxColumns(), layer.getMaxRows());
                for(ChildRenderLayer childLayer : layer.getLayers().values())
                {
                    renderEntityArray(childLayer.GetEntitiesInLayer(),childLayer.getMaxColumns(),childLayer.getMaxRows());
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
                Entity entity = objectToBeRendered[i][j];
                if(entity != null){window.getTerminal().write(entity.getGraphic(), columns, rows);}


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

    public void createChildRenderLayer(MainRenderLayer mLayer,RenderLayerName name, Entity[][] renderObjects)
    {
        ChildRenderLayer cLayer = new ChildRenderLayer(name, renderObjects, mLayer.getMaxColumns(), mLayer.getMaxRows());
        mLayer.addChildLayer(name,cLayer);
    }
}
