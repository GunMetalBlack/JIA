package org.gunmetalblack.jiaframework.render.layers;

import org.gunmetalblack.jiaframework.entity.Entity;
import org.gunmetalblack.jiaframework.render.RenderLayerName;

import java.awt.*;
import java.util.ArrayList;

public class UserInterfaceRenderLayer extends MainRenderLayer{
    private Entity[][] renderObjectsUI;

    /**
     * A temperary Renderlayer WIP for building ui
     * @param layerName
     * @param renderObjects
     * @param maxColumns
     * @param maxRows
     */
    public UserInterfaceRenderLayer(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
        renderObjectsUI = new Entity[maxRows][maxColumns];
        buildBoundingBox();
        writeUIText(1,1, "Welcome to JIA-Sandbox!");
        writeUIText(1,3, "This is a simple sandbox game that represents a bigger project: JIA Engine.");
        writeUIText(1,5, "It is fully built with an entity system and a multi-layer render engine on");
        writeUIText(1,6,"top of a working event system!");
        writeUIText(1,8,"CONTROLS:");
        writeUIText(1,9,"W,A,S,D for movement!");
        writeUIText(1,10,"E to place blocks and Q to break blocks!");
        writeUIText(1,11,"Use the arrow keys to choose the direction of placement!");
    }

    /**
     * A utility function to write text within a ui layer very primitive
     * @param xPos x position within the array
     * @param yPos y position within the array
     * @param message the text you want to draw to the screen
     */
    public void writeUIText(int xPos, int yPos, String message)
    {
        for(char c : message.toCharArray())
        {
            if(yPos > renderObjectsUI[0].length){yPos++;}
            if(isWithinBounds(yPos,xPos))
            {
                Entity cha = new Entity(c, new Color(0xd0, 0x8c, 0x36), Color.BLACK);
                renderObjectsUI[yPos][xPos] = cha;
            }
            xPos++;
        }
    }

    /**
     * A utility function for checking if a x and y is within bounds of renderObjects
     * @param row
     * @param column
     * @return
     */
    private boolean isWithinBounds(int row, int column) {
        return row >= 0 && row < renderObjectsUI.length && column >= 0 && column < renderObjectsUI[0].length;
    }

    /**
     * Builds the rim around the UI layer
     */
    public void buildBoundingBox()
    {
        Entity boarderEntity =  new Entity('#', new Color(0x4e, 0x8d, 0xa4), Color.BLACK);
        for(int i = 0; i < getMaxRows(); i++)
        {
            renderObjectsUI[i][0] = boarderEntity;
        }
        for(int i = 0; i < getMaxRows(); i++)
        {
            renderObjectsUI[i][getMaxColumns()-1] = boarderEntity;
        }
        for(int i = 0; i < getMaxColumns(); i++)
        {
            renderObjectsUI[getMaxRows()-1][i] = boarderEntity;
        }
        for(int i = 0; i < getMaxColumns(); i++)
        {
            renderObjectsUI[0][i] = boarderEntity;
        }
    }

    @Override
    public Entity[][] getEntitiesInLayerAsArray()
    {
        return renderObjectsUI;
    }
}
