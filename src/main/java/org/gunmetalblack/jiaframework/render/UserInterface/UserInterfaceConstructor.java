package org.gunmetalblack.jiaframework.render.UserInterface;

import org.gunmetalblack.jiaframework.entity.Entity;
import org.gunmetalblack.jiaframework.render.RenderLayerName;
import org.gunmetalblack.jiaframework.render.layers.UserInterfaceRenderLayer;

import java.util.ArrayList;
import java.util.List;

public class UserInterfaceConstructor extends UserInterfaceRenderLayer {
    public List<UIElement> UIObject = new ArrayList<>();
    /**
     * A temperary Renderlayer WIP for building ui
     *
     * @param layerName
     * @param renderObjects
     * @param maxColumns
     * @param maxRows
     */
    public UserInterfaceConstructor(RenderLayerName layerName, ArrayList<Entity> renderObjects, int maxColumns, int maxRows) {
        super(layerName, renderObjects, maxColumns, maxRows);
    }
    
    /**
     * Goals to create a UI that can be fully customizable and something that can be updated in real time
     * Needs to be editable in segments and still comply to an overalll element
     * Create a class for UI elements and A list to save the UI "FRAMES" and each of these can use the UI from for single elements
     * Only redraw if an update has occured -> Find a way to store and save UI elements -> Create a builder for UI
     * **/

}
