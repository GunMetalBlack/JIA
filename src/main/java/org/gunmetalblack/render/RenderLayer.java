package org.gunmetalblack.render;

import org.gunmetalblack.entity.Entity;

import java.util.ArrayList;

public class RenderLayer{

    private ArrayList<Entity> renderObjects;
    private RenderLayerName layerName;

    private int maxColumns;
    private int maxRows;

    public RenderLayer(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows)
    {
        this.renderObjects = renderObjects;
        this.layerName = layerName;
        this.maxColumns = maxColumns;
        this.maxRows = maxRows;
        if(renderObjects.size() > (maxColumns * maxRows))
        {
            throw new Error("Catastrophic Render Error: the expected render size is exceeded by renderObjectSize!");
        }
    }

    public RenderLayerName getLayerName() {
        return layerName;
    }

    public void setLayerName(RenderLayerName layerName) {
        this.layerName = layerName;
    }


    public ArrayList<Entity> getEntitiesInLayer() {
        return renderObjects;
    }

    /*
    * This function converts the Array list into a 2d array for the use of rendering
    * Yes I know this is disgustingly inefficient but I don't have enough time to rework the rendering system
    * */
    public Entity[][] getEntitiesInLayerAsArray()
    {
        Entity[][] ar = new Entity[maxColumns][maxRows];
        System.out.println("maxColumns: " + maxColumns + ", maxRows: " + maxRows);
        for (Entity entity : getEntitiesInLayer()) {

            int x = entity.getxPos();
            int y = entity.getyPos();
            System.out.println("Entity position: x=" + x + ", y=" + y);
            }
            return ar;
        }



    public void setRenderObjects(ArrayList<Entity> renderObjects) {
        this.renderObjects = renderObjects;
    }

    public int getMaxColumns() {
        return maxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }

    public int getMaxRows() {
        return maxRows;
    }
}
