package org.gunmetalblack.jiaframework.render;

import org.gunmetalblack.jiaframework.JIAWindow;
import org.gunmetalblack.jiaframework.entity.Entity;
import org.gunmetalblack.jiaframework.render.layers.ChildRenderLayer;
import org.gunmetalblack.jiaframework.render.layers.FrameBufferRenderLayer;
import org.gunmetalblack.jiaframework.render.layers.MainRenderLayer;
import org.gunmetalblack.jiaframework.render.layers.UserInterfaceRenderLayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The {@code Render} class manages the rendering process for various game layers and their child layers.
 * It supports rendering both to a window and to a framebuffer, allowing for complex multi-layer rendering.
 */
public class Render {
    private JIAWindow window;
    public HashMap<RenderLayerName, MainRenderLayer> layerToBeRendered = new HashMap<>();
    public static MainRenderLayer mainGameLayer;
    public static FrameBufferRenderLayer frameBuffer;
    public static UserInterfaceRenderLayer horizontalUILayer;

    /**
     * Constructs a new {@code Render} object.
     *
     * @param window the initialization object representing the game window.
     */
    public Render(JIAWindow window) {
        this.window = window;
        /*
         * Initializes the framebuffer and the main game layer with sub-layers.
         */
        frameBuffer = new FrameBufferRenderLayer(RenderLayerName.FRAME_BUFFER, new ArrayList<Entity>(), 80, 60);
        mainGameLayer = new MainRenderLayer(RenderLayerName.GAME_LAYER, Level.testLevel.getLevel(), 40, 30);
        horizontalUILayer = new UserInterfaceRenderLayer(RenderLayerName.UI_HORIZONTAL_LAYER, new ArrayList<Entity>(),80,20);
        createChildRenderLayer(mainGameLayer, RenderLayerName.GL_LIVING_ENTITY_LAYER, new ArrayList<Entity>());
        layerToBeRendered.put(RenderLayerName.GAME_LAYER, mainGameLayer);
        layerToBeRendered.put(RenderLayerName.UI_HORIZONTAL_LAYER,horizontalUILayer);
    }

    /**
     * Renders the specified main render layer and its child layers by layer name.
     *
     * @param layerName the name of the layer to render.
     */
    public void renderMainLayerAndChildrenByName(RenderLayerName layerName) {
        for (MainRenderLayer layer : layerToBeRendered.values()) {
            if (layer.getLayerName().equals(layerName)) {
                // Renders the main layer and its child layers
                renderEntityArray(layer.getEntitiesInLayerAsArray(), layer.getMaxColumns(), layer.getMaxRows());
                for (ChildRenderLayer childLayer : layer.getLayers().values()) {
                    renderEntityArray(childLayer.getEntitiesInLayerAsArray(), childLayer.getMaxColumns(), childLayer.getMaxRows());
                }
            }
        }
    }

    /**
     * Renders the specified entity array to the framebuffer.
     *
     * @param objectToBeRendered the 2D array of entities to render.
     * @param maxColumns the maximum number of columns to render.
     * @param maxRows the maximum number of rows to render.
     */
    public void renderToFramebuffer(Entity[][] objectToBeRendered, int xOffset, int yOffset) {
        for (int i = 0; i < objectToBeRendered.length; i++) {
            for (int j = 0; j < objectToBeRendered[i].length; j++) {
                Entity entity = objectToBeRendered[i][j];
                if (entity != null) {
                    //REMEMBER THIS IS JANK AND CAUSED
                    frameBuffer.getEntitiesInLayerAsArray()[i + yOffset][j + xOffset] = entity;
                }
            }
        }
    }

    /**
     * Renders all main layers and their child layers to the framebuffer.
     */
    public void renderAllLayersToFramebuffer() {
        clearFramebuffer();

        // Render each main layer and its children
        for (MainRenderLayer layer : layerToBeRendered.values()) {
            if(layer.getLayerName() == RenderLayerName.UI_HORIZONTAL_LAYER) {
                renderToFramebuffer(layer.getEntitiesInLayerAsArray(), 0, mainGameLayer.getMaxRows());
            }
            else{renderToFramebuffer(layer.getEntitiesInLayerAsArray(), 0, 0);}

            for (ChildRenderLayer childLayer : layer.getLayers().values()) {
                renderToFramebuffer(childLayer.getEntitiesInLayerAsArray(), 0, 0);
            }
        }
    }

    public void renderFrameBufferToWindow()
    {
        renderEntityArray(frameBuffer.getEntitiesInLayerAsArray(), frameBuffer.getMaxColumns(), frameBuffer.getMaxRows());
    }

    /**
     * Clears the framebuffer by setting all cells to {@code null}.
     */
    public void clearFramebuffer() {
        Entity[][] entities = frameBuffer.getEntitiesInLayerAsArray();
        for (int i = 0; i < entities.length; i++) {
            for (int j = 0; j < entities[i].length; j++) {
                entities[i][j] = null;
            }
        }
    }

    /**
     * Renders a specific layer by its name. First attempts to render a main layer, then searches within child layers.
     *
     * @param layerName the name of the layer to render.
     */
    public void renderLayerByName(RenderLayerName layerName) {
        boolean layerFound = false;

        for (MainRenderLayer layer : layerToBeRendered.values()) {
            if (layer.getLayerName().equals(layerName)) {
                renderEntityArray(layer.getEntitiesInLayerAsArray(), layer.getMaxColumns(), layer.getMaxRows());
                layerFound = true;
                break;
            }
        }

        if (!layerFound) {
            for (MainRenderLayer layer : layerToBeRendered.values()) {
                for (ChildRenderLayer childLayer : layer.getLayers().values()) {
                    if (childLayer.getLayerName().equals(layerName)) {
                        renderEntityArray(childLayer.getEntitiesInLayerAsArray(), childLayer.getMaxColumns(), childLayer.getMaxRows());
                        layerFound = true;
                        break;
                    }
                }
                if (layerFound) break;
            }
        }

        if (!layerFound) {
            System.out.println("Layer not found: " + layerName);
        }
    }

    /**
     * Renders a 2D array of entities based on the given maximum number of columns and rows.
     *
     * @param objectToBeRendered the 2D array of entities to render.
     * @param maxColumns the maximum number of columns to render.
     * @param maxRows the maximum number of rows to render.
     */
    public void   renderEntityArray(Entity[][] objectToBeRendered, int maxColumns, int maxRows) {
        int columns = 0;
        int rows = 0;

        for (int i = 0; i < objectToBeRendered.length; i++) {
            for (int j = 0; j < objectToBeRendered[i].length; j++) {
                Entity entity = objectToBeRendered[i][j];
                if (entity != null) {
                    window.getTerminal().write(entity.getGraphic(), columns, rows);
                }

                columns++;

                if (columns >= maxColumns) {
                    columns = 0;
                    rows++;
                }

                if (rows >= maxRows) {
                    return;
                }
            }
        }
    }

    /**
     * Creates a new child render layer and adds it to the specified main render layer.
     *
     * @param mLayer the main render layer to which the child layer will be added.
     * @param name the name of the child layer.
     * @param renderObjects the 2D array of entities for the child layer.
     */
    public void createChildRenderLayer(MainRenderLayer mLayer, RenderLayerName name, ArrayList<Entity> renderObjects) {
        ChildRenderLayer cLayer = new ChildRenderLayer(name, renderObjects, mLayer.getMaxColumns(), mLayer.getMaxRows());
        cLayer.setParentLayer(mLayer);
        mLayer.addChildLayer(name, cLayer);
    }
}
